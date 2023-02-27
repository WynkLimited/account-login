package com.wynk.handlers;

import com.wynk.common.*;
import com.wynk.config.MusicConfig;
import com.wynk.constants.*;
import com.wynk.db.MongoDBManager;
import com.wynk.dto.MusicSubscriptionStatus;
import com.wynk.exceptions.OTPAuthorizationException;
import com.wynk.user.dto.User;
import com.wynk.service.MusicTwilioCallService;
import com.wynk.server.ChannelContext;
import com.wynk.server.HttpResponseService;
import com.wynk.service.*;
import com.wynk.utils.*;
import com.wynk.music.dto.MusicPlatformType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.netty.handler.codec.http.*;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import java.util.*;

import static com.wynk.constants.MusicConstants.REDIS_USER_UID_KEY;
import static com.wynk.constants.MusicConstants.REDIS_USER_UID_KEY_TTL_IN_SEC;

/**
 * Created with IntelliJ IDEA. User: bhuvangupta Date: 21/11/13 Time: 1:47 AM To change this
 * template use File | Settings | File Templates.
 */
@Controller("/music/(v1|v2|v3)/account.*")
public class AccountUrlRequestHandler implements IUrlRequestHandler, IAuthenticatedUrlRequestHandler {


    private Logger logger = LoggerFactory.getLogger(getClass().getCanonicalName());

    @Autowired
    private MusicConfig musicConfig;

    @Autowired
    private AccountService                   accountService;

    @Autowired
    private WCFService wcfService;

    @Autowired
    private MusicService                     musicService;

    @Autowired
    private MusicTwilioCallService twilioService;

    @Autowired
    MongoDBManager mongoMusicDBManager;

    @Autowired
    MyAccountService myAccountService;

    @Autowired
    private UserAuthorizationService userAuthorizationService;

    @Autowired
    private AccountRegistrationService accountRegistrationService;

    @Autowired
    private WCFUtils wcfUtils;

    @Autowired
    private SubscriptionIntentService subscriptionIntentService;

    @Autowired
    private OtpService otpService;

    private Gson gson   = new GsonBuilder().disableHtmlEscaping().create();

    private final static Map<String, Integer> ibmOfferPackByPrice;
    private final static Map<String, Integer> ibmNonOfferPackByPrice;

    private static final long SIX_MONTHS_IN_MILLIS = 6*30*24*60*60*1000L;

    private static final Logger subscriptionLogger         = LoggerFactory.getLogger("subscriptionLogger");

    private static final String LANGUAGES = "languages";

    static{
        Map<String, Integer> temp = new HashMap<>();
        temp.put("" + MusicSubscriptionPackConstants.AIRTEL_MUSIC_PACK_PRICE, MusicSubscriptionPackConstants.IBM_OFFER_MUSIC_PACK_ID_29);
        temp.put("" + MusicSubscriptionPackConstants.AIRTEL_FREE_DATA_MUSIC_PACK_PRICE, MusicSubscriptionPackConstants.IBM_OFFER_MUSIC_PACK_ID_99);
        ibmOfferPackByPrice = Collections.unmodifiableMap(temp);
        temp = new HashMap<>();
        temp.put("" + MusicSubscriptionPackConstants.AIRTEL_MUSIC_PACK_PRICE, MusicSubscriptionPackConstants.IBM_MUSIC_PACK_ID_29);
        temp.put("" + MusicSubscriptionPackConstants.AIRTEL_FREE_DATA_MUSIC_PACK_PRICE, MusicSubscriptionPackConstants.IBM_MUSIC_PACK_ID_99);
        ibmNonOfferPackByPrice = Collections.unmodifiableMap(temp);
    }

    @Override
    public boolean authenticate(String requestUri, String requestPayload, HttpRequest request) throws PortalException {

        return userAuthorizationService.authenticate(request, requestUri, requestPayload);
    }

