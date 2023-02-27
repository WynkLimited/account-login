package com.wynk.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.netflix.hystrix.HystrixCommand;
import com.wynk.common.ExceptionTypeEnum;
import com.wynk.common.FlowId;
import com.wynk.common.Language;
import com.wynk.common.PortalException;
import com.wynk.config.MusicConfig;
import com.wynk.constants.JsonKeyNames;
import com.wynk.constants.MusicConstants;
import com.wynk.constants.SubscriptionChannelPhases;
import com.wynk.db.MongoDBManager;
import com.wynk.dto.*;
import com.wynk.music.WCFServiceType;
import com.wynk.music.constants.MusicNotificationType;
import com.wynk.music.constants.MusicRequestSource;
import com.wynk.music.dto.GeoLocation;
import com.wynk.server.ChannelContext;
import com.wynk.service.api.WCFApiService;
import com.wynk.service.helper.WcfHelper;
import com.wynk.service.hystrix.HystrixUtils;
import com.wynk.service.hystrix.WCFCommandSetterFactory;
import com.wynk.service.hystrix.WCFProvisionsCommand;
import com.wynk.service.hystrix.WCFRecommendedPacksCommand;
import com.wynk.subscription.WCFProductInfo;
import com.wynk.subscription.WCFSubscriptionPack;
import com.wynk.user.dto.User;
import com.wynk.user.dto.UserEntityKey;
import com.wynk.utils.*;
import com.wynk.wcf.WCFApisService;
import com.wynk.wcf.WCFApisConstants;
import com.wynk.wcf.WCFApisUtils;
import com.wynk.wcf.dto.OfferProvisionRequest;
import com.wynk.wcf.dto.*;
import io.netty.handler.codec.http.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.wynk.constants.MusicConstants.*;

/**
 * Created by aakashkumar on 22/11/16.
 */

@SuppressWarnings({"unchecked","rawtypes"})
@Service
public class WCFService extends BaseService{

    private static final Logger logger               = LoggerFactory.getLogger(WCFService.class.getCanonicalName());
    public static final String IQ_USER_DELETE_ENDPOINT = "/iq/s2s/user/v1/delete";
    public static final String WCF_S2S_V3_VERIFY_RECEIPT_ENDPOINT  = "/wynk/s2s/v3/verify/receipt";
    public static final String CREATE_GET_USER_ENDPOINT = "/iq/s2s/user/";
    public static final String WCF_CLAIM_BENEFIT_ENDPOINT  = "/wynk/benefit/v1/claim";
    @Value("${hystrix.wcf.recommended.timeout}")
    public int RECOMMENDED_PACKS_EXECUTION_TIMEOUT = 400;

    @Value("${hystrix.wcf.provision.timeout}")
    public int PROVISION_EXECUTION_TIMEOUT = 800;

    @Value("${hystrix.wcf.threadpool.size}")
    public int hystrixWcfThreadPoolSize = 200;
    @Value("${hystrix.wcf.threadpool.queue}")
    public int hystrixWcfThreadPoolQueue = 20;


    @Autowired
    private MongoDBManager mongoMusicDBManager;

    @Autowired
    private WCFApiService wcfApiService;

    @Autowired
    private WCFApisService wcfApisService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MyAccountService myAccountService;

    @Autowired
    private MusicConfig musicConfig;

    @Autowired
    private S3Utils s3Utils;

    @Autowired
    private WCFUtils wcfUtils;

    @Autowired
    private WCFApisUtils wcfApisUtils;

    @Autowired
    MusicService musicService;

    @Value("${wcf.user.consent.base.url}")
    private String identityServiceBaseUrl;

    private Gson gson = new Gson();

    public UserSubscription getFallbackPackFromSubscriptionStatus(UserSubscription provisionObject,UserSubscription userCurrentSubscription){
        if (provisionObject==null || CollectionUtils.isEmpty(provisionObject.getProdIds())){
            return userCurrentSubscription;
        }
        UserSubscription wcfSubscription = new UserSubscription();
        wcfSubscription.setOfferTS(System.currentTimeMillis() + WCFUtils.WCF_FALLBACK_PRODUCT_EXPIRY_DURATION);
        wcfSubscription.setProdIds(provisionObject.getProdIds());
        return wcfSubscription;
    }

