package com.wynk.handlers;

import com.google.gson.*;
import com.wynk.common.PackProviderAuthRequest;
import com.wynk.common.PackProviderAuthResponse;
import com.wynk.common.PortalException;
import com.wynk.config.MusicConfig;
import com.wynk.constants.JsonKeyNames;
import com.wynk.constants.MusicConstants;
import com.wynk.constants.MusicSubscriptionPackConstants;
import com.wynk.dto.MusicSubscriptionState;
import com.wynk.dto.PromoCode;
import com.wynk.music.dto.*;
import com.wynk.notification.MusicAdminNotification;
import com.wynk.server.HttpResponseService;
import com.wynk.service.*;
import com.wynk.sms.SMSService;
import com.wynk.user.dto.User;
import com.wynk.utils.HTTPUtils;
import com.wynk.utils.JsonUtils;
import com.wynk.utils.Utils;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by bhuvangupta on 04/02/14.
 */
@SuppressWarnings("unchecked")
@Controller("/music/v1/admin/.*")
public class MusicAdminServiceRequestHandler implements IUrlRequestHandler {

    private Logger logger = LoggerFactory.getLogger(getClass().getCanonicalName());

    @Autowired
    PackProviderManagementService packProviderManagementService;
    
    @Autowired
    private MusicConfig musicConfig;
    
    @Autowired
    private AccountService accountService;
    

    @Override
    public HttpResponse handleRequest(String requestUri, String requestPayload, HttpRequest request)
            throws PortalException {
        if (logger.isInfoEnabled()) {
            logger.info("Received request " + requestUri + " with payload " + requestPayload);
        }

        try {
            //GET METHODS
            if(request.getMethod() == HttpMethod.GET)
            {
                Map<String, List<String>> urlParameters = HTTPUtils.getUrlParameters(requestUri);
                if ( requestUri.contains("/music/v1/admin/user_info")) {
                	PackProviderAuthRequest appAuthRequest = HTTPUtils.createApplicationAuthRequestFrom(requestUri, requestPayload, request);
                    PackProviderAuthResponse appAuthResponse = packProviderManagementService._authenticate(appAuthRequest);
                    boolean authenticated = appAuthResponse.isAuthenticated();
                
                    logger.info("authenticating  user_info request " + appAuthRequest +" response " +appAuthResponse+" status " + authenticated);
                    
                    if (!authenticated)
                        return HTTPUtils.createResponse(StringUtils.EMPTY, HttpResponseStatus.FORBIDDEN);
                    
                    return getUserRegistrationInfo(urlParameters);
                    
                }
            }
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Failed to process request", ex);
            return HTTPUtils.createResponse("Failed to process request: " + ex.getMessage(), "text/plain", HttpResponseStatus.INTERNAL_SERVER_ERROR);

        }
    }

    private HttpResponse getUserRegistrationInfo(Map<String,List<String>> urlParameters) {
    	String userid =  HTTPUtils.getStringParameter(urlParameters, "uid");
    	User user = accountService.getUserFromUid(userid, false);
        JSONObject jsonObject = new JSONObject();
        if (user != null) {
        	jsonObject.put("registrationDate", user.getCreationDate());
        	jsonObject.put("uid", user.getUid());
        	jsonObject.put("msisdn", user.getMsisdn());
        	String activeDevice = "";
        	if (user.getActiveDevice() != null)
        		activeDevice = user.getActiveDevice().getDeviceId();
        	jsonObject.put("activeDeviceId", activeDevice);
        }
        return HTTPUtils.createOKResponse(jsonObject.toJSONString());
    }
}
