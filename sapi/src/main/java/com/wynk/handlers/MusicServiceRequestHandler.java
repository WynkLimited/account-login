package com.wynk.handlers;

import com.wynk.common.*;
import com.wynk.enums.*;
import com.wynk.exceptions.OTPAuthorizationException;
import com.wynk.server.ChannelContext;
import com.wynk.service.*;
import com.wynk.user.dto.UserActivityEvent;
import com.wynk.utils.*;
import com.wynk.wcf.WCFApisConstants;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.*;

import static com.wynk.constants.JsonKeyNames.*;
import static com.wynk.wcf.WCFApisConstants.*;

/**
 * Created with IntelliJ IDEA.
 * User: bhuvangupta
 * Date: 21/11/13
 * Time: 1:47 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller("/music/(v1|v2|v3|v4)/.*")
public class MusicServiceRequestHandler implements IUrlRequestHandler, IAuthenticatedUrlRequestHandler {

    private Logger logger = LoggerFactory.getLogger(getClass().getCanonicalName());
    private static final Logger mactivityLogger = LoggerFactory.getLogger("mactivityanalytics");

    @Autowired
    private MusicService musicService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountUrlRequestHandler accountUrlRequestHandler;

    @Autowired
    private MusicAdminServiceRequestHandler musicAdminServiceRequestHandler;

    @Autowired
    private UserAuthorizationService userAuthorizationService;

    @Override
    public boolean authenticate(String requestUri, String requestPayload, HttpRequest request) throws PortalException {

        //todo: implement logic to check URLs to be excluded.
        return userAuthorizationService.authenticate(request, requestUri, requestPayload);
    }

    @Override
    public HttpResponse handleRequest(String requestUri, String requestPayload, HttpRequest request) throws PortalException, OTPAuthorizationException {

        long startTime = System.currentTimeMillis();

        try {
            if (logger.isInfoEnabled() && !requestUri.contains("headers") && !requestUri.contains("appsFlyerStats") && !requestUri.contains("fingerprintmatch")) {
                logger.info("Received request " + requestUri + " with payload " + requestPayload + " : " + request.getMethod());
            } else if (requestUri.contains("appsFlyerStats")) {
                logger.trace("Received request " + requestUri + " with payload " + requestPayload + " : " + request.getMethod());
            }

            //            if (!requestUri.contains("headers"))
            //                System.out.println("Received request " + requestUri + " with payload " + requestPayload + " : " + request.getMethod());

            if (requestUri.contains("/v1/account") || requestUri.contains("/v2/account"))
                return accountUrlRequestHandler.handleRequest(requestUri, requestPayload, request);

            if (requestUri.contains("/v1/admin"))
                return musicAdminServiceRequestHandler.handleRequest(requestUri, requestPayload, request);

            //Handling CORS - OPTIONS METHOD

            if (request.getMethod() == HttpMethod.OPTIONS) {
                return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            }

            //POST METHODS
            if ((request.getMethod() == HttpMethod.POST) || (request.getMethod() == HttpMethod.PUT)) {
                if (requestUri.matches("/music/(v1|v2)/statsnonauth.*")) {
                    return handleStatsNoAuth(requestUri, requestPayload, request);
                } else if (requestUri.matches("/music/(v1|v2)/statsnouser.*")) {
                    return handleStatsNoUser(requestUri, requestPayload, request);
                } else if (requestUri.matches("/music/(v1|v2|v3)/itunesSubscription.*")) {
                    if (StringUtils.isBlank(requestPayload))
                        return HTTPUtils.createResponse("Invalid request Payload from the App", HttpResponseStatus.INTERNAL_SERVER_ERROR);
                    return HTTPUtils.createOKResponse(musicService.verifyPaymentReceipt(requestPayload, PaymentCode.ITUNES).toJSONString());
                } else if (requestUri.matches("/music/(v1|v2)/config.*")) {
                    return getConfigForRoaming(requestUri, request, requestPayload,false);
                } else if (requestUri.matches("/music/v3/config.*")) {
                    return getConfigForRoaming(requestUri, request, requestPayload,true);
                } else if (requestUri.matches("/music/v1/initWcfWebView.*")) {
                    return initWCFWebView(requestUri, request);
                } else
                    return HTTPUtils.createResponse("Invalid POST Request : " + requestUri, HttpResponseStatus.BAD_REQUEST);
                //music/v1/ipayycb
            }

            //GET METHODS
            if (request.getMethod() == HttpMethod.GET) {
                if (requestUri.matches("/music/(v1|v2)/featured.*")) {
                    return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
                    // return getFeaturedContent(requestUri, request, false);
                } else if (requestUri.matches("/music/(v1|v2)/collections.*")) {
                    return getUserCollections(requestUri, request);
                } else if ((requestUri.matches("/music/(v1|v2)/stats/status.*"))) {
                    return HTTPUtils.createOKResponse("{\"status\":\"ok\"}");
                } else if (requestUri.matches("/music/(v1|v2)/config.*")) {
                    return getConfig(requestUri, request);
                } else
                    return HTTPUtils.createResponse("Invalid GET Request : " + requestUri, HttpResponseStatus.BAD_REQUEST);
            }

            if (request.getMethod() == HttpMethod.DELETE) {
                return HTTPUtils.createResponse("Invalid DELETE Request : " + requestUri, HttpResponseStatus.BAD_REQUEST);
            }
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        } finally {
            long apiTime = System.currentTimeMillis() - startTime;
            //if(musicConfig.isEnableStatsD())
            //    musicMonitoringService.timing(requestUri,(int)apiTime);
            logger.info("API Time for " + requestUri + " : " + apiTime);
        }
    }

    private HttpResponse getConfigForRoaming(String requestUri, HttpRequest request, String requestPayload,boolean isEnrcypted) {
        if (requestUri.toLowerCase().contains(accountService.OFFLINE_SUBSCRIPTION_INTENT.toLowerCase())) {
            //TODO - Send offline intent to WCF to create intent logs
        }

        String archType = null;
        JSONObject simInfo = new JSONObject();

        try {
            if (StringUtils.isNotBlank(requestPayload)) {
                simInfo = (JSONObject) JSONValue.parseWithException(requestPayload);
            }
        } catch (ParseException e) {
            LogstashLoggerUtils.createCriticalExceptionLog(e, ExceptionTypeEnum.CODE.name(), ChannelContext.getUid(), "MusicServiceRequestHandler.getConfigForRoaming");
            e.printStackTrace();
        }

        try {
            Map<String, List<String>> urlParameters = HTTPUtils.getUrlParameters(requestUri);
            archType = HTTPUtils.getStringParameter(urlParameters, "archType");
        } catch (PortalException e) {
            LogstashLoggerUtils.createCriticalExceptionLog(e, ExceptionTypeEnum.CODE.name(), ChannelContext.getUid(), "MusicServiceRequestHandler.getConfig");
        }

        JSONObject jsonObject = musicService.getConfigData(request, true, archType, true, simInfo, false, false,isEnrcypted);

        return HTTPUtils.createOKResponse(jsonObject.toJSONString());
    }

    private HttpResponse handleStatsNoAuth(String requestUri, String requestPayload, HttpRequest request) {
        logger.error("statsnonauth Payload = " + requestPayload);
        String responseStr = JsonUtils.EMPTY_JSON_STR;
        return HTTPUtils.createOKResponse(responseStr);
    }

    private HttpResponse handleStatsNoUser(String requestUri, String requestPayload, HttpRequest request) {
        String responseStr = JsonUtils.EMPTY_JSON_STR;
        JSONObject eventJson;
        try {
            eventJson = (JSONObject) JSONValue.parseWithException(requestPayload);
            UserActivityEvent event = new UserActivityEvent();
            event.fromJsonObject(eventJson);
            if (event.getTimestamp() <= 0)
                event.setTimestamp(System.currentTimeMillis());
            else {
                //apps send timestamp in seconds
                event.setTimestamp(MusicUtils.getTimestampInMS(event.getTimestamp()));
            }
            event.setAppType(MusicUtils.getRequestSourceType().getName());
            String eventLog = musicService.createEventLogNoAuth(event, request);
            mactivityLogger.info(eventLog);
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("Error while calling handleStatsNoUser", e);
        }

        return HTTPUtils.createOKResponse(responseStr);
    }

    private HttpResponse getConfig(String requestUri, HttpRequest request) {
        if (requestUri.toLowerCase().contains(accountService.OFFLINE_SUBSCRIPTION_INTENT.toLowerCase())) {
            //TODO - Send offline intent to WCF to create intent logs
        }

        String archType = null;
        try {
            Map<String, List<String>> urlParameters = HTTPUtils.getUrlParameters(requestUri);
            archType = HTTPUtils.getStringParameter(urlParameters, "archType");
        } catch (PortalException e) {
            LogstashLoggerUtils.createCriticalExceptionLog(e, ExceptionTypeEnum.CODE.name(), ChannelContext.getUid(), "MusicServiceRequestHandler.getConfig");
        }

        JSONObject jsonObject = musicService.getConfigData(request, true, archType, true, null, false, false,false);

        return HTTPUtils.createOKResponse(jsonObject.toJSONString());
    }

    private HttpResponse getUserCollections(String requestUri, HttpRequest request) throws PortalException {
        String json = musicService.getUserCollections();
        HttpResponse response = HTTPUtils.createOKResponse(json);
        return response;
    }

    private HttpResponse initWCFWebView(String requestUri, HttpRequest request) {
        WCFPackGroup packGroup = null;
        Theme theme = null;
        String planId = null;
        WCFView view = null;
        Intent intent = null;
        String freeTextIntent = null;
        try {
            Map<String, List<String>> queryParams = HTTPUtils.getUrlParameters(requestUri);
            if (queryParams.containsKey(WCFApisConstants.INTENT) && queryParams.get(WCFApisConstants.INTENT).size() > 0) {
                intent = Intent.getIntent(queryParams.get(WCFApisConstants.INTENT).get(0));
                switch (intent) {
                    case HELLOTUNE:
                    case SpecialHT:
                        packGroup = WCFPackGroup.WYNK_HT;
                        break;
                    case MY_ACCOUNT: //packGroup should be null for settings page webView
                        break;
                    default:
                        packGroup = WCFPackGroup.WYNK_MUSIC;
                }
                /*
                it was requested to make intent as free text
                i.e don't send default
                not removing old handling. Client sends hellotune and we send HELLOTUNE :(
                Adding fix on top.
                */
                freeTextIntent = intent.name();
                if (intent.equals(Intent.UNKNOWN)) {
                    freeTextIntent = queryParams.get(WCFApisConstants.INTENT).get(0);
                }
            }
            if (queryParams.containsKey(THEME) && queryParams.get(THEME).size() > 0) {
                theme = Theme.getTheme(queryParams.get(THEME).get(0));
            }
            if (queryParams.containsKey(PLAN_ID) && queryParams.get(PLAN_ID).size() > 0) {
                planId = queryParams.get(PLAN_ID).get(0);
            }
            if (queryParams.containsKey(VIEW) && queryParams.get(VIEW).size() > 0) {
                view = WCFView.getView(queryParams.get(VIEW).get(0));
            }
        } catch (Exception e) {
            return HTTPUtils.createResponse(e.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }

        JSONObject responseObj = musicService.forwardInitWCFWebViewRequest(packGroup, theme, planId, view, freeTextIntent);
        logger.info("Response from WCF : {} for request : {}", responseObj.toString(), request.toString());
        if (responseObj.get(ERROR) != null)
            return HTTPUtils.createResponse((String) responseObj.get(ERROR), HttpResponseStatus.INTERNAL_SERVER_ERROR);
        return HTTPUtils.createOKResponse(responseObj.toJSONString());
    }
}