    public UserSubscription wcfOfferCall(String service, String msisdnStr, UserSubscription userCurrentSubscription, GeoLocation location) {

        OfferProvisionRequest wcfProvisionOfferRequestApiObject = OfferProvisionRequest.builder()
            .appId(WCFApisConstants.APPID_MOBILITY).msisdn(msisdnStr).appVersion(ChannelContext.getAppVersion()).buildNo(ChannelContext.getBuildnumber()).deviceId(ChannelContext.getDeviceId()).os(ChannelContext.getOS()).service(WCFServiceType.WYNK_MUSIC.getServiceName()).uid(ChannelContext.getUid()).build();

        HystrixCommand.Setter setter = WCFCommandSetterFactory.INSTANCE.getSetter(HystrixUtils.WCF_COMMAND_GROUP_KEY, PROVISION_EXECUTION_TIMEOUT, hystrixWcfThreadPoolSize, hystrixWcfThreadPoolQueue);
        boolean isSubscriptionActive = wcfUtils.isUserSubscriptionActive(ChannelContext.getUser());
        logger.info("Preparing geo location for Async Hystrix Offer provision call with location {}",location.toString());
        wcfProvisionOfferRequestApiObject.setGeoLocation(location);
        WCFProvisionsCommand provisionsCommand = new WCFProvisionsCommand(setter, wcfApisService, service, wcfProvisionOfferRequestApiObject, ChannelContext.getUid(), isSubscriptionActive);
        UserSubscription wcfCurrentOfferProvisionObject = provisionsCommand.execute();
        if(provisionsCommand.isResponseFromFallback()){
            return getFallbackPackFromSubscriptionStatus(wcfCurrentOfferProvisionObject, userCurrentSubscription);
        }
        return wcfCurrentOfferProvisionObject;
    }

    @Deprecated
    public List<Integer> updateRecommendedProductId(String service,String userId, String msisdn, WCFSubscription wcfSubscription )throws Exception {
        List<Integer> productIdList = getSortedRecommendedProductId(service,msisdn);

        if(CollectionUtils.isNotEmpty(productIdList)) {
            if(wcfSubscription == null){
                wcfSubscription = new WCFSubscription();
            }

            wcfSubscription.setRecommendedProductId(productIdList);
            wcfSubscription.setLastUpdatedTS(System.currentTimeMillis());
            Map<String, Object> paramValues = new HashMap();
            paramValues.put(UserEntityKey.subscription, wcfSubscription.toJsonObject());
            accountService.updateSubscriptionForUserId(userId, paramValues, Boolean.FALSE, null);
            User user = ChannelContext.getUser();
            user.setSubscription(wcfSubscription);
            ChannelContext.setUser(user);

        }
        return productIdList;
    }

    @Deprecated
    public List<Integer> getSortedRecommendedProductId(String service,String msisdn)throws Exception {
        HystrixCommand.Setter setter = WCFCommandSetterFactory.INSTANCE.getSetter(HystrixUtils.WCF_COMMAND_GROUP_KEY, RECOMMENDED_PACKS_EXECUTION_TIMEOUT, hystrixWcfThreadPoolSize, hystrixWcfThreadPoolQueue);
        WCFRecommendedPacksCommand  packsCommand= new WCFRecommendedPacksCommand(setter, wcfApiService, service, msisdn);
        List<Integer> productIdList = packsCommand.execute();
        if(CollectionUtils.isNotEmpty(productIdList)) {
            productIdList = wcfUtils.sortProductIdByHierarchyAscPriceAsc(service,productIdList);
        }
        return productIdList;
    }

