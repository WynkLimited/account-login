package com.wynk.utils;

import ch.qos.logback.core.db.dialect.SybaseSqlAnywhereDialect;
import com.google.gson.Gson;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.wynk.common.Language;
import com.wynk.config.MusicConfig;
import com.wynk.constants.MusicConstants;
import com.wynk.constants.SubscriptionChannelPhases;
import com.wynk.constants.WCFPaymentMethod;
import com.wynk.db.ShardedRedisServiceManager;
import com.wynk.dto.*;
import com.wynk.music.WCFServiceType;
import com.wynk.music.constants.MusicNotificationType;
import com.wynk.music.dto.MusicPlatformType;
import com.wynk.notification.NotificationsText;
import com.wynk.subscription.WCFProductInfo;
import com.wynk.subscription.WCFSubscriptionPack;
import com.wynk.user.dto.User;
import com.wynk.user.dto.UserDevice;
import com.wynk.user.dto.UserEntityKey;
import com.wynk.wcf.WCFApisService;
import com.wynk.wcf.WCFApisConstants;
import com.wynk.wcf.dto.*;
import com.wynk.wcf.dto.SubscriptionStatus;
import com.wynk.wcf.dto.UserSubscription.ProductMeta;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by Aakash on 24/01/17.
 */
@Component
public class WCFUtils {

    private static final Logger logger = LoggerFactory.getLogger(WCFUtils.class.getCanonicalName());

    @Autowired
    private MusicConfig musicConfig;

    @Autowired
    private WCFApisService wcfApisService;


    private Set<Integer> lyricsProductIds;
    public static final Integer WCF_FALLBACK_PRODUCT_ID = 1;
    public static final Integer WCF_FALLBACK_OFFER_ID = 1;
    public static final Integer WCF_FALLBACK_PLAN_ID = 1;
    public static final Long WCF_FALLBACK_PRODUCT_EXPIRY_DURATION = 24*3600*1000L;
    public static final UserSubscription FALLBACK_PROVISION = new UserSubscription();

    public static Set<Integer> htAllowedProductIds = new HashSet<>();
    public static Set<Integer> htTrialProductIds = new HashSet<>();
    public static Set<Integer> downloadsAllowedProductIds = new HashSet<>();
    public static Set<Integer> streamingAllowedProductIds = new HashSet<>();
    public static Set<Integer> hideAdsProductIds = new HashSet<>();

    @Autowired
    private ShardedRedisServiceManager musicUserShardRedisServiceManager;

    public void setHtProductIdsInRedis(Set<Integer> htAllowedProductIds)
    {
        this.htAllowedProductIds = htAllowedProductIds;
        logger.info("setting ht products in redis, redis key : {} , productIds : {}", MusicConstants.REDIS_KEY_FOR_HT_PRODUCTS_NEW, htAllowedProductIds);
        if(CollectionUtils.isNotEmpty(htAllowedProductIds)) {
            musicUserShardRedisServiceManager.set(MusicConstants.REDIS_KEY_FOR_HT_PRODUCTS_NEW,Arrays.toString(htAllowedProductIds.toArray()));
        }
    }

    public void setHtTrialProductIdsInRedis(Set<Integer> htTrialProductIds)
    {
        this.htTrialProductIds = htTrialProductIds;
        logger.info("setting ht trial products in redis, redis key : {} , productIds : {}", MusicConstants.REDIS_KEY_FOR_HT_TRIAL_PRODUCTS_NEW, htTrialProductIds);
        if(CollectionUtils.isNotEmpty(htTrialProductIds)) {
            musicUserShardRedisServiceManager.set(MusicConstants.REDIS_KEY_FOR_HT_TRIAL_PRODUCTS_NEW,Arrays.toString(htTrialProductIds.toArray()));
        }
    }

    public void setDownloadsProductIdsInRedis(Set<Integer> downloadsAllowedProductIds)
    {
        this.downloadsAllowedProductIds = downloadsAllowedProductIds;
        logger.info("setting download products in redis, redis key : {} , productIds : {}", MusicConstants.REDIS_KEY_FOR_DOWNLOADS_PRODUCTS, downloadsAllowedProductIds);
        if(CollectionUtils.isNotEmpty(downloadsAllowedProductIds)) {
            musicUserShardRedisServiceManager.set(MusicConstants.REDIS_KEY_FOR_DOWNLOADS_PRODUCTS,Arrays.toString(downloadsAllowedProductIds.toArray()));
        }
    }

