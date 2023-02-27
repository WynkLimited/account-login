package com.wynk.service;

import com.google.gson.Gson;
import com.wynk.common.*;
import com.wynk.config.MusicConfig;
import com.wynk.constants.MusicConstants;
import com.wynk.db.MongoDBManager;
import com.wynk.dto.*;
import com.wynk.music.WCFServiceType;
import com.wynk.music.constants.MusicRequestSource;
import com.wynk.music.dto.GeoLocation;
import com.wynk.server.ChannelContext;
import com.wynk.subscription.WCFSubscriptionPack;
import com.wynk.user.dto.User;
import com.wynk.user.dto.UserEntityKey;
import com.wynk.utils.*;
import com.wynk.wcf.WCFApisUtils;
import com.wynk.wcf.dto.Feature;
import com.wynk.wcf.dto.FeatureType;
import com.wynk.wcf.dto.UserSubscription;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wynk.constants.MusicConstants.WCF_TRANSACTION_HISTORY_COLLECTION;


/**
 * Created by Aakash on 15/12/16.
 */

@SuppressWarnings({"unchecked","rawtypes"})
@Service
public class MyAccountService extends BaseService{

    private static final Logger logger = LoggerFactory.getLogger(WCFService.class.getCanonicalName());

    @Autowired
    private WCFService wcfService;

    @Autowired
    private WCFApisUtils wcfApisUtils;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MongoDBManager mongoMusicDBManager;

    @Autowired
    private MusicConfig musicConfig;

    @Autowired
    private WCFUtils wcfUtils;

    @Autowired
    private UserUpdateService userUpdateService;

    private Gson gson = new Gson();

    private static final String LATEST_USER_SUBS = "LATEST_USER_SUBS";
    private static final String NON_EMPTY_USER_LATEST_SUBS = "NON_EMPTY_USER_LATEST_SUBS";
    private static final String SUBS_NOT_FALLBACK = "SUBS_NOT_FALLBACK";

    public UserSubscription getCurrentSubscriptionPlanForUser(String service, User user) throws Exception {
        logger.info("In getCurrentSubscriptionPlanForUser with User via CONTEXT is :  {}", user.toJson());
        String countryId;
        if (user.getCountryId() != null) {
            countryId = user.getCountryId();
            logger.info("coo via User CONTEXT is : {}", countryId);
        } else {
            countryId = ChannelContext.getUserCooContext();
            logger.info("access coo via header set value : {}", countryId);
        }
        logger.info("Otherwise lets hit db to cross check what's present there :  {}", accountService.getUserFromUid(user.getUid()).toJson());
        UserSubscription userLatestSubscription = wcfService.wcfOfferCall(service, user.getMsisdn(), user.getUserSubscription(),new GeoLocation()._setCountryCode(countryId)._setAccessCountryCode(ChannelContext.getUserCoaContext()));
        if (ObjectUtils.notEmpty(userLatestSubscription)) {
            boolean isFallback = userLatestSubscription.getProdIds().stream().anyMatch(e -> e.getProdId() == WCFUtils.WCF_FALLBACK_PRODUCT_ID);
            if (!isFallback) {
                userLatestSubscription.setOfferTS(System.currentTimeMillis());
                Map<String,Object> paramValues = new HashMap();
                Feature fupFeature = wcfApisUtils.getFeature(FeatureType.STREAMING,userLatestSubscription);
                Feature fupFeatureOld = wcfApisUtils.getFeature(FeatureType.STREAMING,user.getUserSubscription());
                if (fupFeatureOld.getValidTill() != fupFeature.getValidTill()) {
                    paramValues = wcfUtils.getFupResetParameterMap(System.currentTimeMillis());
                }
                paramValues.put(UserEntityKey.userSubscription,JsonUtils.getJsonObjectFromString(userLatestSubscription.toJson()));
                accountService.updateSubscriptionForUserId(user.getUid(),paramValues,Boolean.FALSE,userLatestSubscription);
                user.setUserSubscription(userLatestSubscription);
                ChannelContext.setUser(user);
            }
        }
        return userLatestSubscription;
    }