    @Override
    public HttpResponse handleRequest(String requestUri, String requestPayload, HttpRequest request) throws PortalException, OTPAuthorizationException {

        if (logger.isInfoEnabled() && !requestUri.contains("headers")) {
            logger.info("Received request " + requestUri + " with payload " + requestPayload);
        }

        if(request.getMethod() == HttpMethod.OPTIONS){
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        }

        // POST METHODS
        if(request.getMethod() == HttpMethod.POST) {
            if(requestUri.matches("/music/v1/account/login.*")) {
                return loginOrCreateUser(requestPayload, request, false, requestUri,false);
            }
            if(requestUri.matches("/music/v2/account/s2s/login.*")) {
                return loginAndUpdate(requestPayload, request, true);
            }
            // blocking old WAP users
            if(requestUri.matches("/music/v3/account/login.*")) {
                return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
                //return loginOrCreateWapUser(requestPayload, request);
            }
            else if(requestUri.matches("/music/v2/account/login.*")) {
                return loginOrCreateUser(requestPayload, request, true, requestUri,false);
            }
            else if(requestUri.contains("/v1/account/otp")) {
                return generatePin(requestPayload, request, false,false);
            }
            else if(requestUri.contains("/v2/account/otp")) {
                return generatePin(requestPayload, request, true,false);
            }
            else if(requestUri.contains("/v3/account/otp")) {
                return generatePin(requestPayload, request, true,true);
            }
            else if(requestUri.contains("/v1/account/validate/otp")) {
                return validateOtp(requestPayload, request, requestUri);
            }
            else if(requestUri.contains("/v2/account/requestOtpCall")) {
                return generateOtpCall(requestPayload, request, true, false);
            }
            else if(requestUri.contains("/v3/account/requestOtpCall")) {
                return generateOtpCall(requestPayload, request, true, true);
            }
            else if(requestUri.matches("/music/(v1|v2)/account/profile.*")) {
                return updateUserProfile(requestPayload, request);
            }
            else if(requestUri.matches("/music/v2/account/s2s/profile.*")) {
              return updateUserProfile(requestPayload, request);
            }
            else if(requestUri.matches("/music/(v1|v2)/account/avatar.*")) {
                return updateUserAvatar(requestPayload, request);
            }
            else if(requestUri.matches("/music/v2/account/chromecast.*")) {
                return createChromecastUser(requestPayload, request);
            }
            else if (requestUri.equalsIgnoreCase("/music/v1/account/delete")) {
                return accountService.deleteUserAccount(requestPayload, request);
            }
            else if (requestUri.equalsIgnoreCase("/music/v1/account/partner/login")) {
                return accountService.getLoginInfo(requestPayload, request);
            }
            else if(requestUri.matches("/music/v1/account.*")) {
                return loginOrCreateUser(requestPayload, request, false, requestUri,false);
            }
            else if(requestUri.matches("/music/v2/account.*")) {
                return loginOrCreateUser(requestPayload, request, true, requestUri,false);
            }
            else if(requestUri.matches("/music/v3/account/claim/benefit.*")){
                return claimBenefit(request, requestUri, requestPayload);
            }
            else if(requestUri.matches("/music/v3/account.*")) {
                return loginOrCreateUser(requestPayload, request, true, requestUri,true);
            }
        }

        // GET METHODS
        if(request.getMethod() == HttpMethod.GET) {
            if(requestUri.contains("musicsubscriptionstatus")) {
                return getMusicSubscription(requestUri, requestPayload, request);
            }else if(requestUri.matches("/music/v1/account/s2s/getmsisdn.*")){
                return getMsisdn(requestUri);
            } else if(requestUri.matches("/music/v1/account/s2s/validate.*")) {
                return validateMusicUser(requestUri, request);
            }else if(requestUri.matches("/music/v1/account/s2s/ispaiduser.*")) {
                return isPaidUserResponse(requestUri, request);
            }else if(requestUri.matches("/music/v1/account/s2s/offercachepurge.*")) {
                return cachePurge(requestUri, request);
            }
            else if(requestUri.matches("/music/v2/account/promocode/request.*")) {
                return HttpResponseService.createOKResponse(new JSONObject().toJSONString());
            }
            else if(requestUri.matches("/music/(v1|v2)/account/profile.*")) {
                return getUserProfile(request);
            }
            else if(requestUri.matches("/music/(v1|v2)/account/detectmsisdn.*")) {
            	return detectMsisdn(requestUri, request);
            }
            else if(requestUri.matches("/music/v1/account/getPacks.*")){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(JsonKeyNames.RECOMMENDED_PACKS,new JSONArray());
                return HttpResponseService.createOKResponse(jsonObject.toJSONString());
            }
            else if(requestUri.matches("/music/v1/account/otp/generate.*")) {
                return generatePinExternalWeb(requestUri);
            }
            else if(requestUri.matches("/music/v1/account/s2s/circle.*")) {
                return getCircle(requestUri);
            }
            else if(requestUri.matches("/music/v1/account/s2s/langsByCircle.*")) {
                return getOrderedLanguages(requestUri);
            }
            else if(requestUri.matches("/music/v1/account/s2s/onboardingLangs.*")) {
                return getOnboardingLanguages(requestUri);
            }
            else if(requestUri.matches("/music/v1/account/s2s/token.*")) {
                return getUserToken(request, requestUri, requestPayload);
            }
            else if(requestUri.matches("/music/(v1|v2)/account.*")) {
                return getAccountInfo(requestUri, request);
            }
        }

        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
    }