    public void setStreamingProductIdsInRedis(Set<Integer> streamingAllowedProductIds)
    {
        this.streamingAllowedProductIds = streamingAllowedProductIds;
        logger.info("setting streaming products in redis, redis key : {} , productIds : {}", MusicConstants.REDIS_KEY_FOR_STREAMING_PRODUCTS, streamingAllowedProductIds);
        if(CollectionUtils.isNotEmpty(streamingAllowedProductIds)) {
            musicUserShardRedisServiceManager.set(MusicConstants.REDIS_KEY_FOR_STREAMING_PRODUCTS,Arrays.toString(streamingAllowedProductIds.toArray()));
        }
    }

    public void setHtAllowedProductIdsMetaInRedis(Map<Integer, Map<String, Object>> htMeta) {
        logger.info("setting ht offers meta in redis, redis key : {} , meta : {}", MusicConstants.REDIS_KEY_FOR_WCF_HT_META, htMeta);
        if(htMeta != null && htMeta.size() > 0) {
            musicUserShardRedisServiceManager.set(MusicConstants.REDIS_KEY_FOR_WCF_HT_META, new Gson().toJson(htMeta));
        }
    }

    public void setHideAdsProductIdsInRedis(Set<Integer> hideAdsProductIds)
    {
        this.hideAdsProductIds = hideAdsProductIds;
        logger.info("setting Ads products in redis, redis key : {} , productIds : {}", MusicConstants.REDIS_KEY_FOR_HIDE_ADS_PRODUCTS, hideAdsProductIds);
        if(CollectionUtils.isNotEmpty(hideAdsProductIds)) {
            musicUserShardRedisServiceManager.set(MusicConstants.REDIS_KEY_FOR_HIDE_ADS_PRODUCTS,Arrays.toString(hideAdsProductIds.toArray()));
        }
    }

    public void setOffersMetaInRedis(Map<Integer, Map<String, Object>> offersMeta)
    {
        logger.info("setting wcf offers meta in redis, redis key : {} , meta : {}", MusicConstants.REDIS_KEY_FOR_WCF_OFFERS_META, offersMeta);
        if(offersMeta != null && offersMeta.size() > 0) {
            musicUserShardRedisServiceManager.set(MusicConstants.REDIS_KEY_FOR_WCF_OFFERS_META, new Gson().toJson(offersMeta));
        }
    }

    public void setCompleteOffersMetaInRedis(Map<Integer, Offer> offersMeta)
    {
        logger.info("setting wcf offers meta in redis complete, redis key : {} , meta : {}", MusicConstants.REDIS_KEY_FOR_WCF_OFFERS_META_COMPLETE, offersMeta);
        if(offersMeta != null && offersMeta.size() > 0) {
            musicUserShardRedisServiceManager.set(MusicConstants.REDIS_KEY_FOR_WCF_OFFERS_META_COMPLETE, new Gson().toJson(offersMeta));
        }
    }

    static {
        ProductMeta productMeta = new ProductMeta(WCF_FALLBACK_PRODUCT_ID, WCF_FALLBACK_PLAN_ID, WCF_FALLBACK_OFFER_ID, WCF_FALLBACK_PRODUCT_EXPIRY_DURATION);
        FALLBACK_PROVISION.builder().prodIds(Collections.singletonList(productMeta)).offerTS(0l).build();
    }

    @PostConstruct
    public void init() {
        lyricsProductIds = new HashSet<>();
        lyricsProductIds.add(musicConfig.getTrialProductId());
        lyricsProductIds.add(musicConfig.getTrialAgainProductId());
        lyricsProductIds.add(musicConfig.getBundledTrialProductId());
        lyricsProductIds.add(musicConfig.getBundledTrialAgainProductId());
    }

    public Map<String, Object> getFupResetParameterMap(Long lastFUPResetDate){
        Map<String, Object> paramValues = new HashMap<>();
        paramValues.put("packs.FUPPack."+ UserEntityKey.FupPack.rentalsCount, 0);
        paramValues.put("packs.FUPPack."+ UserEntityKey.FupPack.streamedCount, 0);
        paramValues.put("packs.FUPPack."+ UserEntityKey.FupPack.shownFUPWarning, false);
        paramValues.put("packs.FUPPack."+ UserEntityKey.FupPack.shownFUP95Warning, false);
        paramValues.put("packs.FUPPack."+ UserEntityKey.FupPack.lastFUPResetDate, lastFUPResetDate);
        return paramValues;
    }

