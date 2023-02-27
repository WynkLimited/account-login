package com.wynk.handlers;

import com.google.gson.Gson;
import com.wynk.common.PortalException;
import com.wynk.config.MusicConfig;
import com.wynk.server.HttpResponseService;
import com.wynk.service.AccountService;
import com.wynk.utils.WCFUtils;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


@Controller("/music/external/v1/account.*")
public class OutsideRequestHandler implements IUrlRequestHandler,IAuthenticatedUrlRequestHandler{

    private Logger logger = LoggerFactory.getLogger(getClass().getCanonicalName());

    @Autowired
    private AccountService accountService;

    @Autowired
    private MusicConfig musicConfig;

    @Autowired
    private WCFUtils wcfUtils;

    public static final String X_BSY_ATKN = "x-bsy-atkn";
    public static final String X_BSY_DATE = "x-bsy-date";

    private Gson gson = new Gson();

    @Override
    public boolean authenticate(String requestUri, String requestPayload, HttpRequest request) throws PortalException {
        HttpMethod httpMethod = request.getMethod();

        String timestamp = request.headers().get(X_BSY_DATE);
        String atken = request.headers().get(X_BSY_ATKN);

        if(StringUtils.isBlank(timestamp) || StringUtils.isBlank(atken)){
            logger.info("Authentication failiure for " + requestUri + " with payload " + requestPayload);
            return false;
        }

        String[] parts = atken.split(":");
        String signature = parts[1];
        if(requestPayload == null){
            requestPayload="";
        }

        String calculatedString = wcfUtils.createSignature(httpMethod.name(),requestUri,"",timestamp,musicConfig.getExternalHandlerAuthenicationCode());
        logger.info("calculated signature : "+calculatedString);
        if(StringUtils.isNotBlank(calculatedString) && calculatedString.equalsIgnoreCase(signature)){
            return true;
        }
        logger.info("Authentication failiure for " + requestUri + " with payload " + requestPayload);
        return false;
    }

    @Override
    public HttpResponse handleRequest(String requestUri, String requestPayload, HttpRequest request) throws PortalException {
        if (logger.isInfoEnabled()) {
            logger.info("Received request for External CallBack" + requestUri + " with payload " + requestPayload);
        }

        if(request.getMethod() == HttpMethod.GET) {
            //no get method as of now
        }
        else if(request.getMethod().equals(HttpMethod.POST)){
            if(requestUri.contains("/external/v1/account/sendSmsText")) {
                return sendSMSWithText(requestPayload, request);
            }
        }
        return new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
    }

    private HttpResponse sendSMSWithText(String requestPayload, HttpRequest request) throws PortalException {
        if(StringUtils.isEmpty(requestPayload))
            return HttpResponseService
                    .createResponse(AccountService.createErrorResponse("BSY007", "Empty request").toJSONString(),
                            HttpResponseStatus.NO_CONTENT);

        JSONObject requestJson = accountService.getJsonObjectFromPayload(requestPayload);

        JSONObject responseJson = accountService.sendSMSWithText(requestJson);
        if(!responseJson.containsKey("error")) {
            return HttpResponseService.createOKResponse(responseJson.toJSONString());
        }
        else {
            return HttpResponseService.createResponse(responseJson.toJSONString(), HttpResponseStatus.BAD_REQUEST);
        }
    }
}