    private HttpResponse validateOtp(String requestPayload, HttpRequest request, String requestUri) throws PortalException, OTPAuthorizationException {
        JSONObject requestJson = null;
        if (StringUtils.isNotBlank(requestPayload)) {
            try {
                requestJson = (JSONObject) JSONValue.parseWithException(requestPayload);
            } catch (ParseException e) {
                LogstashLoggerUtils.createCriticalExceptionLog(e,
                        ExceptionTypeEnum.CODE.name(),
                        ChannelContext.getUid(),
                        "AccountUrlRequestHandler.validateOtp");

                throw new PortalException("Error parsing request : " + e.getMessage(), e);
            }
        }
        boolean success = false;
        JSONObject response = new JSONObject();
        String msisdnRequest = (String) requestJson.getOrDefault(JsonKeyNames.MSISDN, Constants.EMPTY_STRING);
        String uid = request.headers().get("x-bsy-utkn").split(":")[0];
        String intent = (String) requestJson.getOrDefault(JsonKeyNames.INTENT, Constants.EMPTY_STRING);
        String otp = (String) requestJson.getOrDefault(JsonKeyNames.OTP, Constants.EMPTY_STRING);
        String countryCode = (String) requestJson.getOrDefault("countryCode", Constants.EMPTY_STRING);
        Country country = Country.getCountryByCountryCode(countryCode);
        User user = accountService.getUserFromContext(uid);
        if (Objects.nonNull(user) && user.getMsisdn().equals(Utils.normalizePhoneNumber(msisdnRequest, country))) {
            success = accountService.validateOtp(msisdnRequest, otp, intent, country);
            /*  To avoid cases where
             *  after validation user does not
             *  fire delete request immediately */
            // if user validated successfully then add key in redis with tll of 6 minutes giving buffer
            if (success) {
                logger.info("Validate Otp is success {}", success);
                otpService.setValueInDbWithExpiryTime(REDIS_USER_UID_KEY + uid, uid, REDIS_USER_UID_KEY_TTL_IN_SEC);
                logger.info("Setted time in redis db is {} , {}, {}", REDIS_USER_UID_KEY_TTL_IN_SEC, REDIS_USER_UID_KEY + uid, uid);
            }
        }
        logger.info("Result of Validate Otp is {}", success);
        response.put("success", success);
        return HttpResponseService.createOKJsonResponse(response);
    }

    private HttpResponse getUserToken(HttpRequest request, String requestUri, String requestPayload) throws PortalException {
        String uid = HTTPUtils.getStringParameter(HTTPUtils.getUrlParameters(requestUri), "uid");
        logger.info("prepared uid for search is {}",uid);
        User user = accountService.getUserFromContext(uid);
        if (user != null && user.getToken() != null) {
            logger.info("Found user token is {}", user.getToken());
        } else {
            logger.info("User null, so token should be null");
        }
        JSONObject response = new JSONObject();
        response.put(MusicConstants.TOKEN, user.getToken());
        return HttpResponseService.createOKJsonResponse(response);
    }

