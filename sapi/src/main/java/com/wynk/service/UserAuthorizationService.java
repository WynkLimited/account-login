package com.wynk.service;

import com.wynk.common.ExceptionTypeEnum;
import com.wynk.common.PortalException;
import com.wynk.config.MusicConfig;
import com.wynk.constants.MusicConstants;
import com.wynk.music.dto.MusicPlatformType;
import com.wynk.server.ChannelContext;
import com.wynk.user.dto.User;
import com.wynk.utils.*;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SignatureException;
import java.util.*;

/**
 * Created by Aakash on 07/07/17.
 */
@Service
public class UserAuthorizationService {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthorizationService.class.getCanonicalName());

    private static final Set<String> AUTHENTICATION_BYPASS_URLS = new HashSet<String>();
    private static final Set<String> NO_AUTHENTICATION_REQUIRED_URLS = new HashSet<String>();
    private static final Set<String> NO_AUTHENTICATION_REQUIRED_URLS_TEMP = new HashSet<String>();
    private static final Map<String, String> secretMap = new HashMap<>();

    @Autowired
    private MusicConfig musicConfig;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ClientAuthorizationService clientAuthorizationService;
    static {
        secretMap.put("543fbd6f96644406567079c00d8f33dc", "50de5a601c133a29c8db434fa9bf2db4");
        secretMap.put("arsenal_service", "n56y7zPeVZ7q39F8KtxXh6ddmXUQeS4B");
        secretMap.put("com.wynk.discoverios", "2wkcndjkjhdb23422cdnm435mneamxhdk");
        secretMap.put("in.bsb.myairtel.inhouse", "2wkcndjkjhdb23422cdnm435mneamxhdk");
        secretMap.put("com.BhartiMobile.myairtel", "2wkcndjkjhdb23422cdnm435mneamxhdk");
        secretMap.put("com.myairtelapp.debug", "2wkcndjkjhdb23422cdnm435mneamxhdk");
        secretMap.put("com.myairtelapp", "2wkcndjkjhdb23422cdnm435mneamxhdk");
        secretMap.put("com.airtel.sample", "2wkcndjkjhdb23422cdnm435mneamxhdk");
    }
    static {
        AUTHENTICATION_BYPASS_URLS.add("/songscore");
        AUTHENTICATION_BYPASS_URLS.add("/unisearch");
        AUTHENTICATION_BYPASS_URLS.add("/jackpot");
        AUTHENTICATION_BYPASS_URLS.add("/account/headers");
        AUTHENTICATION_BYPASS_URLS.add("/stats");
        AUTHENTICATION_BYPASS_URLS.add("/clearcache");
        AUTHENTICATION_BYPASS_URLS.add("/offercachepurge");
        AUTHENTICATION_BYPASS_URLS.add("/ipayyconfirmoperator");
        AUTHENTICATION_BYPASS_URLS.add("/operatorconfirmation");
        AUTHENTICATION_BYPASS_URLS.add("/ipayyconfirmotp");
        AUTHENTICATION_BYPASS_URLS.add("/doipayypayment");
        AUTHENTICATION_BYPASS_URLS.add("/resendotp");
        AUTHENTICATION_BYPASS_URLS.add("/debugUnisearch");
        AUTHENTICATION_BYPASS_URLS.add("/debugSearch");
        AUTHENTICATION_BYPASS_URLS.add("/clearservercache");
        AUTHENTICATION_BYPASS_URLS.add("/appsFlyerStats");
        AUTHENTICATION_BYPASS_URLS.add("/wcfcb");
        AUTHENTICATION_BYPASS_URLS.add("/statsnonauth");
        AUTHENTICATION_BYPASS_URLS.add("/statsnouser");
        AUTHENTICATION_BYPASS_URLS.add("/fetchSpotlightImages");
        AUTHENTICATION_BYPASS_URLS.add("/uploadSpotlightImages");
        AUTHENTICATION_BYPASS_URLS.add("music/v3/trendingsearch");
        AUTHENTICATION_BYPASS_URLS.add("music/v4/trendingsearch");
        AUTHENTICATION_BYPASS_URLS.add("/(v2|v3)/account/sendSmsText");
        AUTHENTICATION_BYPASS_URLS.add("/music/wcf/postPaymentData");
        AUTHENTICATION_BYPASS_URLS.add("/music/wcf/refreshPacks");
        AUTHENTICATION_BYPASS_URLS.add("/music/wcf/getInMemoryProductIds");
    }

    static {
        NO_AUTHENTICATION_REQUIRED_URLS_TEMP.add("/config");
    }

    static {
        NO_AUTHENTICATION_REQUIRED_URLS.add("/music/(v1|v2|v3)/account/login.*");
        NO_AUTHENTICATION_REQUIRED_URLS.add("/music/(v1|v2|v3)/account/ifb.*");
        NO_AUTHENTICATION_REQUIRED_URLS.add("/music/(v1|v2|v3)/account[^/]*");
        NO_AUTHENTICATION_REQUIRED_URLS.add("/music/(v1|v2|v3)/account/otp");
        NO_AUTHENTICATION_REQUIRED_URLS.add("/music/v1/account/otp/generate");
        NO_AUTHENTICATION_REQUIRED_URLS.add("/music/(v1|v2|v3)/account/requestOtpCall");
        NO_AUTHENTICATION_REQUIRED_URLS.add("/music/(v1|v2)/acdcgwcallback.*");
        NO_AUTHENTICATION_REQUIRED_URLS.add("/music/(v1|v2)/wcdcgwcallback.*");
        NO_AUTHENTICATION_REQUIRED_URLS.add("/music/(v1|v2)/cdcgwcallback.*");
        NO_AUTHENTICATION_REQUIRED_URLS.add("/music/(v1|v2)/paytmcb");
        NO_AUTHENTICATION_REQUIRED_URLS.add("/jackpot");
        NO_AUTHENTICATION_REQUIRED_URLS.add("/music/(v1|v2)/wcfcb");
        NO_AUTHENTICATION_REQUIRED_URLS.add("/music/wcf/refreshPacks");
        NO_AUTHENTICATION_REQUIRED_URLS.add("/music/wcf/getInMemoryProductIds");
    }

    public boolean authenticate(HttpRequest request,String requestUri,String requestPayload)
    {
        // In case of chrome cast ( Cross domain call) we recieve OPTIONS calls before a POST call for validation
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return true;
        }

        //bypass authentication for certain url's for speeding
        if (StringUtils.isNotBlank(requestUri)){
            for (String bypass_url : AUTHENTICATION_BYPASS_URLS){
                if (requestUri.contains(bypass_url)){
                    return true;
                }
            }
        }


        long startTime = System.currentTimeMillis();

        boolean authenticated = _authenticate(request, requestUri, requestPayload);
        boolean verifiedClient = clientAuthorizationService.authenticateClient(request, requestUri);

        logger.info("Time taken to authenticate user:" + (System.currentTimeMillis()-startTime) + "Auth status : " + request.getMethod().name() + " : "+ requestUri+" : Client verification status: " + verifiedClient+", Auth Status : "+authenticated);

        if (!authenticated) {
            return false;
        }

        if (!verifiedClient) {
            return false;
        }

        if (UserDeviceUtils.isThirdPartyPlatformRequest(request)) {
            boolean verifiedPlatform = authenticatePlatform(request);
            if (!verifiedPlatform)
                return false;
        }

        return true;
    }

    private boolean _authenticate(HttpRequest request, String requestUri, String requestPayload) {
        String wapID = request.headers().get(MusicConstants.MUSIC_HEADER_WAPID);
        String uid = null;
        boolean authenticated = false;

        if (StringUtils.isNotEmpty(wapID)) {

            String wapUID = request.headers().get(MusicConstants.MUSIC_HEADER_WAP);
            uid = wapUID;
            authenticated = authenticateUsingWapUID(wapUID, request, requestUri, requestPayload);

        } else {
            String isWap = request.headers().get(MusicConstants.MUSIC_HEADER_IS_WAP);
            String userToken = request.headers().get("x-bsy-utkn");
            String apiToken = request.headers().get("x-bsy-atkn");
            Map<String, List<String>> params;

            try {
                params = HTTPUtils.getUrlParameters(requestUri);
            } catch (PortalException e) {
                params = null;
            }

            String basicAuth = request.headers().get(MusicConstants.HEADER_AUTHORIZATION);
            if(basicAuth != null) {
                if(requestUri.contains("galaxy") && basicAuth.equalsIgnoreCase(MusicConstants.BASIC_AUTH_HEADER_SAMSUNG_GTM)){
                    updateBasicAuthenticatedUsersData(request, true);
                    authenticated = true;
                }
            }else if(StringUtils.isNotBlank(apiToken)){
                authenticated = authenticateUsingApiToken(apiToken, request, requestUri, requestPayload);
                uid = UserUtils.getUidFromRequest(request);
            } else if (HTTPUtils.getStringParameter(params, "u") != null) {

                // Replace this with not empty check
                uid = MusicUtils.decryptParamFromRequestUri(musicConfig.getEncryptionKey(), requestUri, "u");
                authenticated = authenticateUsingUIDParam(uid);
            } else if (StringUtils.isNotEmpty(userToken)) {

                authenticated = authenticateUsingUserToken(userToken, request, requestUri, requestPayload);
                ChannelContext.setUtknContext(userToken);
                uid = ChannelContext.getUid();
            } else if (requestUri.contains(MusicConstants.WAP_OTP_URI)) {
                updateBasicAuthenticatedUsersData(request, false);
                authenticated = true;
            }
            // uid = setUserInContextFromMsisdn(request);
            if (StringUtils.isNotBlank(requestUri)){
                for (String bypass_url : NO_AUTHENTICATION_REQUIRED_URLS){
                    if (requestUri.matches(bypass_url) && !(StringUtils.isNotBlank(isWap) && requestUri.contains("otp"))){
                        authenticated = true;
                    }
                }
            }

            if (StringUtils.isNotBlank(requestUri)){
                for (String bypass_url : NO_AUTHENTICATION_REQUIRED_URLS_TEMP){
                    if (requestUri.contains(bypass_url)){
                        authenticated = true;
                    }
                }
            }

        }
        if (authenticated && uid != null && StringUtils.isNotEmpty(uid)) {
            accountService.updateAuthenticatedUsersData(uid, request);
        }
        logger.info("Authenticating " + requestUri + " for uid : " + ChannelContext.getUid() + ", status : " + authenticated);
        return authenticated;
    }

    /**
     * Verified WAP clients are all authenticated
     */
    private boolean authenticateUsingWapUID(String wapUid, HttpRequest request, String requestUri, String requestPayload) {
        return true;
    }

    private void updateBasicAuthenticatedUsersData(HttpRequest request, boolean isSamsungGTM) {

        String msisdn = null;
        Map<String, List<String>> params;
        if(isSamsungGTM){
            msisdn = UserDeviceUtils.getMsisdnFromBsyHeader(request);
        }
        else if(request.uri().contains(MusicConstants.WAP_OTP_URI)){
            try{
            msisdn  = HTTPUtils.getStringParameter(HTTPUtils.getUrlParameters(request.uri()), "msisdn");
            } catch (PortalException e) {
                logger.error("Exception occurred while getting msisdn from url param : {}",e);
                msisdn = null;
            }
        }
        else{
            msisdn = UserDeviceUtils.getMsisdn(request);
        }

        if(!StringUtils.isEmpty(msisdn))
            ChannelContext.setMsisdn(msisdn);
        else {
            return;
        }

        String uid = null;
        try {
            MusicPlatformType platform = request.uri().contains(MusicConstants.WAP_OTP_URI) ? MusicPlatformType.WYNK_APP : MusicPlatformType.SAMSUNG_SDK;
            uid = UserDeviceUtils.generateUUID(msisdn, null, ChannelContext.getRequest(), platform);
        } catch (Exception e) {
            logger.info("Error generating UUID from msisdn  AccountService.updateMSISDNAuthenticatedUsersData " + msisdn);
        }
        ChannelContext.setUid(uid);
        MDC.put("uid", uid);

        String circleShortName 	= null;
        User appUser 			= accountService.getAppUserFromUID(uid, request);

        if (UserDeviceUtils.isWAPUser(uid)) {
            ChannelContext.setUser(appUser);
            return;
        }

        if (appUser != null) {
            circleShortName = appUser.getCircle();
        } else {
            logger.info("No user object in DB for UID : " + ChannelContext.getUid());
        }

        ChannelContext.setCircle(circleShortName);
    }

    private boolean authenticateUsingUIDParam(String uid) {
        if(StringUtils.isBlank(uid)){
            return false;
        }
        return true;
    }

    private boolean authenticateUsingUserToken(String userToken, HttpRequest request, String requestUri,
                                               String requestPayload) {

        boolean authenticate 	= false;
        String[] parts 			= userToken.split(":");
        String uid 				= parts[0];
        String signature 		= parts[1];
        String data 			= request.getMethod() + requestUri;

        if (!StringUtils.isEmpty(requestPayload))
            data += requestPayload;
        String token = accountService.getTokenFromLru(uid);

        // TODO - Remove the below line, added as fallback for non migrated users.
        ChannelContext.setUid(uid);
        String deviceId = request.headers().get("x-bsy-did");
        ChannelContext.setDeviceId(deviceId);


        if (StringUtils.isEmpty(token)) {
            if (!musicConfig.isEnableAuth()) {
                return true;
            }
            return authenticate;
        }

        try {
            String generatedSig = EncryptUtils.calculateRFC2104HMAC(data, token);
            authenticate 		= generatedSig.equalsIgnoreCase(signature);
            ChannelContext.setUid(uid);
            logger.info("UID : " + uid + " ;Signature : " + signature + " ;url : " + data + " ;token : " + token
                    + " ;generatedSig : " + generatedSig + " ;deviceId : " + deviceId);
        } catch (SignatureException e) {
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, ExceptionTypeEnum.CODE.name(), uid,
                    "AccountService._authenticate", "Error authenticating, invalid signature");
            e.printStackTrace();
            logger.error("Error authenticating : " + e.getMessage(), e);
        }
        if (!musicConfig.isEnableAuth()) {
            return true;
        }
        return authenticate;
    }

    private boolean authenticateUsingApiToken(String apiToken, HttpRequest request, String requestUri,
                                               String requestPayload) {

        boolean authenticate 	= false;
        String[] parts 			= apiToken.split(":");
        String appId 				= parts[0];
        String signature 		= parts[1];

        String data 			= request.getMethod() + requestUri;
        if (!StringUtils.isEmpty(requestPayload))
            data += requestPayload;
        String ts = request.headers().get("x-bsy-date");
        if(StringUtils.isNotBlank(ts)){
            data += ts;
        }

        String token = secretMap.get(appId);
        try {
            String generatedSig = EncryptUtils.calculateRFC2104HMAC(data, token);
            authenticate 		= generatedSig.equalsIgnoreCase(signature);
            logger.info("AppId : " + appId + " ;Signature : " + signature + " ;url : " + data + " ;token : " + token
                    + " ;generatedSig : " + generatedSig + " ;deviceId : ");

        } catch (Exception e) {
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, ExceptionTypeEnum.CODE.name(), appId,
                    "AccountService._authenticate", "Error authenticating, invalid signature");
            logger.error("Error authenticating : " + e.getMessage(), e);
        }
        return authenticate;
    }

    private boolean authenticatePlatform(HttpRequest request) {
        MusicPlatformType platform = UserDeviceUtils.getUserPlatformFromRequest(request);
        String secret = "";
        if (platform == MusicPlatformType.SAMSUNG_SDK)
            secret = musicConfig.getSamsungSDKKey();
        String token = Utils.getSha1Hash(platform.getName() + secret);
        String tokenHeader = getPlatormToken(request);
        if (token.equals(tokenHeader))
            return true;

        return false;
    }

    public String getPlatormToken(HttpRequest request) {
        String platformToken = request.headers().get("x-bsy-ptkn");
        if(!StringUtils.isEmpty(platformToken)) {
            return platformToken;
        }
        return null;
    }
}
