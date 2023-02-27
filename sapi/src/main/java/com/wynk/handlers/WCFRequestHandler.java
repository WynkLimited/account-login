package com.wynk.handlers;

import com.google.gson.Gson;
import com.wynk.common.ExceptionTypeEnum;
import com.wynk.common.FlowId;
import com.wynk.common.PortalException;
import com.wynk.config.MusicConfig;
import com.wynk.db.MongoDBManager;
import com.wynk.dto.AccountWCFUpgradeResponseDTO;
import com.wynk.dto.WCFSubscription;
import com.wynk.enums.PaymentCode;
import com.wynk.music.WCFServiceType;
import com.wynk.music.constants.MusicRequestSource;
import com.wynk.server.ChannelContext;
import com.wynk.service.*;
import com.wynk.service.api.WCFApiService;
import com.wynk.user.dto.User;
import com.wynk.user.dto.UserEntityKey;
import com.wynk.utils.*;
import com.wynk.wcf.WCFApisService;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wynk.constants.MusicConstants.USER_COLLECTION;

/**
 * Created by aakashkumar on 10/11/16.
 */
@Controller("/music/wcf.*")
public class WCFRequestHandler implements IUrlRequestHandler,IAuthenticatedUrlRequestHandler{

    private Logger logger = LoggerFactory.getLogger(getClass().getCanonicalName());

    @Autowired
    private WCFService wcfService;

    @Autowired
    private MusicService musicService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private WCFApiService wcfApiService;

    @Autowired
    private MyAccountService myAccountService;

    @Autowired
    private UserAuthorizationService userAuthorizationService;

    @Autowired
    private WCFApisService wcfApisService;

    @Override
    public boolean authenticate(String requestUri, String requestPayload, HttpRequest request) throws PortalException {
        return userAuthorizationService.authenticate(request, requestUri, requestPayload);
    }

    @Override
    public HttpResponse handleRequest(String requestUri, String requestPayload, HttpRequest request) throws PortalException {
        if (logger.isInfoEnabled()) {
            logger.info("Received request for WCF CallBack" + requestUri + " with payload " + requestPayload);
        }

        if (request.getMethod() == HttpMethod.OPTIONS) {
            if (requestUri.matches("/music/wcf/postPaymentData.*")) {
                return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            }
        } else if (request.getMethod() == HttpMethod.GET) {
            if(requestUri.matches("/music/wcf/subscribePack.*")) {
                return subscribeValidPack(requestUri, request,Boolean.FALSE);
            }
            else if(requestUri.matches("/music/wcf/v2/subscribePack.*")){
                return subscribeValidPack(requestUri,request,Boolean.TRUE);
            }
            else if(requestUri.matches("/music/wcf/displaySubscription.*")){
                return displaySubscriptionPlan(requestUri,request);
            }
            else if(requestUri.matches("/music/wcf/refreshPacks.*")){
                return refreshPacks();
            } else if(requestUri.matches("/music/wcf/getInMemoryProductIds*")){
                return getInMemoryProductIds();
            }
        } else if (request.getMethod() == HttpMethod.POST) {
            if (requestUri.matches("/music/wcf/postPaymentData.*")) {
                return postPaymentData(requestPayload);
            } else if (requestUri.matches("/music/wcf/googleCb.*") || requestUri.matches("/music/wcf/v1/googleCb.*")) {
                if (StringUtils.isBlank(requestPayload))
                    return HTTPUtils.createResponse("Invalid request Payload from the App", HttpResponseStatus.INTERNAL_SERVER_ERROR);
                return HTTPUtils.createOKResponse(musicService.verifyPaymentReceipt(requestPayload, PaymentCode.GOOGLE_WALLET).toJSONString());
            }
        }
        return new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
    }

    private HttpResponse subscribeValidPack(String requestUri, HttpRequest request, Boolean isNewVersion) throws PortalException {
        AccountWCFUpgradeResponseDTO accountWCFUpgradeResponseDTO = wcfService.subscribePack(requestUri, request);
        if (isNewVersion) {
            return HTTPUtils.createOKResponse(wcfService.createResponseForNewSubscription(accountWCFUpgradeResponseDTO).toJSONString());
        }
        String redirectUris = accountWCFUpgradeResponseDTO.getUrl();
        return HTTPUtils.createRedirectResponse(redirectUris);
    }

    public HttpResponse displaySubscriptionPlan(String requestUri, HttpRequest request){
        return myAccountService.displaySubscriptionPage(request);
    }

    private HttpResponse postPaymentData(String requestPayload) {
        logger.info("[POST Request from WCF WebView] payload : " + requestPayload);
        return wcfApisService.setSidAndPlanIdInRedis(requestPayload);
    }

    private HttpResponse refreshPacks() {
        JSONObject jsonObject =  wcfApisService.refreshProducts();
        LogstashLoggerUtils.createAccessLogLite("REFRESH_PACKS",jsonObject.toJSONString(),"NA");
        return HTTPUtils.createOKResponse(jsonObject.toJSONString());
    }

    private HttpResponse getInMemoryProductIds() {
        JSONObject jsonObject =  wcfApisService.getInMemoryProductIds();
        LogstashLoggerUtils.createAccessLogLite("GET_IN_MEMORY_PRODUCT_IDS",jsonObject.toJSONString(),"NA");
        return HTTPUtils.createOKResponse(jsonObject.toJSONString());
    }

}