    private HttpResponse claimBenefit(HttpRequest request, String requestUri, String requestPayload) {
        JSONObject wcfResponse = wcfService.claimBenefit(requestPayload);
        if (Objects.isNull(wcfResponse) || StringUtils.isBlank(wcfResponse.toJSONString())) {
            HttpResponseService.createResponse(
                    HttpResponseService.createErrorResponse("500", "Internal Server Error").toJSONString(),
                    HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
        return HttpResponseService.createOKJsonResponse(wcfResponse);
    }


    private HttpResponse getOnboardingLanguages(String requestUri) throws PortalException{
        String uid = HTTPUtils.getStringParameter(HTTPUtils.getUrlParameters(requestUri), "uid");
        List<String> onboardingLangs = accountService.getOnboardingLanguagesFromUid(uid);
        JSONObject response = new JSONObject();
        response.put(LANGUAGES, onboardingLangs);
        return HttpResponseService.createOKJsonResponse(response);
    }

    private HttpResponse getOrderedLanguages(String requestUri) throws PortalException {
        String circle = HTTPUtils.getStringParameter(HTTPUtils.getUrlParameters(requestUri), "circle");
        List<String> languagesOrderedByCircle = accountService.getLanguagesOrderByCircle(circle);
        JSONObject response = new JSONObject();
        response.put(LANGUAGES, languagesOrderedByCircle);
        return HttpResponseService.createOKJsonResponse(response);
    }

    private HttpResponse getCircle(String requestUri) throws PortalException {
        String msisdn = HTTPUtils.getStringParameter(HTTPUtils.getUrlParameters(requestUri), "msisdn");
        return HttpResponseService.createOKResponse(accountService.getCircleFromMsisdn(msisdn).toJSONString());
    }

    private HttpResponse getMsisdn(String requestUri) throws PortalException {
        String uid = HTTPUtils.getStringParameter(HTTPUtils.getUrlParameters(requestUri), "uid");
        return HttpResponseService.createOKResponse(accountService.getMsisdnFromUid(uid).toJSONString());
    }

    private HttpResponse validateMusicUser(String requestUri, HttpRequest request) throws PortalException {
        String uid = HTTPUtils.getStringParameter(HTTPUtils.getUrlParameters(requestUri), "uid");
        String deviceId = HTTPUtils.getStringParameter(HTTPUtils.getUrlParameters(requestUri), "did");
        JSONObject response = new JSONObject();
        if (StringUtils.isNotEmpty(uid) && StringUtils.isNotEmpty(deviceId)) {
            response = accountService.validateMusicUser(uid, deviceId);
        } else {
            response.put("is_user", false);
            logger.info("[validateMusicUser] uid : {} ,deviceId :{} ,is_user : {}", uid, deviceId, false);
        }
        return HttpResponseService.createOKResponse(response.toJSONString());
    }

    private HttpResponse isPaidUserResponse(String requestUri, HttpRequest request) throws PortalException {
        String uid = HTTPUtils.getStringParameter(HTTPUtils.getUrlParameters(requestUri), "uid");
        JSONObject response = new JSONObject();
        if (StringUtils.isNotEmpty(uid)) {
            response = accountService.isPaidUser(uid);
        } else {
            response.put("is_paid_music_user", false);
            logger.info("[isPaidUser] uid : {} ,is_user : {}", uid, false);
        }
        return HttpResponseService.createOKResponse(response.toJSONString());
    }



    private HttpResponse cachePurge(String requestUri, HttpRequest request) throws PortalException {
        String msisdn = HTTPUtils.getStringParameter(HTTPUtils.getUrlParameters(requestUri), "msisdn");
        JSONObject response = new JSONObject();
        response.put("msisdn",msisdn);
        if (StringUtils.isNotEmpty(msisdn)) {
            try {
                String uid = UserDeviceUtils.generateUUID(msisdn, null, null, MusicPlatformType.WYNK_APP);
                String uuid=MusicUtils.encryptAndEncodeParam(musicConfig.getEncryptionKey(),uid);
                accountService.purgeOfferCache(uid);
                response.put("uuid",uuid);
            } catch (Exception e) {
                logger.warn("Error generating uid : " + e.getMessage(), e);
            }
        }
        return HTTPUtils.createOKResponse(response.toJSONString());
    }

    private HttpResponse generatePinExternalWeb(String requestUri) throws PortalException {
        String msisdn = HTTPUtils.getStringParameter(HTTPUtils.getUrlParameters(requestUri), "msisdn");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("msisdn",msisdn);
        jsonObj.put("countryCode","+91");
        JSONObject responseJson = accountService.generatePin(jsonObj, true,false);
        if(!responseJson.containsKey("error")) {
            return HTTPUtils.getCompressedEmptyOkReponse("{}");
        }
        else {
            return HttpResponseService.createResponse(responseJson.toJSONString(), HttpResponseStatus.BAD_REQUEST);
        }
    }

    private HttpResponse createChromecastUser(String requestPayload, HttpRequest request) throws PortalException {
        String msisdn = UserDeviceUtils.getMsisdn(request);
        JSONObject requestJson = null;
        if(!StringUtils.isEmpty(requestPayload)) {
            try {
                requestJson = (JSONObject) JSONValue.parseWithException(requestPayload);
            }
            catch (ParseException e) {

                LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e,
                        ExceptionTypeEnum.CODE.name(),
                        "",
                        "AccountUrlRequestHandler.createChromecastUser",
                        "Exception parsing request for create chromecast user");

                e.printStackTrace();
                throw new PortalException("Error parsing request : " + e.getMessage(), e);
            }
        }

        JSONObject responseJsonObj = new JSONObject();


        responseJsonObj.put("dupd", false);

        responseJsonObj.put("purge", false);

        responseJsonObj = accountService.createChromecastUser(msisdn, requestJson);

        if(!responseJsonObj.containsKey("token")) {
            logger.info("chromecast - token not found using default user id token");
            responseJsonObj.put("uid", "g698s0qTLrADLU-9UpJ8jBQkAG43");
            responseJsonObj.put("token", "c58vYuYl");
        }
        String responseJson = responseJsonObj.toJSONString();
        if(!responseJsonObj.containsKey("error"))
        {
            HttpResponse response = HttpResponseService.createOKResponse(responseJson);
            if(!StringUtils.isEmpty((String) responseJsonObj.get("token")))
            {
                Cookie tokenCookie = new DefaultCookie("x-bsb-utkn", (String) responseJsonObj.get("token"));
                tokenCookie.setMaxAge(365 * 24 * 60 * 60);
                response.headers().set("Set-Cookie", ServerCookieEncoder.encode(tokenCookie));
            }
            return response;
        }
        else {
            HttpResponse response = HttpResponseService.createResponse(responseJson, HttpResponseStatus.BAD_REQUEST);
            return response;
        }
    }

    private HttpResponse getMusicSubscription(String requestUri, String requestPayload, HttpRequest request) {
        MusicSubscriptionStatus statusDto = accountService.getMusicSubscriptionStatus(request, true, requestUri, false, false);
        JSONObject jsonObject = statusDto.toJsonObject();
        return HttpResponseService.createOKResponse(jsonObject.toJSONString());
    }

    private HttpResponse detectMsisdn(String requestUri, HttpRequest request) {
         String msisdn = UserDeviceUtils.getMsisdn(request);
         String responseJson = null;

         JSONObject jsonObject = new JSONObject();
         if (StringUtils.isNotBlank(msisdn)) {
             String uid = ChannelContext.getUid();
             String encryptionKey = uid.substring(0,16);
             try {
     			jsonObject.put("msisdn", EncryptUtils.encrypt_256(msisdn, encryptionKey));
     		} catch (Exception e) {
     			logger.error("Error while encrypting msisdn");
     		}
         }
         responseJson = jsonObject.toJSONString();
         HttpResponse response = HttpResponseService.createOKResponse(responseJson);
         return response;
	 }


    private HttpResponse getAccountInfo(String requestUri, HttpRequest request) {
        String msisdn = UserDeviceUtils.getMsisdn(request);
        String responseJson = null;
        if(StringUtils.isEmpty(msisdn)) {
            responseJson = AccountService.createErrorResponse("BSY001", "Unable to determine MSISDN").toJSONString();
            return HttpResponseService.createResponse(responseJson, HttpResponseStatus.BAD_REQUEST);
        }

        String uid = ChannelContext.getUid();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msisdn", Utils.normalizePhoneNumber(msisdn));

        try {
            Map<String, List<String>> urlParameters = HTTPUtils.getUrlParameters(requestUri);

            if(urlParameters.get("restrict") != null && urlParameters.get("restrict").size() > 0) {
                try {
                    if(StringUtils.isBlank(uid)) {
                    	MusicPlatformType platform = UserDeviceUtils.getUserPlatform(msisdn, null, ChannelContext.getRequest());
						uid =  UserDeviceUtils.generateUUID(msisdn,null,ChannelContext.getRequest(),platform);
                    }
                } catch (Exception e) {
                    logger.error("Error generating uid in getAccountInfo from msisdn  " + msisdn);
                }
                User user = accountService.getUserFromUid(uid);
                jsonObject.put("uid", uid);
//                if(ChannelContext.getContentLang2() != null)
//                    jsonObject.put("clang2", ChannelContext.getContentLang2().getName());
//                if(ChannelContext.getContentLang3() != null)
//                    jsonObject.put("clang3", ChannelContext.getContentLang3().getName());
                if(user != null) {
                    jsonObject.put("circle", user.getCircle());
                    //jsonObject.put("user", user.toJsonObject());
                    jsonObject.put("lang", user.getLang());
                    List<String> contentLang = user.getSelectedLanguages();
                    if(!CollectionUtils.isEmpty(contentLang))
                        jsonObject.put("clang1", contentLang.get(0));

                }
            }
        }
        catch (PortalException e) {
        	 LogstashLoggerUtils.createCriticalExceptionLog(e,
                     ExceptionTypeEnum.CODE.name(),
                     ChannelContext.getUid(),
                     "AccountUrlRequestHandler.getAccountInfo");

            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

        responseJson = jsonObject.toJSONString();
        HttpResponse response = HttpResponseService.createOKResponse(responseJson);
        return response;
    }

    /**
     * Generates an otp and request a call to twilio with the specified otp.
     */
    private HttpResponse generateOtpCall(String requestPayload, HttpRequest request, boolean isFourDigitPin, boolean isEncrypted) throws PortalException {
        return twilioService.generateOtpCall(requestPayload, isFourDigitPin, isEncrypted);
    }

    private HttpResponse generatePin(String requestPayload, HttpRequest request, boolean isFourDigitPin,boolean isEncrypted) throws PortalException {
        if(StringUtils.isEmpty(requestPayload))
            return HttpResponseService
                    .createResponse(AccountService.createErrorResponse("BSY007", "Empty request").toJSONString(),
                            HttpResponseStatus.NO_CONTENT);

        String origin = request.headers().get("Origin");
        String referer = request.headers().get("Referer");

        if (StringUtils.isNotEmpty(origin) && origin.contains("smsbomber")) {
            return HttpResponseService.createResponse(AccountService.createErrorResponse("400", "Bad request").toJSONString(), HttpResponseStatus.BAD_REQUEST);
        }

        if (StringUtils.isNotEmpty(referer) && referer.contains("smsbomber")) {
            return HttpResponseService.createResponse(AccountService.createErrorResponse("400", "Bad request").toJSONString(), HttpResponseStatus.BAD_REQUEST);
        }

        JSONObject requestJson = accountService.getJsonObjectFromPayload(requestPayload);

        JSONObject responseJson = accountService.generatePin(requestJson, isFourDigitPin,isEncrypted);
        if(!responseJson.containsKey("error")) {
            return HTTPUtils.getCompressedEmptyOkReponse("{}");
        }
        else {
            return HttpResponseService.createResponse(responseJson.toJSONString(), HttpResponseStatus.BAD_REQUEST);
        }
    }

    private HttpResponse loginAndUpdate(String requestPayload, HttpRequest request, boolean isFourDigitPin) throws PortalException {
        ChannelContext.setUser(null);
        ChannelContext.setUid(null);
        JSONObject requestJson = null;
        if(StringUtils.isNotBlank(requestPayload)) {
            try {
                requestJson = (JSONObject) JSONValue.parseWithException(requestPayload);
            }
            catch (ParseException e) {

                LogstashLoggerUtils.createCriticalExceptionLog(e,
                        ExceptionTypeEnum.CODE.name(),
                        ChannelContext.getUid(),
                        "AccountUrlRequestHandler.loginOrCreate");

                throw new PortalException("Error parsing request : " + e.getMessage(), e);
            }
        }
        String msisdn = (String) requestJson.remove("msisdn");
        HttpResponse response = loginOrCreateUserS2S(requestJson, request, msisdn, isFourDigitPin);
        if(requestJson.containsKey("profileUpdate")){
            updateUserProfile((JSONObject) requestJson.get("profileUpdate"), request);
        }
        return response;
    }

    private HttpResponse loginOrCreateUserS2S(JSONObject requestJson , HttpRequest request, String msisdn, boolean isFourDigitPin) throws PortalException {
        Boolean isChangeMobileRequest = Boolean.FALSE;
        String archType = null;
        boolean isSamsungGTM = false;
        if(requestJson != null) {
            archType = (String) requestJson.get("archType");
            String medium = (String) requestJson.get("medium");
            isSamsungGTM = UserDeviceUtils.isSamsungGTMDevice(medium);
        }

        if(StringUtils.isNotBlank(ChannelContext.getMsisdn())){
            isChangeMobileRequest = Boolean.TRUE;
        }
        JSONObject responseJsonObj = null;
        //responseJsonObj = accountService.findOrCreate(msisdn, requestJson,isFourDigitPin,isSamsungGTM, request);
        responseJsonObj = accountRegistrationService.loginOrCreateAccount(msisdn,isSamsungGTM,requestJson,isFourDigitPin,request,msisdn,false);

        Boolean isConfigPopupSupported = false;
        isConfigPopupSupported = MusicBuildUtils.isConfigPopupOnAccountCreationSupported();

        boolean responseObjContainsErrorCode = responseJsonObj.containsKey("errorCode");
        boolean isIosBuild = MusicDeviceUtils.isIOSDevice();
        boolean isIosBuildAfterLangSplit = isIosBuildAfterLangSplit();

        // iOs doesn't cater to account call config data and hence popup get's missed . So sending config object only in builds after lang split
        if (!responseObjContainsErrorCode && (!isIosBuild || isIosBuildAfterLangSplit)) {
            responseJsonObj.put("config", musicService.getConfigData(request, isConfigPopupSupported, archType, false,requestJson,isChangeMobileRequest,true,false));
            responseJsonObj.remove(MusicConstants.NEW_USER);
            responseJsonObj.remove("current_lad");
        }
        String responseJson = responseJsonObj.toJSONString();
        if(!responseJsonObj.containsKey("error")) {
            HttpResponse response = HttpResponseService.createOKResponse(responseJson);
            if(!StringUtils.isEmpty((String) responseJsonObj.get("token")))
            {
                Cookie tokenCookie = new DefaultCookie("x-bsb-utkn", (String) responseJsonObj.get("token"));
                tokenCookie.setMaxAge(365 * 24 * 60 * 60);
                response.headers().set("Set-Cookie", ServerCookieEncoder.encode(tokenCookie));
            }
            return response;
        }
        else {
            HttpResponse response = HttpResponseService.createResponse(responseJson, HttpResponseStatus.BAD_REQUEST);
            return response;
        }
    }

    private HttpResponse loginOrCreateUser(String requestPayload, HttpRequest request, boolean isFourDigitPin, String requestUri,boolean isEncrypted) throws PortalException {
        JSONObject requestJson = null;
        Boolean isChangeMobileRequest = Boolean.FALSE;
        if(StringUtils.isNotBlank(requestPayload)) {
            try {
                requestJson = (JSONObject) JSONValue.parseWithException(requestPayload);
            }
            catch (ParseException e) {
                LogstashLoggerUtils.createCriticalExceptionLog(e,
                        ExceptionTypeEnum.CODE.name(),
                        ChannelContext.getUid(),
                        "AccountUrlRequestHandler.loginOrCreate");

                throw new PortalException("Error parsing request : " + e.getMessage(), e);
            }
        }
        String archType = null;
        boolean isSamsungGTM = false;
        if(requestJson != null) {
            archType = (String) requestJson.get("archType");
            String medium = (String) requestJson.get("medium");
            isSamsungGTM = UserDeviceUtils.isSamsungGTMDevice(medium);
        }
        String msisdn = null;
        if(isSamsungGTM || UserDeviceUtils.isThirdPartyPlatformRequest(request))
            msisdn = UserDeviceUtils.getMsisdnFromBsyHeader(request);
        else {
            msisdn = UserDeviceUtils.getMsisdn(request);
        }

        if(StringUtils.isNotBlank(ChannelContext.getMsisdn())){
            isChangeMobileRequest = Boolean.TRUE;
        }
        JSONObject responseJsonObj = null;
        //responseJsonObj = accountService.findOrCreate(msisdn, requestJson,isFourDigitPin,isSamsungGTM, request);
        responseJsonObj = accountRegistrationService.loginOrCreateAccount(msisdn,isSamsungGTM,requestJson,isFourDigitPin,request, requestUri,isEncrypted);

        Boolean isConfigPopupSupported = false;
        isConfigPopupSupported = MusicBuildUtils.isConfigPopupOnAccountCreationSupported();

        boolean isIosBuildAfterLangSplit = isIosBuildAfterLangSplit();
        // iOs doesn't cater to account call config data and hence popup get's missed . So sending config object only in builds after lang split
       if (!responseJsonObj.containsKey("errorCode") && (!MusicDeviceUtils.isIOSDevice() || isIosBuildAfterLangSplit)) {
           boolean isNewUser = false;
           List<String> pages = new ArrayList<>();
           try {
               isNewUser = (boolean) responseJsonObj.getOrDefault(MusicConstants.NEW_USER, false);
               long timeDiff = System.currentTimeMillis() - (long)responseJsonObj.getOrDefault(JsonKeyNames.INITIAL_LAD, System.currentTimeMillis());
               if (isNewUser ||  timeDiff > SIX_MONTHS_IN_MILLIS) {
                   pages = OnboardingPages.getListOfPages();
               }
           } catch (Exception e) {
               logger.error("Exception while setting onboarding pages. Exception: {}", e.toString());
           }
           JSONObject configObj = musicService.getConfigData(request, isConfigPopupSupported, archType, false, requestJson, isChangeMobileRequest, true, isEncrypted);
           configObj.put(JsonKeyNames.ONBOARDING_PAGES, pages);
           responseJsonObj.put("config", configObj);
           responseJsonObj.remove(MusicConstants.NEW_USER);
           responseJsonObj.remove(JsonKeyNames.INITIAL_LAD);
       }
        accountService.removeUserFromCache(ChannelContext.getUid());
        String responseJson = responseJsonObj.toJSONString();
        if(!responseJsonObj.containsKey("error")) {
            HttpResponse response = HttpResponseService.createOKResponse(responseJson);
            if(!StringUtils.isEmpty((String) responseJsonObj.get("token")))
            {
                Cookie tokenCookie = new DefaultCookie("x-bsb-utkn", (String) responseJsonObj.get("token"));
                tokenCookie.setMaxAge(365 * 24 * 60 * 60);
                response.headers().set("Set-Cookie", ServerCookieEncoder.encode(tokenCookie));
            }
            return response;
        }
        else {
            HttpResponse response = HttpResponseService.createResponse(responseJson, HttpResponseStatus.BAD_REQUEST);
            return response;
        }
    }

    private boolean isIosBuildAfterLangSplit() {
    	Map<String,String> oSBuildNo = MusicDeviceUtils.getOSAndBuildNo();
        String os = oSBuildNo.get(MusicConstants.OS);
        String buildNumber = oSBuildNo.get(MusicConstants.APP_BUILD_NO);
        int currentBuildNum = Integer.parseInt(buildNumber);
        boolean isIos = MusicDeviceUtils.isIOSDeviceFromOS(os);
        return isIos&&MusicBuildUtils.isNewerBuildNumber(currentBuildNum, MusicConstants.IOS_LANGSPLIT_BUILD_NUMBER);
	}

    private HttpResponse updateUserProfile(JSONObject requestJson, HttpRequest request) throws PortalException {
        User user = accountService.getUserFromUid(UserUtils.getUidFromRequest(request));
        if(user != null){
            ChannelContext.setUser(user);
        }
        JSONObject responseJsonObj = accountService.updateUserProfile(requestJson);
        String responseJson = responseJsonObj.toJSONString();
        if(!responseJsonObj.containsKey("error")) {
            HttpResponse response = HttpResponseService.createOKResponse(responseJson);
            return response;
        }
        else {
            HttpResponse response = HttpResponseService.createResponse(responseJson, HttpResponseStatus.BAD_REQUEST);
            return response;
        }
    }

	private HttpResponse updateUserProfile(String requestPayload, HttpRequest request) throws PortalException {
        JSONObject requestJson = null;
        if(!StringUtils.isBlank(requestPayload)) {
            try {
                requestJson = (JSONObject) JSONValue.parseWithException(requestPayload);
            }
            catch (ParseException e) {

                LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e,
                        ExceptionTypeEnum.CODE.name(),
                        "",
                        "AccountUrlRequestHandler.updateUserProfile",
                        "Exception parsing request");

                e.printStackTrace();
                throw new PortalException("Error parsing request : " + e.getMessage(), e);
            }
        }
        JSONObject responseJsonObj = accountService.updateUserProfile(requestJson);
        String responseJson = responseJsonObj.toJSONString();
        if(!responseJsonObj.containsKey("error")) {
            HttpResponse response = HttpResponseService.createOKResponse(responseJson);
            return response;
        }
        else {
            HttpResponse response = HttpResponseService.createResponse(responseJson, HttpResponseStatus.BAD_REQUEST);
            return response;
        }
    }

    private HttpResponse updateUserAvatar(String requestPayload, HttpRequest request) throws PortalException {
        JSONObject requestJson = null;
        if(!StringUtils.isEmpty(requestPayload)) {
            try {
                requestJson = (JSONObject) JSONValue.parseWithException(requestPayload);
            }
            catch (ParseException e) {
                LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e,
                        ExceptionTypeEnum.CODE.name(),
                        "",
                        "AccountUrlRequestHandler.updateUserAvatar",
                        "Error parsing request");

                e.printStackTrace();
                throw new PortalException("Error parsing request : " + e.getMessage(), e);
            }
        }
        JSONObject responseJsonObj = accountService.updateUserAvatar(requestJson);
        String responseJson = responseJsonObj.toJSONString();
        if(!responseJsonObj.containsKey("error")) {
            HttpResponse response = HttpResponseService.createOKResponse(responseJson);
            return response;
        }
        else {
            HttpResponse response = HttpResponseService.createResponse(responseJson, HttpResponseStatus.BAD_REQUEST);
            return response;
        }
    }

    private HttpResponse getUserProfile(HttpRequest request) throws PortalException {
        JSONObject responseJsonObj = accountService.getUserProfile();

        if(responseJsonObj == null)
        {
            HttpResponse response = HttpResponseService.createOKResponse(JsonUtils.EMPTY_JSON_STR);
            return response;
        }
        String responseJson = responseJsonObj.toJSONString();
        if(!responseJsonObj.containsKey("error")) {
            HttpResponse response = HttpResponseService.createOKResponse(responseJson);
            return response;
        }
        else {
            HttpResponse response = HttpResponseService.createResponse(responseJson, HttpResponseStatus.BAD_REQUEST);
            return response;
        }
    }
}