    public HttpResponse displaySubscriptionPage(HttpRequest request){
        UserSubscription wcfSubscription;
        UserSubscription subscriptionMap = null;
        List<Integer> productIdList = null;
        String msisdnStr = accountService.getMSISDN();
        MusicRequestSource musicReqSrc = MusicUtils.getRequestSourceType();
        Boolean isProvisionCalled = Boolean.FALSE;

        WCFServiceType wcfServiceType = wcfUtils.getWCFServiceType(UserDeviceUtils.getPlatform());
        try {
            wcfSubscription = wcfUtils.getUserSubscriptionDetails(ChannelContext.getUser());

            if (StringUtils.isNotBlank(msisdnStr) && wcfUtils.isWcfProvisionCallRequired(wcfSubscription)) {

                logger.info("From displaySubscriptionPage method user is : {}", ChannelContext.getUser().toJson());
                subscriptionMap = getCurrentSubscriptionPlanForUser(wcfServiceType.getServiceName(),ChannelContext.getUser());
                productIdList = wcfService.updateRecommendedProductId(wcfServiceType.getServiceName(),ChannelContext.getUid(),msisdnStr,wcfUtils.getUserSubscriptionDetail(ChannelContext.getUser()));
                isProvisionCalled = Boolean.TRUE;
            }
            else{
                //productIdList = wcfSubscription!= null ? wcfSubscription.getRecommendedProductId():null;
            }

            if(StringUtils.isNotBlank(msisdnStr) && productIdList == null && !isProvisionCalled){
                //productIdList = wcfService.updateRecommendedProductId(wcfServiceType.getServiceName(),ChannelContext.getUid(),msisdnStr,wcfSubscription);
            }

        }catch(Throwable th ){
            logger.error("Exception occurred in Displaying the Page url for ex mssge {} and ex ",th.getMessage(),th);
            String redirectUri = musicConfig.getBaseFeUrl() + "payment/failure.html?dg=5&bsycode=109&src="+musicReqSrc.getId()+"&fid="+ FlowId.SUBS;
            return HTTPUtils.createRedirectResponse(redirectUri);
        }

        wcfSubscription = wcfUtils.getUserSubscriptionDetails(ChannelContext.getUser());

        Integer productId = null;
        if(!ObjectUtils.isEmpty(subscriptionMap)){
            if(wcfApisUtils.getUserFeature(wcfSubscription).isSubscribed()){
                String redirectUri = musicConfig.getBaseFeUrl() + "payment/offer_success.html?"+MusicConstants.SUBSCRIPTION_SUCCESS_PARAMETER+"&bsys=success&dg=5";
                return HTTPUtils.createRedirectResponse(redirectUri);
            }
            wcfSubscription = wcfUtils.getUserSubscriptionDetails(ChannelContext.getUser());
            if(wcfSubscription != null) {
                List<Integer> eligibleOfferId = null;
                if(!CollectionUtils.isEmpty(eligibleOfferId)){
                    productId = eligibleOfferId.get(0);
                }
            }
        }

        String redirectUri = null;
        try {
            if (productId != null) {
                if (MusicDeviceUtils.isIOSDevice()) {
                    WCFSubscriptionPack wcfSubscriptionPack = WCFSubscriptionPack.getWCFSubscriptionPackDetailByProductId(wcfServiceType,productId);
                    if(wcfSubscriptionPack == null) {
                        logger.error("WCF Subscription Pack Detail is Null for the productId {} for msisdn {}",productId,msisdnStr);
                    }
                    redirectUri = musicConfig.getWcfBaseApiUrl() + "FEPAGE" + "?bsyext=" + musicConfig.getIosBsyExt() + "&pid=" + wcfSubscriptionPack.getExternalProductId();
                } else {
                    String msisdnFromHeader = UserDeviceUtils.getMsisdn(request);
                    WCFSubsInitResponseObject wcfSubsInitResponseObject = wcfService.subscribePaidPack(wcfServiceType.getServiceName(),msisdnFromHeader,msisdnStr, ChannelContext.getUid(), productId);
                    if (wcfSubsInitResponseObject == null || StringUtils.isBlank(wcfSubsInitResponseObject.getRedirectUri()) ||
                            StringUtils.isBlank(wcfSubsInitResponseObject.getTransactionId())) {
                        redirectUri = musicConfig.getBaseFeUrl() + "payment/failure.html?dg=5&bsycode=109&src="+musicReqSrc.getId()+"&fid="+ FlowId.SUBS;
                        logger.info("Redirected uris :"+redirectUri);
                        return HTTPUtils.createRedirectResponse(redirectUri);
                    }

                    WCFTransactionHistory wcfTransactionHistory = wcfUtils.createWCFTxnHistoryObject(wcfServiceType.getServiceName(),msisdnStr, productId, ChannelContext.getUid(), wcfSubsInitResponseObject.getTransactionId(),null,Boolean.FALSE);
                    try {
                        mongoMusicDBManager.addObject(WCF_TRANSACTION_HISTORY_COLLECTION, gson.toJson(wcfTransactionHistory));
                        redirectUri = wcfSubsInitResponseObject.getRedirectUri();
                    } catch (Exception ex) {
                        LogstashLoggerUtils.createCriticalExceptionLogWithMessage(ex, ExceptionTypeEnum.CODE.name(), ChannelContext.getUid(),
                                "MyAccountService.displaySubscriptionPage", "Exception in Inserting in WCF Transaction History" );
                        logger.error("Exception occured in inserting detail in Wcf Transaction entry for {}",wcfTransactionHistory);
                        redirectUri = musicConfig.getBaseFeUrl() + "payment/failure.html?dg=5&bsycode=109&src="+musicReqSrc.getId()+"&fid="+ FlowId.SUBS;
                    }
                }
            }
        }catch (Exception ex){
            redirectUri = musicConfig.getBaseFeUrl() + "payment/failure.html?dg=5&bsycode=109&src="+musicReqSrc.getId()+"&fid="+ FlowId.SUBS;
        }
        if (StringUtils.isBlank(redirectUri)) {
            redirectUri = musicConfig.getBaseFeUrl() + "payment/failure.html?dg=5&bsycode=109&src="+musicReqSrc.getId()+"&fid="+ FlowId.SUBS;
        }
        logger.info("Redirected uris :"+redirectUri);
        return HTTPUtils.createRedirectResponse(redirectUri);
    }

    public UserSubscription updateAndProvisionSubscription(String msisdnStr,WCFServiceType wcfServiceType){
        UserSubscription wcfSubscription = null;
        UserSubscription subscriptionMap = null;

        try {
            wcfSubscription = wcfUtils.getUserSubscriptionDetails(ChannelContext.getUser());

            if (StringUtils.isNotBlank(msisdnStr)) {
                if(wcfUtils.isWcfProvisionCallRequired(wcfSubscription)) {
                    logger.info("From updateAndProvisionSubscription method user is {}", ChannelContext.getUser().toJson());
                     subscriptionMap = getCurrentSubscriptionPlanForUser(wcfServiceType.getServiceName(), ChannelContext.getUser());
                }
                if(wcfUtils.isNDSCallRequired(ChannelContext.getUser().getNdsTS())) {
                    logger.info("Nds call required for user : " + ChannelContext.getUser().getUid() + " His last nds timestamp : " + ChannelContext.getUser().getNdsTS()) ;
                    userUpdateService.checkAndUpdateOperator();
                }
            }
        }catch(Exception ex ){
            logger.error("Exception occured in calling Wcf Provision Call and ex mssge is {} and ex is",ex.getMessage(),ex);
        }

        return subscriptionMap;
    }
}