    @Deprecated
    public WCFSubsInitResponseObject subscribePaidPack(String service, String msisdnStrFromHeader, String msisdnStr, String userId, Integer productId)throws Exception {

        Integer packId = null;
        if(productId != null){
            WCFSubscriptionPack wcfSubscriptionPack = WCFSubscriptionPack.getWCFSubscriptionPackDetailByProductId(WCFServiceType.getWCFServiceType(service),productId);
            if(wcfSubscriptionPack != null){
                packId = wcfSubscriptionPack.getPackId();
            }
        }

        WCFSubscribeInitializationObject wcfSubscribeInitializationObject = new WCFSubscribeInitializationObject();
        wcfSubscribeInitializationObject.setMsisdn(msisdnStr);
        wcfSubscribeInitializationObject.setUid(userId);
        wcfSubscribeInitializationObject.setMsisdnFromHeader(msisdnStrFromHeader);
        wcfSubscribeInitializationObject.setEmail(Utils.getSingleEmail(ChannelContext.getUser().getEmail()));
        wcfSubscribeInitializationObject.setName(ChannelContext.getUser().getName());
        wcfSubscribeInitializationObject.setReturnUrl(musicConfig.getWcfBaseApiUrl() + MusicConstants.SUBSCRIBE_CALL_BACK_URL);
        wcfSubscribeInitializationObject.setProductId(packId);
        wcfSubscribeInitializationObject.setAppVersion(ChannelContext.getAppVersion());
        String buildNumber = ChannelContext.getBuildnumber() != null ? String.valueOf(ChannelContext.getBuildnumber()) : null;
        wcfSubscribeInitializationObject.setBuildNumber(buildNumber);

        return wcfApiService.wcfInitialisationCall(service,wcfSubscribeInitializationObject);
    }

    @Deprecated
    public static JSONObject createErrorResponse(String errorcode, String errorMessage)
    {
        JSONObject response = new JSONObject();
        response.put("errorCode",errorcode);
        response.put("error",errorMessage);
        return response;
    }

    @Deprecated
    public AccountWCFUpgradeResponseDTO subscribePack(String requestUri, HttpRequest request) throws PortalException {
        AccountWCFUpgradeResponseDTO accountWCFUpgradeResponseDTO = null;
        String msisdnStr = accountService.getMsisdnByUID();
        if(StringUtils.isNotBlank(msisdnStr))
            msisdnStr = Utils.getTenDigitMsisdn(msisdnStr);
        String redirectUris = null;

        String userId = ChannelContext.getUid();

        MusicRequestSource musicReqSrc = MusicUtils.getRequestSourceType();
        Map<String, List<String>> urlParameters = null;

        if(!musicService.isGeoRestrictionPassed()){
            accountWCFUpgradeResponseDTO = new AccountWCFUpgradeResponseDTO();
            redirectUris = musicConfig.getBaseFeUrl() + "payment/failure.html?dg=5&bsycode=109&src="+musicReqSrc.getId()+"&fid="+ FlowId.SUBS;
            accountWCFUpgradeResponseDTO.setStatus(JsonKeyNames.FAILURE);
            accountWCFUpgradeResponseDTO.setUrl(redirectUris);
            return accountWCFUpgradeResponseDTO;
        }

        if(StringUtils.isEmpty(msisdnStr)) {
            accountWCFUpgradeResponseDTO = new AccountWCFUpgradeResponseDTO();
            redirectUris = musicConfig.getBaseFeUrl() + "payment/failure.html?dg=5&bsycode=109&src="+musicReqSrc.getId()+"&fid="+ FlowId.SUBS;
            accountWCFUpgradeResponseDTO.setStatus(JsonKeyNames.FAILURE);
            accountWCFUpgradeResponseDTO.setUrl(redirectUris);
            return accountWCFUpgradeResponseDTO;
        }
        WCFServiceType wcfServiceType = wcfUtils.getWCFServiceType(UserDeviceUtils.getPlatform());
        try{
            urlParameters = HTTPUtils.getUrlParameters(requestUri);
            String isPremiumString = Utils.getURLParam(urlParameters,"isPremium").trim();
            Integer productId = NumberUtils.toInt(Utils.getURLParam(urlParameters,"productId").trim(),-1);

            Boolean isPremium = Boolean.FALSE;
            if(StringUtils.isNotBlank(isPremiumString) && isPremiumString.equalsIgnoreCase("true")){
                isPremium = Boolean.TRUE;
            }
            String msisdnFromRequest = UserDeviceUtils.getMsisdn(request);
            return subscribePlan(wcfServiceType.getServiceName(),productId,msisdnStr,userId,isPremium,msisdnFromRequest);
        }catch (Exception ex){
            logger.error("Exception occured in subscribePack for Uid {}",ChannelContext.getUid());
        }
        redirectUris = musicConfig.getBaseFeUrl() + "payment/failure.html?dg=5&bsycode=109&src="+musicReqSrc.getId()+"&fid="+ FlowId.SUBS;
        accountWCFUpgradeResponseDTO = new AccountWCFUpgradeResponseDTO();
        accountWCFUpgradeResponseDTO.setStatus(JsonKeyNames.FAILURE);
        accountWCFUpgradeResponseDTO.setUrl(redirectUris);
        return accountWCFUpgradeResponseDTO;
    }

