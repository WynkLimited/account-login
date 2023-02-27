package com.wynk.service;

import com.wynk.common.ExceptionTypeEnum;
import com.wynk.constants.Constants;
import com.wynk.dto.*;
import com.wynk.music.dto.GeoLocation;
import com.wynk.server.ChannelContext;
import com.wynk.server.HttpResponseService;
import com.wynk.utils.JsonUtils;
import com.wynk.utils.LogstashLoggerUtils;
import com.wynk.utils.MusicDeviceUtils;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.wynk.constants.Constants.RequestHeaders.X_BSY_DID;

/**
 * @author : Kunal Sharma
 * @since : 25/09/22, Sunday
 **/

@Service
public class PaymentService {

    @Autowired
    private WCFService wcfService;

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public HttpResponse initializePayRequest(String requestPayload, HttpRequest request) {
        try {
            JSONObject requestPayloadJson = null;
            if (StringUtils.isBlank(requestPayload)) {
                // payload is empty | error code response
                return CustomResponseService.getGenericFailureResponse();
            }

            if (StringUtils.isNotBlank(requestPayload)) {
                try {
                    requestPayloadJson = (JSONObject) JSONValue.parseWithException(requestPayload);
                } catch (ParseException e) {
                    LogstashLoggerUtils.createCriticalExceptionLog(e,
                            ExceptionTypeEnum.CODE.name(),
                            ChannelContext.getUid(),
                            "PaymentService.initializePayRequest");
                }
            }
            logger.info("Request body received is {}", requestPayloadJson);

            PaymentRequest paymentRequest = PaymentRequest.createPaymentRequest(requestPayload);
            addAppDetails(paymentRequest, request, requestPayloadJson);
            UserDetailsDto userDetails = UserDetailsDto.builder()
                    .uid(ChannelContext.getUid())
                    .msisdn(ChannelContext.getMsisdn()).build();
            paymentRequest.setUserDetails(userDetails);
            addGeoLocation(request, paymentRequest);

            logger.info("preparedObject for wcf is {}", paymentRequest.toJson());

            LogstashLoggerUtils.logExecutionStep(paymentRequest.toJson(), ChannelContext.getUid(), ChannelContext.getMsisdn(), "ToWCF","");

            WcfResponse<VerifyReceiptResponseV3> response = wcfService.verifyPaymentReceiptV3(paymentRequest.toJson());
            logger.info("Response received in svc is : {}", response.toString());
            if (response.isSuccess() && response.getData() != null) {
                logger.info("Received success is {}", response.isSuccess());
                VerifyReceiptResponseV3 data = response.getData();
                PaymentResponse paymentResponse = PaymentResponse.builder()
                        .payload(new PaymentResponse.Payload(data.getPageDetails().getPageUrl(), Boolean.TRUE))
                        .timestamp(new Date().getTime())
                        .build();
                String outputString = JsonUtils.GSON.toJson(paymentResponse);
                logger.info("Returning success is {}", outputString);
                return HttpResponseService.createOKResponse(outputString);
            }
        } catch (Exception e) {
            logger.info("An exception occurred {}", e.getMessage(), e);
            LogstashLoggerUtils.createCriticalExceptionLog(e, ExceptionTypeEnum.CODE.name(), ChannelContext.getUid(), "PaymentService.initializePayRequest");
        }

        return CustomResponseService.getGenericFailureResponse();

    }

    private void addAppDetails(PaymentRequest paymentRequest, HttpRequest request, JSONObject requestJson) {

//        logger.info("Header was for mobility is {}", request.headers().get("x-bsy-appid-mobility"));
        Map<Object, Object> map = MusicDeviceUtils.parseMusicHeaders(request.headers().get(X_BSY_DID));
        map.put("packageName", requestJson.get("packageName"));
        map.put("service", "music");
        map.put("appId", "mobility");
        paymentRequest.setAppDetails(map);
    }

    private static void addGeoLocation(HttpRequest request, PaymentRequest paymentRequest) {

        logger.info("Geo found from cloudfront  is {} , {} , {} ", request.headers().get(Constants.GEO_LOCATION_HEADER.CloudFront_Viewer_Country)
                , request.headers().get(Constants.GEO_LOCATION_HEADER.CloudFront_Viewer_Country_Region),
                request.headers().get(Constants.GEO_LOCATION_HEADER.CloudFront_Viewer_Address).trim().split(":")[0]);

        GeoLocation location = new GeoLocation()
                ._setCountryCode(request.headers().get(Constants.GEO_LOCATION_HEADER.CloudFront_Viewer_Country))
                ._setStateCode(request.headers().get(Constants.GEO_LOCATION_HEADER.CloudFront_Viewer_Country_Region))
                ._setIp(request.headers().get(Constants.GEO_LOCATION_HEADER.CloudFront_Viewer_Address).trim().split(":")[0]);
        paymentRequest.setGeoLocation(location);
    }
}