    public static boolean isPaidUser(User user) {

        if (user == null || user.getUserSubscription() == null) {
            return Boolean.FALSE;
        }
        List<ProductMeta> prods = user.getUserSubscription().getProdIds();
        for (ProductMeta prod : prods) {
            Integer offerId = prod.getOfferId();
            Offer offer = WCFApisService.getOffer(offerId);
            if (prod.getEts() > System.currentTimeMillis() && offer.getProvisionType().equals(ProvisionType.PAID)) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    @Deprecated
    public List<Integer> getDistinctPackId(WCFServiceType wcfServiceType ,List<Integer> productIdList){
        if(CollectionUtils.isNotEmpty(productIdList)){

            if(productIdList.size() < 2){
                return productIdList;
            }

            List<Integer> wcfPackIdList = new ArrayList<>();
            List<Integer> wcfProductIdList = new ArrayList<>();
            BiMap<Integer, Integer> prodToPackId = HashBiMap.create();

            for(Integer productId : productIdList){
                WCFSubscriptionPack wcfSubscriptionPack = WCFSubscriptionPack.getWCFSubscriptionPackDetailByProductId(wcfServiceType,productId);
                boolean isGoogleWallet = wcfSubscriptionPack.getPaymentMethod().equalsIgnoreCase(WCFPaymentMethod.GOOGLE_WALLET.getPaymentMethod());
                // disabling "SE" products
                if(wcfSubscriptionPack.getPaymentMethod().equalsIgnoreCase("se")){
                        continue;
                    }
                if(wcfSubscriptionPack != null && (!wcfPackIdList.contains(wcfSubscriptionPack.getPackId()) || isGoogleWallet)){
                    if(!wcfPackIdList.contains(wcfSubscriptionPack.getPackId())){
                        wcfPackIdList.add(wcfSubscriptionPack.getPackId());
                    }
                    prodToPackId.put(wcfSubscriptionPack.getPackId(), productId);
                }
            }
            prodToPackId.entrySet().stream().forEach(e -> wcfProductIdList.add((e.getValue())));
            return wcfProductIdList;
        }
        return productIdList;
    }

    public List<Integer> sortProductIdByHierarchyAscPriceAsc(String service, List<Integer> productIdList){
        List<WCFSubscriptionPack> wcfSubscriptionPackList = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(productIdList)){

            if(productIdList.size() < 2){
                return productIdList;
            }

            for(Integer productId : productIdList){
                WCFSubscriptionPack wcfSubscriptionPack = WCFSubscriptionPack.getWCFSubscriptionPackDetailByProductId(WCFServiceType.getWCFServiceType(service),productId);

                if(wcfSubscriptionPack != null){
                    wcfSubscriptionPackList.add(wcfSubscriptionPack);
                }
            }

            if(wcfSubscriptionPackList.size() > 1) {
                Collections.sort(wcfSubscriptionPackList, WCFSubscriptionPack.getAscComparatorByPriorityAscByPrice());

                List<Integer> sortedProductId = new ArrayList<>();
                for(WCFSubscriptionPack wcfSubscriptionPack : wcfSubscriptionPackList){
                    sortedProductId.add(wcfSubscriptionPack.getProductId());
                }
                return sortedProductId;
            }

        }
        return productIdList;
    }

    @Deprecated
    public WCFSubscriptionPack getWCFPackDetail(String service,Integer productId, MusicSubscriptionState musicSubscriptionState, Boolean isAirtelUser,Boolean isDeviceCheckRequired){
        WCFSubscriptionPack wcfSubscriptionPack = null;

        if(productId == null){
            musicSubscriptionState = MusicSubscriptionState.NEVER_SUBSCRIBED;
        }

        if (isUserSubsActive(musicSubscriptionState, productId)) {
            wcfSubscriptionPack = WCFSubscriptionPack.getWCFSubscriptionPackDetailByProductId(WCFServiceType.getWCFServiceType(service),productId);
        }
        else if(!MusicSubscriptionState.NEVER_SUBSCRIBED.equals(musicSubscriptionState)) {
            wcfSubscriptionPack = WCFSubscriptionPack.getWCFSubscriptionPackDetailByProductId(WCFServiceType.getWCFServiceType(service),productId);

            if (wcfSubscriptionPack == null || wcfSubscriptionPack.getDeprecated() || !wcfSubscriptionPack.getPaidPack() || (isDeviceCheckRequired && !isPackAndDeviceTypeSame(MusicDeviceUtils.isIOSDevice(),wcfSubscriptionPack.getIOSPack()))) {
                wcfSubscriptionPack = WCFSubscriptionPack.getDefaultPackDetail(WCFServiceType.getWCFServiceType(service),isAirtelUser);
            }
        }
        else{
            wcfSubscriptionPack = WCFSubscriptionPack.getDefaultPackDetail(WCFServiceType.getWCFServiceType(service),isAirtelUser);
        }
        return wcfSubscriptionPack;
    }

    @Deprecated
    public boolean isLyricsPackActive(MusicSubscriptionStatus musicSubscriptionStatus) {
        if(musicSubscriptionStatus == null){
            return false;
        }

        MusicSubscriptionState musicSubscriptionState = musicSubscriptionStatus.getState();

        if ((MusicSubscriptionState.SUBSCRIBED_PRE_REMINDER == musicSubscriptionState || MusicSubscriptionState.SUBSCRIBED_IN_REMINDER == musicSubscriptionState ) && lyricsProductIds.contains(musicSubscriptionStatus.getProductId())) {
            return true;
        }
        return false;
    }

    public Set<Integer> getHtAllowedProductIds() {
        return htAllowedProductIds;
    }

    public void addHtAllowedProductId(Integer htAllowedProductIds) {
        this.htAllowedProductIds.add(htAllowedProductIds);
    }

    public void clearHtAllowedProductIds() {
        this.htAllowedProductIds.clear();
    }

    public Set<Integer> getDownloadsAllowedProductIds() {
        return downloadsAllowedProductIds;
    }

    public void addDownloadsAllowedProductId(Integer downloadsAllowedProductIds) {
        this.downloadsAllowedProductIds.add(downloadsAllowedProductIds);
    }

    public void clearDownloadsAllowedProductIds() {
        this.downloadsAllowedProductIds.clear();
    }

    public boolean isDownloadAllowed(Integer productId) {
        if (getDownloadsAllowedProductIds().contains(productId)) {
            return true;
        }
        return false;
    }

    public boolean isHelloTunesAllowed(Integer productId) {
        if (getHtAllowedProductIds().contains(productId)) {
            return true;
        }
        return false;
    }

    public boolean isUserSubsActive (MusicSubscriptionStatus musicSubsStatus) {
        MusicSubscriptionState subsState = MusicSubscriptionState.NEVER_SUBSCRIBED;
        if(musicSubsStatus != null) {
            subsState = musicSubsStatus.getState();
        }
        if (subsState.equals(MusicSubscriptionState.SUBSCRIBED_PRE_REMINDER)
                || subsState.equals(MusicSubscriptionState.SUBSCRIBED_IN_REMINDER)) {
            return true;
        }
        return false;
    }


    @Deprecated
    public boolean isUserSubsActive(MusicSubscriptionState musicSubscriptionState, Integer productId) {
        if(musicSubscriptionState == null){
            return false;
        }

        if(MusicSubscriptionState.SUBSCRIBED_PRE_REMINDER == musicSubscriptionState || MusicSubscriptionState.SUBSCRIBED_IN_REMINDER == musicSubscriptionState ) {
            return true;
        }
        return false;
    }

    public Boolean isUserSubscriptionActive(User user){
        if (user.getUserSubscription() == null || user.getUserSubscription() == null || user.getUserSubscription().getProdIds().stream().anyMatch(e -> e.getEts() < System.currentTimeMillis())){
            return Boolean.FALSE;
        }
        return isNotExpired(user);
    }

    @Deprecated
    public Boolean isAutoRenewal(String service,Integer productId, Boolean isSubscribed){
        if(isSubscribed == null || isSubscribed.equals(Boolean.FALSE)){
            return Boolean.FALSE;
        }
        if(productId != null){
            WCFSubscriptionPack wcfSubscriptionPack = WCFSubscriptionPack.getWCFSubscriptionPackDetailByProductId(WCFServiceType.getWCFServiceType(service),productId);

            if(wcfSubscriptionPack != null){
                if(wcfSubscriptionPack.getPackageType() != null && wcfSubscriptionPack.getPackageType().equalsIgnoreCase("Subscription")){
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    public WCFSubscription getUserSubscriptionDetail(User user){

        if(user != null){
            WCFSubscription subscription = user.getSubscription();
            return subscription;
        }
        logger.info("getUserSubscriptionDetail: user is null");
        return null;
    }

    public UserSubscription getUserSubscriptionDetails(User user){

        if(user != null){
            UserSubscription subscription = user.getUserSubscription();
            return subscription;
        }
        logger.info("getUserSubscriptionDetail: user is null");
        return null;
    }

    @Deprecated
    public WCFTransactionHistory createWCFTxnHistoryObject(String service,String msisdn, Integer productId, String uid, String txnId, String payload,Boolean isOtpRequired){
        WCFTransactionHistory wcfTransactionHistory = new WCFTransactionHistory();

        if(org.apache.commons.lang3.StringUtils.isNotBlank(msisdn)) {
            wcfTransactionHistory.setMsisdn(msisdn);
        }
        if(productId != null) {
            wcfTransactionHistory.setProductId(productId);
        }
        wcfTransactionHistory.setUid(uid);
        wcfTransactionHistory.setTransactionId(txnId);
        wcfTransactionHistory.setRetryCount(0);
        wcfTransactionHistory.setCreationDate(System.currentTimeMillis());
        wcfTransactionHistory.setServiceType(service);
        wcfTransactionHistory.setOtpRequired(isOtpRequired);
        if(payload != null) {
            wcfTransactionHistory.setPayload(payload);
        }

        return wcfTransactionHistory;
    }

    @Deprecated
    public WCFPurchaseSongHistory createWCFPurchaseHistoryObject(String userId,String contentId,String contentTitle,String contentType,String orderId,Boolean isSamsung,Boolean isWapUser,String price,String transactionId,Integer src,String serviceType){
        WCFPurchaseSongHistory wcfPurchaseSongHistory = new WCFPurchaseSongHistory();

        wcfPurchaseSongHistory.setUserId(userId);
        wcfPurchaseSongHistory.setContentId(contentId);
        wcfPurchaseSongHistory.setContentTitle(contentTitle);
        wcfPurchaseSongHistory.setContentType(contentType);
        wcfPurchaseSongHistory.setOrderId(orderId);
        wcfPurchaseSongHistory.setSamsung(isSamsung);
        wcfPurchaseSongHistory.setPrice(price);
        wcfPurchaseSongHistory.setWapUser(isWapUser);
        wcfPurchaseSongHistory.setTransactionId(transactionId);
        wcfPurchaseSongHistory.setSrc(src);
        wcfPurchaseSongHistory.setCreationDate(System.currentTimeMillis());
        wcfPurchaseSongHistory.setRetryCount(1);
        wcfPurchaseSongHistory.setServiceType(serviceType);
        return wcfPurchaseSongHistory;
    }

    public UserSubscription getUpdatedSubscriptionObject(User user, WCFCallbackResponse wcfCallbackResponse) throws Exception {

        Integer planId = wcfCallbackResponse.getPlanId();
        String event = wcfCallbackResponse.getEvent();
        Long expireTS = wcfCallbackResponse.getValidTillDate();
        if (planId == null || event == null || expireTS == null) {
            throw new Exception("required parameters not available in callback payload");
        }

        UserSubscription wcfSubscription = getUserSubscriptionDetails(user);
        if (ObjectUtils.isEmpty(wcfSubscription)) {
            wcfSubscription = new UserSubscription();
        }
        Long currentTime = System.currentTimeMillis();
        Long diff = currentTime - expireTS;
        if (diff < 0 && event.equalsIgnoreCase("UNSUBSCRIBE")) {
            logger.info("UNSUBSCRIBE event for products expiring in future, nothing to update");
            return wcfSubscription;
        }

        PlanDTO planData = WCFApisService.getPlan(planId);
        if (planData == null) {
            wcfApisService.loadAllPlans();
        }
        planData = WCFApisService.getPlan(planId);
        if (planData == null) {
            throw new Exception("planId " + planId + " does not exist");
        }
        Integer offerId = planData.getLinkedOfferId();
        Offer offerData = WCFApisService.getOffer(offerId);
        if (offerData == null) {
            wcfApisService.loadOffers();
        }
        offerData = WCFApisService.getOffer(offerId);
        if (offerData == null) {
            throw new Exception("offerId" + offerId + " linked to planId \" + planId + \" does not exist");
        }

        HashSet<Integer> newProductIds = new HashSet<>();
        for (Map.Entry product : offerData.getProducts().entrySet()) {
            newProductIds.add(Integer.parseInt(product.getKey().toString()));
        }

        List<ProductMeta> updatedProductsList = wcfSubscription.getProdIds().stream()
                .filter(e -> !newProductIds.contains(e.getProdId())).collect(Collectors.toList());

        if (!event.equalsIgnoreCase("UNSUBSCRIBE")) {
            for (Integer prodId : newProductIds) {
                updatedProductsList.add(new ProductMeta(prodId, offerId, planId, expireTS));
            }
        }
        wcfSubscription.setProdIds(updatedProductsList);
        wcfSubscription.setOfferTS(0L);
        return wcfSubscription;
    }

    public UserSubscription getSubsObjFromSubsStatus(List<SubscriptionStatus> subscriptionStatusList) throws Exception {
        UserSubscription subs = new UserSubscription();
        subs.setProdIds(new ArrayList<>());
        subs.setOfferTS(System.currentTimeMillis());
        for (SubscriptionStatus status : subscriptionStatusList) {
            int planId = status.getPlanId();
            int prodId = status.getProductId();
            PlanDTO planData = WCFApisService.getPlan(planId);
            if (planData == null) {
                throw new Exception("planId " + planId + " does not exist");
            }
            int offerId = planData.getLinkedOfferId();
            long ets = status.getValidity();
            subs.getProdIds().add(new ProductMeta(prodId, offerId, planId, ets));
        }
        return subs;
    }

    public String replaceMessageParameter(String statement, Map<String,Object> paramMap){
        if(paramMap != null){
            Set<String> keySet = paramMap.keySet();

            if(keySet != null){
                for(String key : keySet){
                    statement = statement.replace(key, String.valueOf(paramMap.get(key)));
                }
            }
        }
        return statement;
    }

    public Boolean isWcfProvisionCallRequired(UserSubscription wcfSubscription){
        if(wcfSubscription == null || wcfSubscription.getOfferTS() == null){
            return Boolean.TRUE;
        }

        if(CollectionUtils.isEmpty(wcfSubscription.getProdIds())){
            return Boolean.TRUE;
        }

        Long lastOfferCallTimestamp = wcfSubscription.getOfferTS();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastOfferCallTimestamp);
        // Todo - change it to 1 day
        calendar.add(Calendar.DATE, MusicConstants.WCF_OFFER_CALL_INTERVAL_IN_DAY);
        Date nextUpdatedCall = calendar.getTime();

        Date currentDate = new Date();
        if(currentDate.compareTo(nextUpdatedCall) == 1){
            return Boolean.TRUE;
        }

        Long currentTime = System.currentTimeMillis();

        if (CollectionUtils.isNotEmpty(wcfSubscription.getProdIds()) && wcfSubscription.getProdIds().stream().anyMatch(o -> o.getEts() < currentTime)){
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public Boolean isNDSCallRequired(Long lastModifiedTimestamp){

        Long currentTime = System.currentTimeMillis();
        if (lastModifiedTimestamp == null) {
            return Boolean.TRUE;
        }
        if((lastModifiedTimestamp + MusicConstants.NDS_CALL_INTERVAL_IN_MILLISECONDS) < currentTime) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @Deprecated
    public WCFProductInfo getNotificationMessage(WCFSubscriptionPack wcfSubscriptionPack, MusicSubscriptionStatus musicSubscriptionStatus, String language, Boolean isAirtelUser){

        WCFProductInfo wcfProductInfo = null;

        if(null != wcfSubscriptionPack){
            if(isUserSubsActive(musicSubscriptionStatus)) {
                if ("hi".equalsIgnoreCase(language)) {
                    wcfProductInfo = wcfSubscriptionPack.getActive_hi();
                }
                else{
                    wcfProductInfo = wcfSubscriptionPack.getActive_en();
                }
            }else{
                if("hi".equalsIgnoreCase(language)){
                    wcfProductInfo = wcfSubscriptionPack.getInactive_hi();
                }
                else{
                    wcfProductInfo = wcfSubscriptionPack.getInactive_en();
                }
            }
        }
        return wcfProductInfo;
    }

    public String createSignature(String httpMethod , String requestUri, String requestBody, String timestamp, String secretKey){
        String digestString = new StringBuilder(httpMethod).append(requestUri).append(requestBody).append(timestamp).toString();
        String computedSignature ="";
        try {
            computedSignature = EncryptUtils.calculateRFC2104HMAC(digestString, secretKey);
        }catch (Throwable th){
            logger.error("exception occuured in calculating signature for the requestBody {}",requestBody);
        }
        return computedSignature;
    }

    @Deprecated
    public Boolean isPackAndDeviceTypeSame(Boolean isIOSDevice , Boolean isIOSPack){
        if((isIOSDevice && isIOSPack) || (!isIOSDevice && !isIOSPack)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public WCFServiceType getWCFServiceType(MusicPlatformType musicPlatformType){
        if(musicPlatformType == null){
            return WCFServiceType.WYNK_MUSIC;
        }

        switch (musicPlatformType){
            case SAMSUNG_SDK:  return WCFServiceType.SAMSUNG_MUSIC;

            default:           return WCFServiceType.WYNK_MUSIC;
        }
    }

    @Deprecated
    public MusicPlatformType getMusicPlatform(WCFServiceType wcfServiceType){
        if(wcfServiceType == null){
            return MusicPlatformType.WYNK_APP;
        }

        switch (wcfServiceType){
            case SAMSUNG_MUSIC: return MusicPlatformType.SAMSUNG_SDK;

            default: return MusicPlatformType.WYNK_APP;
        }
    }

    @Deprecated
    public Boolean isSubscriptionEventRequired(UserSubscription userSubscription, UserSubscription activatedSubscription){
        if(ObjectUtils.notEmpty(userSubscription) || ObjectUtils.notEmpty(activatedSubscription)){
            if(CollectionUtils.isNotEmpty(userSubscription.getProdIds()) && CollectionUtils.isNotEmpty(activatedSubscription.getProdIds())){
                for (ProductMeta productMeta : userSubscription.getProdIds()) {
                    boolean alreadyHasExpiredProduct = activatedSubscription.getProdIds().stream().anyMatch(e -> e.getProdId() == productMeta.getProdId() && e.getEts() == productMeta.getEts());
                    if(alreadyHasExpiredProduct){
                        return Boolean.FALSE;
                    }
                }
            }
        }
        return true;
    }

    @Deprecated
    public WCFMsisdnIdentificationRequestBody createWCFMsisdnRequestBody(UserDevice userDevice,String xMsisdn,String ipAddress){
        WCFMsisdnIdentificationRequestBody wcfMsisdnIdentificationRequestBody = new WCFMsisdnIdentificationRequestBody();

        wcfMsisdnIdentificationRequestBody.setAppVersion(userDevice.getAppVersion());
        wcfMsisdnIdentificationRequestBody.setDeviceId(userDevice.getDeviceId());
        wcfMsisdnIdentificationRequestBody.setImei(userDevice.getImeiNumber());
        wcfMsisdnIdentificationRequestBody.setIpAddress(ipAddress);
        wcfMsisdnIdentificationRequestBody.setPlatform(userDevice.getOs());
        wcfMsisdnIdentificationRequestBody.setxMsisdn(xMsisdn);

        String imsi ="";
        List<SimInfo> userSimInfoList = userDevice.getSimInfo();

        if(CollectionUtils.isNotEmpty(userSimInfoList)){
            for(SimInfo simInfo : userSimInfoList){
                if(StringUtils.isNotBlank(simInfo.getImsiNumber())){
                    if(StringUtils.isNotBlank(imsi)){
                        imsi = imsi.concat(",");
                    }
                    imsi = imsi.concat(simInfo.getImsiNumber());
                }
            }
        }
        wcfMsisdnIdentificationRequestBody.setImsi(imsi);

        return wcfMsisdnIdentificationRequestBody;
    }

    public List<String> getImsiInfo(UserDevice userSimInfoList){
        if(ObjectUtils.isEmpty(userSimInfoList.getSimInfo())){
            return null;
        }else{
            return userSimInfoList.getSimInfo().stream().filter(e -> StringUtils.isNotBlank(e.getImsiNumber())).map(e ->e.getImsiNumber()).collect(Collectors.toList());
        }
    }

    public boolean isNotExpired(User user) {
        return user.getUserSubscription() != null && user.getUserSubscription().getProdIds() != null && user.getUserSubscription().getProdIds().stream().anyMatch(e -> e.getEts() > System.currentTimeMillis());
    }

    public WCFPromoCodeRequestBody createPromoCodeRequest(String uid,String coupon,String deviceId){
        WCFPromoCodeRequestBody wcfPromoCodeRequestBody = new WCFPromoCodeRequestBody();
        wcfPromoCodeRequestBody.setCoupon(coupon);
        wcfPromoCodeRequestBody.setDeviceId(deviceId);
        wcfPromoCodeRequestBody.setUid(uid);
        return wcfPromoCodeRequestBody;
    }

    public WCFOfferIDProvisionedRequestBody createWCFOfferIDProvisioned(String uid,UserDevice userDevice,String msisdn,List<Integer> offerIdList){
        WCFOfferIDProvisionedRequestBody wcfOfferIDProvisionedRequestBody = new WCFOfferIDProvisionedRequestBody();
        wcfOfferIDProvisionedRequestBody.setDeviceId(userDevice.getDeviceId());
        wcfOfferIDProvisionedRequestBody.setMsisdn(msisdn);

        wcfOfferIDProvisionedRequestBody.setOfferIds(offerIdList);
        wcfOfferIDProvisionedRequestBody.setUid(uid);

        return wcfOfferIDProvisionedRequestBody;
    }

    public Boolean isOtpRequiredForCarrierBillingPayment(Boolean isCarrierBillingAllow,String currentDeviceId,User user){
        if(isCarrierBillingAllow == null || !isCarrierBillingAllow){
            return Boolean.FALSE;
        }

        if(StringUtils.isBlank(currentDeviceId)){
            return Boolean.TRUE;
        }

        List<UserDevice> userDeviceList = user.getDevices();
        if(CollectionUtils.isEmpty(userDeviceList)){
            return Boolean.TRUE;
        }

        for(UserDevice userDevice : userDeviceList){
            if(userDevice.getDeviceId().equalsIgnoreCase(currentDeviceId)){
                if(userDevice.getRegistrationChannel() == null){
                    return Boolean.FALSE;
                }

                if(userDevice.getRegistrationChannel().getVerifiedUser() != null && userDevice.getRegistrationChannel().getVerifiedUser()){
                    return Boolean.FALSE;
                }
            }
        }

        return Boolean.TRUE;
    }

    public JSONObject createPaymentChannelResponse(MusicNotificationType buttonText, String url, Boolean openWebView, Boolean openPopUp, Boolean showOtpScreen, String title,
                                                   String subTitle, Language userLang, String httpMethod, SubscriptionChannelPhases subscriptionChannelPhases,Boolean isAutorenewal,String pricePoint){

        AccountPaymentResponseDTO accountPaymentResponseDTO = new AccountPaymentResponseDTO();
        if(StringUtils.isNotBlank(url)) {
            accountPaymentResponseDTO.setUrl(url);
        }
        accountPaymentResponseDTO.setOpenPopUp(openPopUp);
        accountPaymentResponseDTO.setOpenWebView(openWebView);
        accountPaymentResponseDTO.setShowOtpScreen(showOtpScreen);

        if(buttonText != null){
            accountPaymentResponseDTO.setButtonText(NotificationsText.getLangNotificationWithParams(buttonText,userLang,null));
        }
        if(StringUtils.isNotBlank(title)) {
            accountPaymentResponseDTO.setTitle(title);
        }
        if(StringUtils.isNotBlank(subTitle)) {
            accountPaymentResponseDTO.setSubTitle(subTitle);
        }
        accountPaymentResponseDTO.setButtonColour(MusicConstants.BUTTON_COLOUR);
        accountPaymentResponseDTO.setSubTitleColour(MusicConstants.SUBTITLE_COLOUR);
        accountPaymentResponseDTO.setTitleColour(MusicConstants.SUBTITLE_COLOUR);
        accountPaymentResponseDTO.setHttpMethod(httpMethod);

        if(subscriptionChannelPhases != null){
            if(subscriptionChannelPhases.getNativePayment() != null){
                accountPaymentResponseDTO.setNativePayment(subscriptionChannelPhases.getNativePayment());
            }
            if(StringUtils.isNotBlank(subscriptionChannelPhases.getIconUrl())){
                accountPaymentResponseDTO.setIconUrl(subscriptionChannelPhases.getIconUrl());
            }
            if(StringUtils.isNotBlank(subscriptionChannelPhases.getRequestMandatoryBodyParameter())){
                accountPaymentResponseDTO.setRequestBodyMandatoryParameter(subscriptionChannelPhases.getRequestMandatoryBodyParameter());
            }
            if(StringUtils.isNotBlank(subscriptionChannelPhases.getRequestOptionalBodyParameter())){
                accountPaymentResponseDTO.setRequestBodyOptionalParameter(subscriptionChannelPhases.getRequestOptionalBodyParameter());
            }
            if(StringUtils.isNotBlank(subscriptionChannelPhases.getPaymentName())){
                accountPaymentResponseDTO.setPaymentName(subscriptionChannelPhases.getPaymentName());
            }
            if(StringUtils.isNotBlank(subscriptionChannelPhases.getBillingText())){
                accountPaymentResponseDTO.setBillingText(subscriptionChannelPhases.getBillingText());
            }
        }
        if(isAutorenewal != null){
            accountPaymentResponseDTO.setAutoRenewal(isAutorenewal);
        }
        if(StringUtils.isNotBlank(pricePoint)){
            accountPaymentResponseDTO.setPricePoint(pricePoint);
        }
        return accountPaymentResponseDTO.toJsonObject();
    }

    @Deprecated
    public WCFCarrierBillingRequestBody createWCFCarrierBillingRequestBody(String tid, String msisdn){
        WCFCarrierBillingRequestBody wcfCarrierBillingRequestBody = new WCFCarrierBillingRequestBody();
        wcfCarrierBillingRequestBody.setTid(tid);
        wcfCarrierBillingRequestBody.setMsisdn(msisdn);
        return wcfCarrierBillingRequestBody;
    }

    public Map<String,String> getHeaderMap(String httpMethod , String requestUri, String requestBody){
        Map<String,String> headerMap = new HashMap();
        String timestamp = String.valueOf(System.currentTimeMillis());
        requestBody = org.apache.commons.lang3.StringUtils.isBlank(requestBody) ? "" : requestBody;
        String signature = createSignature(httpMethod,requestUri,requestBody,timestamp, musicConfig.getWcfApiSecretKey());
        headerMap.put(WCFApisConstants.X_BSY_DATE_KEY,timestamp);
        headerMap.put(WCFApisConstants.X_BSY_ATKN_KEY, musicConfig.getWcfApiAppId().concat(":").concat(signature));
        headerMap.put("Content-Type", "application/json");
        return headerMap;
    }
}