    @Deprecated
    public AccountWCFUpgradeResponseDTO subscribePlan(String service,Integer productId, String msisdnStr, String userId, Boolean isPremium, String msisdnFromHeader) throws Exception {
        AccountWCFUpgradeResponseDTO accountWCFUpgradeResponseDTO = new AccountWCFUpgradeResponseDTO();
        String redirectUri = null;

        MusicRequestSource musicReqSrc = MusicUtils.getRequestSourceType();
        if(productId.equals(-1)){
            redirectUri = musicConfig.getBaseFeUrl() + "payment/failure.html?dg=5&bsycode=109&src="+musicReqSrc.getId()+"&fid="+ FlowId.SUBS;
            accountWCFUpgradeResponseDTO.setStatus(JsonKeyNames.FAILURE);
            accountWCFUpgradeResponseDTO.setUrl(redirectUri);
            return accountWCFUpgradeResponseDTO;
        }

        WCFSubsInitResponseObject wcfSubsInitResponseObject = subscribePaidPack(service,msisdnFromHeader,msisdnStr,userId,productId);

        if(wcfSubsInitResponseObject == null || StringUtils.isBlank(wcfSubsInitResponseObject.getRedirectUri()) ||
                StringUtils.isBlank(wcfSubsInitResponseObject.getTransactionId())){
            logger.info("Redirected uris :"+redirectUri);
            redirectUri = musicConfig.getBaseFeUrl() + "payment/failure.html?dg=5&bsycode=109&src="+musicReqSrc.getId()+"&fid="+ FlowId.SUBS;
            accountWCFUpgradeResponseDTO.setStatus(JsonKeyNames.FAILURE);
            accountWCFUpgradeResponseDTO.setUrl(redirectUri);
            return accountWCFUpgradeResponseDTO;
        }

        Boolean isOtpRequired = wcfUtils.isOtpRequiredForCarrierBillingPayment(wcfSubsInitResponseObject.getCarrierBillingAllow(),ChannelContext.getDeviceId(),ChannelContext.getUser());
        WCFTransactionHistory wcfTransactionHistory = wcfUtils.createWCFTxnHistoryObject(service,msisdnStr,productId,userId,wcfSubsInitResponseObject.getTransactionId(),null,isOtpRequired);
        mongoMusicDBManager.addObject(WCF_TRANSACTION_HISTORY_COLLECTION,gson.toJson(wcfTransactionHistory));

        redirectUri = wcfSubsInitResponseObject.getRedirectUri();

        logger.info("Redirected uris :"+redirectUri);
        if(StringUtils.isNotBlank(redirectUri)){
            accountWCFUpgradeResponseDTO.setStatus(JsonKeyNames.SUCCESS);
            accountWCFUpgradeResponseDTO.setUrl(redirectUri);
            accountWCFUpgradeResponseDTO.setCarrierBillingAllow(Boolean.FALSE);
            if(wcfSubsInitResponseObject.getCarrierBillingAllow() != null){
                accountWCFUpgradeResponseDTO.setCarrierBillingAllow(wcfSubsInitResponseObject.getCarrierBillingAllow());
            }
            accountWCFUpgradeResponseDTO.setOtpRequired(isOtpRequired);
            accountWCFUpgradeResponseDTO.setTransactionId(wcfTransactionHistory.getTransactionId());
            accountWCFUpgradeResponseDTO.setProductId(wcfTransactionHistory.getProductId());
            return accountWCFUpgradeResponseDTO;
        }
        redirectUri = musicConfig.getBaseFeUrl() + "payment/failure.html?dg=5&bsycode=109&src="+musicReqSrc.getId()+"&fid="+ FlowId.SUBS;
        accountWCFUpgradeResponseDTO.setStatus(JsonKeyNames.FAILURE);
        accountWCFUpgradeResponseDTO.setUrl(redirectUri);
        return accountWCFUpgradeResponseDTO;
    }

    @Deprecated
    public JSONObject createResponseForNewSubscription(AccountWCFUpgradeResponseDTO accountWCFUpgradeResponseDTO){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if(accountWCFUpgradeResponseDTO == null || accountWCFUpgradeResponseDTO.getStatus() ==null || accountWCFUpgradeResponseDTO.getStatus().equalsIgnoreCase(JsonKeyNames.FAILURE)){
            jsonObject.put(JsonKeyNames.STATUS,JsonKeyNames.FAILURE);
            return jsonObject;
        }

        String lang = ChannelContext.getLang();
        Language userLang = Language.getLanguageById(lang);
        WCFSubscriptionPack wcfSubscriptionPack = WCFSubscriptionPack.getWCFSubscriptionPackDetailByProductId(WCFServiceType.WYNK_MUSIC,accountWCFUpgradeResponseDTO.getProductId());
        WCFProductInfo wcfProductInfo = wcfUtils.getNotificationMessage(wcfSubscriptionPack,new MusicSubscriptionStatus(),userLang.getId(),Boolean.FALSE);

        jsonObject.put(JsonKeyNames.STATUS,accountWCFUpgradeResponseDTO.getStatus());

        String url = null;
        if(accountWCFUpgradeResponseDTO.getCarrierBillingAllow() != null && accountWCFUpgradeResponseDTO.getCarrierBillingAllow()){
            if(accountWCFUpgradeResponseDTO.getOtpRequired() != null && accountWCFUpgradeResponseDTO.getOtpRequired()){
                url = musicConfig.getApiBaseUrl() + MusicConstants.SEND_OTP_URL + "?u=" + MusicUtils.encryptAndEncodeParam(musicConfig.getEncryptionKey(), ChannelContext.getUid());
                jsonArray.add(wcfUtils.createPaymentChannelResponse(MusicNotificationType.SEND_OTP,url,Boolean.FALSE,Boolean.TRUE,Boolean.FALSE,
                    wcfProductInfo.getNativeTitle(),wcfProductInfo.getNativeSubTitle(),userLang,HttpMethod.POST.name(), SubscriptionChannelPhases.SEND_OTP,wcfSubscriptionPack.getAutoRenewal(),wcfProductInfo.getPricePoint()));
            }
            else{
                url = musicConfig.getApiBaseUrl() + MusicConstants.CONFIRM_PAYMENT_URL + "?u=" + MusicUtils.encryptAndEncodeParam(musicConfig.getEncryptionKey(), ChannelContext.getUid());
                jsonArray.add(wcfUtils.createPaymentChannelResponse(MusicNotificationType.PURCHASE_AIRTEL_BILLING,url,Boolean.FALSE,Boolean.TRUE,Boolean.FALSE,
                        wcfProductInfo.getNativeTitle(),wcfProductInfo.getNativeSubTitle(),userLang,HttpMethod.POST.name(), SubscriptionChannelPhases.AIRTEL_CARRIER_BILLING,wcfSubscriptionPack.getAutoRenewal(),wcfProductInfo.getPricePoint()));
            }
        }

        if(StringUtils.isNotBlank(accountWCFUpgradeResponseDTO.getUrl())){
            jsonArray.add(wcfUtils.createPaymentChannelResponse(MusicNotificationType.UPGRADE,accountWCFUpgradeResponseDTO.getUrl(),Boolean.TRUE,Boolean.FALSE,Boolean.FALSE,
                    wcfProductInfo.getNativeTitle(),wcfProductInfo.getNativeSubTitle(),userLang, HttpMethod.GET.name(), SubscriptionChannelPhases.OTHER_CHANNELS,wcfSubscriptionPack.getAutoRenewal(),wcfProductInfo.getPricePoint()));
        }

        jsonObject.put(JsonKeyNames.TRANSACTION_ID,accountWCFUpgradeResponseDTO.getTransactionId());
        if(jsonArray.size() > 0){
            jsonObject.put(JsonKeyNames.PAYMENT_PAYLOAD,jsonArray);
        }
        return jsonObject;
    }

    public boolean validateUserConsent(String body) {
        logger.info("Requesting to WCF with body  {}", body);
        boolean isSuccess = false;
        try {
            StringBuilder url = new StringBuilder(identityServiceBaseUrl);
            url.append(IQ_USER_DELETE_ENDPOINT);
            logger.info("Url is {}", url);
            Map<String, String> headerMap = wcfUtils.getHeaderMap(WCFApisConstants.METHOD_POST, IQ_USER_DELETE_ENDPOINT, body);
            logger.info("Header map is {}", headerMap.toString());
            String response = HttpClient.postData(url.toString(), body, headerMap, 500);
            logger.info("Received response from WCF is {}", response);
            if (Objects.isNull(response)) {
                return isSuccess;
            }
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonResponse = (JsonObject) jsonParser.parse(response);
            JsonElement jsonSuccessKey = jsonResponse.get(JsonKeyNames.SUCCESS);
            isSuccess = Boolean.parseBoolean(jsonSuccessKey.getAsString());
            logger.info("Status flag received from Wcf is : {}", isSuccess);
            if (isSuccess) {
                isSuccess = accountService.deleteUserFromDB(ChannelContext.getUid(), isSuccess);
                logger.info("Mongo db updated successfully with value : {}", isSuccess);
            }
        } catch (Exception e) {
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, ExceptionTypeEnum.CODE.name(), "", "WCFService.validateUserConsent", "Exception while validate user consent");

            logger.error("Exception while delete user. {}, {}", e.getMessage(), e);
        }
        return isSuccess;
    }

    public WcfResponse<VerifyReceiptResponseV3> verifyPaymentReceiptV3(String body) {
        logger.info("Wcf base pay url found is {}", musicConfig.getWcfPayBaseUrl());
        long start = System.currentTimeMillis();
        logger.info("Requesting to WCF with body  {}", body);
        StringBuilder url = new StringBuilder(musicConfig.getWcfPayBaseUrl());
        url.append(WCF_S2S_V3_VERIFY_RECEIPT_ENDPOINT);
        logger.info("Url is {}", url);
        Map<String, String> headerMap = wcfUtils.getHeaderMap(WCFApisConstants.METHOD_POST, WCF_S2S_V3_VERIFY_RECEIPT_ENDPOINT, body);
        logger.info("Header map verify payment receipt is {}", headerMap.toString());
        String response = HttpClient.postData(url.toString(), body, headerMap, 2000);
        LogstashLoggerUtils.logExecutionStep(response,ChannelContext.getUid(),ChannelContext.getMsisdn(),"fromWCF",response);
        logger.info("Received response from WCF verify payment receipt is {}", response);
        if (Objects.isNull(response)) {
            return null;
        }
        WcfResponse<VerifyReceiptResponseV3> responseIs = JsonUtils.GSON.fromJson(response, new TypeToken<WcfResponse<VerifyReceiptResponseV3>>() {
        }.getType());
        logger.info("Net time taken by wcf is {} ms", System.currentTimeMillis() - start);
        return responseIs;
    }

    public String createOrGetUserId(String body)
    {
        logger.info("Requesting to WCF with body for create or get user id {}", body);
        String fetchedUid = null;
        try {
            StringBuilder url = new StringBuilder(identityServiceBaseUrl);
            url.append(CREATE_GET_USER_ENDPOINT);
            logger.info("Url is {}", url);
            Map<String, String> headerMap = wcfUtils.getHeaderMap(WCFApisConstants.METHOD_POST, CREATE_GET_USER_ENDPOINT, body);
            logger.info("Header map created for create or get user {}", headerMap.toString());
            String response = HttpClient.postData(url.toString(), body, headerMap, 250);
            logger.info("Received response from WCF for create or get user {}", response);
            if (Objects.isNull(response)) {
                return fetchedUid;
            }
            WcfApiResponseDTO<CreateAndGetApiResponse> wcfApiResponseDTO = JsonUtils.GSON.fromJson(response, new TypeToken<WcfApiResponseDTO<CreateAndGetApiResponse>>() {
            }.getType());
            if (wcfApiResponseDTO != null && wcfApiResponseDTO.getData() != null && wcfApiResponseDTO.getData().getUid() != null) {
                fetchedUid = wcfApiResponseDTO.getData().getUid();
                // if this uid is deleted then save entry in context only for getting it while creating user
                if (wcfApiResponseDTO.getData().getVersion() != 0) {
                    int deletedVersion = wcfApiResponseDTO.getData().getVersion() - 1;
                    logger.info("Saving deleted version in context is {}", deletedVersion);
                    ChannelContext.setUserDeletedVersionContext(String.valueOf(deletedVersion));
                }
            }
            logger.info("Fetched Uid received in wcf response is : {}", fetchedUid);
        } catch (Exception e) {
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, ExceptionTypeEnum.CODE.name(), fetchedUid, "WCFService.createOrGetUserId", "Exception while creating Or getting User");

            logger.error("Exception while creating or getting user. {}, {}", e.getMessage(), e);
            return null;
        }
        return fetchedUid;

    }

    public String getPreviousUidBasedOnDeletedVersion(String msisdn, String currentVersion) {
        String previousUid = null;
        if (StringUtils.isBlank(msisdn) || StringUtils.isBlank(currentVersion)) {
            logger.info("Either msisdn : {} or deleted version : {} is empty or null", msisdn, currentVersion);
            return previousUid;
        }
        logger.info("Requesting to WCF to get old user with msisdn : {} and deleted version {}", msisdn, currentVersion);
        try {
            StringBuilder url = new StringBuilder(identityServiceBaseUrl);
            StringBuilder endpoint = new StringBuilder(CREATE_GET_USER_ENDPOINT).append(BACKEND_SERVICE_NAME).append("/").append(msisdn).append("/").append(currentVersion);
            url = url.append(endpoint);
            logger.info("Prepared Url is {}", url);
            Map<String, String> headerMap = wcfUtils.getHeaderMap(WCFApisConstants.METHOD_GET, endpoint.toString(), null);
            logger.info("Header map created to get old user {}", headerMap.toString());
            String response = HttpClient.getContent(url.toString(), 250, headerMap);
            logger.info("Received response from WCF for previous uid on deleted version basis {}", response);
            if (Objects.isNull(response)) {
                return previousUid;
            }
            WcfApiResponseDTO<CreateAndGetApiResponse> wcfApiResponseDTO = JsonUtils.GSON.fromJson(response, new TypeToken<WcfApiResponseDTO<CreateAndGetApiResponse>>() {
            }.getType());
            if (wcfApiResponseDTO != null && wcfApiResponseDTO.getData() != null && wcfApiResponseDTO.getData().getUid() != null) {
                previousUid = wcfApiResponseDTO.getData().getUid();
                logger.info("Previous Uid is : {}", previousUid);
            }
        } catch (Exception e) {
            LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, ExceptionTypeEnum.CODE.name(), previousUid, "WCFService.getPreviousUidBasedOnDeletedVersion", "Exception while getting previous user");
            logger.error("Exception while getting previous user. {}, {}", e.getMessage(), e);
            return null;
        }
        return previousUid;
    }

    public JSONObject claimBenefit(String requestPayload) {

        long start = System.currentTimeMillis();

        JSONObject payloadJson = (JSONObject) JSONValue.parse(requestPayload);
        if (Objects.nonNull(payloadJson)) {
//            GeoLocation location = WcfHelper.addLocationInRequest();
            GeoLocation location = musicService.prepareGeoLocationOnDemand(ChannelContext.getUser());
            location.setAccessCountryCode(null);
            payloadJson.put("geoLocation", JsonUtils.GSON.fromJson(JsonUtils.GSON.toJson(location), JsonObject.class));
            requestPayload = payloadJson.toJSONString();
        }

        logger.info("Claim Benefit : WCF : Body : {}", requestPayload);

        StringBuilder url = new StringBuilder(musicConfig.getWcfBaseUrl()).append(WCF_CLAIM_BENEFIT_ENDPOINT);
        logger.info("Claim Benefit : WCF : URL : {}", url);

        Map<String, String> headerMap = wcfUtils.getHeaderMap(WCFApisConstants.METHOD_POST, WCF_CLAIM_BENEFIT_ENDPOINT, requestPayload);
        logger.info("Claim Benefit : WCF : Headers : {}", headerMap.toString());

        LogstashLoggerUtils.logExecutionStep(requestPayload, ChannelContext.getUid(), ChannelContext.getMsisdn(), "ToWCF","");

        String response = HttpClient.postData(url.toString(), requestPayload, headerMap, 3500);
        if (StringUtils.isBlank(response)) {
            logger.info("Blank response received");
            return null;
        }
        LogstashLoggerUtils.logExecutionStep(requestPayload, ChannelContext.getUid(), ChannelContext.getMsisdn(), "fromWCF", response);
        logger.info("Claim Benefit : WCF : Response : {}", response);

        logger.info("Claim Benefit : WCF : Time Taken : {} ms", System.currentTimeMillis() - start);

        return (JSONObject) JSONValue.parse(response);
    }

}
