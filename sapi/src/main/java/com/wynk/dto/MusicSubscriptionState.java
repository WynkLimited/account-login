package com.wynk.dto;

import static com.wynk.constants.MusicSubscriptionPackConstants.*;

import com.wynk.config.MusicConfig;
import com.wynk.constants.JsonKeyNames;
import com.wynk.common.Language;
import com.wynk.constants.MusicConstants;
import com.wynk.music.constants.MusicNotificationType;
import com.wynk.constants.MusicSubscriptionPackConstants;
import com.wynk.musicpacks.FUPPack;
import com.wynk.notification.NotificationsText;
import com.wynk.server.ChannelContext;
import com.wynk.user.dto.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;


public enum MusicSubscriptionState {

    NEVER_SUBSCRIBED {

        @Override
        public MusicSubscriptionStatus getNotificationMessages(MusicSubscriptionStatus subStatus, int price, String lang) {
            if(!"hi".equals(lang)) {
                subStatus.setHeaderMessage("Unlimited Download Subscription");
                subStatus.setMessage("Get Wynk subscription to download unlimited songs and play them without internet saving your data costs.");
                subStatus.setButtonText("Try Free!");
                subStatus.setStatusMessage("None");
            }
            else {
                subStatus.setHeaderMessage("अनलिमिटेड डाउनलोड सब्स्क्रिप्शन");
                subStatus.setMessage("अनलिमिटेड गाने डाउनलोड करने और अपना डाटा खर्च बचाकर बिना इंटरनेट के गाने सुनने के लिए विंक सब्स्क्राइब करें.");
                subStatus.setButtonText("ट्राइ फ्री!");
                subStatus.setStatusMessage("कोई नहीं");
            }
            subStatus.setStatusMessageColor("#5f5f5f");
            return subStatus;
        }

        @Override
        public long getCurrentStateExpireTimestamp(long subscriptionExpireTimestmp) {
            return 0L;
        }
        
        @Override
        public MusicSubscriptionStatus getAccountMessages(MusicSubscriptionStatus subStatus, User user, MusicConfig musicConfig) {
            JSONObject subscriptionObject = new JSONObject();
            JSONArray infoArray = new JSONArray();

            Map<String, String> paramValues = new HashMap<>();
            String lang = ChannelContext.getLang();
            if(lang.equalsIgnoreCase("en"))
            {
                if(user != null)
                    lang = user.getLang();
            }
            Language userLang = Language.getLanguageById(lang);
            infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.WYNK_FREE_INFO1,
                    userLang, paramValues));
            infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.WYNK_FREE_INFO2, userLang, paramValues));
//            if(StringUtils.isNotBlank(user.getOperator()) && "airtel".contains(user.getOperator().toLowerCase()) && musicConfig.isMusicSixMonthsOfferOn()) {
//                infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.WYNK_NEW_FREE_INFO_OFFER_NOT_AVAILED, userLang, paramValues));
//            }
//            else if(!subStatus.isOfferPackAvailed())
//            {
//                infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.WYNK_FREE_INFO_OFFER_NOT_AVAILED, userLang, paramValues));
//            }
            subscriptionObject.put(NotificationsText.getLangNotificationWithParams(MusicNotificationType.INFO, userLang, paramValues), infoArray);
            JSONArray infoKeysArray = new JSONArray();
            infoKeysArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.INFO, userLang, paramValues));
            subscriptionObject.put(JsonKeyNames.PACK_INFO_KEYS, infoKeysArray);
            subscriptionObject.put(JsonKeyNames.STATUS_MESSAGE, "");
            subscriptionObject.put(JsonKeyNames.STATUS_MESSAGE_COLOR, subStatus.getStatusMessageColor());
            subscriptionObject.put(JsonKeyNames.SUBSCRIPTION_PACK_INFO, "");
            subStatus.setSubscriptionInfo(subscriptionObject);
            return subStatus;
        }
    },

    SUBSCRIBED_PRE_REMINDER {

        @Override
        public MusicSubscriptionStatus getNotificationMessages(MusicSubscriptionStatus subStatus, int price, String lang) {
            String date = musicAppDateFormat.format(new Date(subStatus.getExpireTimestamp() - 1L));
            if(!"hi".equals(lang)) {
                subStatus.setHeaderMessage("Subscription pack of Rs " + price + " is Active");
                subStatus.setMessage("Your subscription is valid till " + date + ". Download and play songs without internet to save your data costs.");
                subStatus.setStatusMessage("Active");
            }
            else {
                subStatus.setHeaderMessage(price + " रुपये का सब्स्क्रिप्शन पैक एक्टिव है");
                subStatus.setMessage("आपका सब्स्क्रिप्शन " + date + " तक वैध है. गाने डाउनलोड करें और अपने डाटा खर्च को बचाने के​ लिए बिना इंटरनेट के सुनें.");
                subStatus.setStatusMessage("एक्टिव");
            }
            subStatus.setStatusMessageColor("#5fa81c");
            return subStatus;
        }

        @Override
        public long getCurrentStateExpireTimestamp(long subscriptionExpireTimestmp) {
            return subscriptionExpireTimestmp - PRE_REMINDER_PERIOD * DAY;
        }
        
        @Override
        public MusicSubscriptionStatus getAccountMessages(MusicSubscriptionStatus subStatus, User user, MusicConfig musicConfig) {
            return getActiveSubscriptionStatus(subStatus, user, musicConfig);
        }
        
    },

    SUBSCRIBED_IN_REMINDER {

        @Override
        public MusicSubscriptionStatus getNotificationMessages(MusicSubscriptionStatus subStatus, int price, String lang) {
            String date = musicAppDateFormat.format(new Date(subStatus.getExpireTimestamp() - 1L));
            if(!"hi".equals(lang)) {
                subStatus.setHeaderMessage("Subscription pack of Rs " + price + " is Active");
                if(subStatus.isAutoRenewalOn()) {
                    subStatus.setNotificationMessage("Your subscription will be renewed on " + date + ".");
                    subStatus.setMessage("Your subscription will be renewed on " + date + ".");
                    subStatus.setStatusMessage("Active");
                }
                else {
                    subStatus.setNotificationMessage("Your subscription will expire on " + date + ".");
                    subStatus.setMessage("Your subscription will expire on " + date + ".");
                    subStatus.setStatusMessage("Active");
                }
            }
            else {
                subStatus.setHeaderMessage(price + " रुपये का सब्स्क्रिप्शन पैक एक्टिव है");
                if(subStatus.isAutoRenewalOn()) {
                    subStatus.setNotificationMessage("आपका सब्स्क्रिप्शन " + date + " को रिन्यू होगा.");
                    subStatus.setMessage("आपका सब्स्क्रिप्शन " + date + " को रिन्यू होगा.");
                    subStatus.setStatusMessage("एक्टिव");
                }
                else {
                    subStatus.setNotificationMessage("आपका सब्स्क्रिप्शन " + date + " को ख़त्म हो जाएगा.");
                    subStatus.setMessage("आपका सब्स्क्रिप्शन " + date + " को समाप्त होगा.");
                    subStatus.setStatusMessage("एक्टिव");
                }
            }
            subStatus.setStatusMessageColor("#5fa81c");
            return subStatus;
        }

        @Override
        public long getCurrentStateExpireTimestamp(long subscriptionExpireTimestmp) {
            return subscriptionExpireTimestmp;
        }
        
        @Override
        public MusicSubscriptionStatus getAccountMessages(MusicSubscriptionStatus subStatus, User user, MusicConfig musicConfig) {
            return getActiveSubscriptionStatus(subStatus, user, musicConfig);
        }
    },

    SUBSCRIBED_GRACE_EXCEEDED {

        @Override
        public MusicSubscriptionStatus getNotificationMessages(MusicSubscriptionStatus subStatus, int price, String lang) {
            String date = musicAppDateFormat.format(new Date(getCurrentStateExpireTimestamp(subStatus.getExpireTimestamp() - 1L)));
            if(!"hi".equals(lang)) {
                subStatus.setHeaderMessage("Subscription pack of Rs " + price + " is in Suspension period");
                if(subStatus.isAutoRenewalOn()) {
                    subStatus.setMessage("Your subscription could not be renewed. We will attempt again to renew it. "
                            + "Please ensure sufficient balance in your account. You will neither be able to download more songs nor play "
                            + "already downloaded songs without internet. You will not be able to access your downloaded songs after " + date + ".");
                    subStatus.setStatusMessage("Suspended");
                }
                else {
                    subStatus.setMessage("You will neither be able to download more songs nor play already downloaded songs without "
                            + "internet. You will not be able to access your downloaded songs after " + date + ".");
                    subStatus.setButtonText("Renew");
                    subStatus.setStatusMessage("Suspended");
                }
                subStatus.setNotificationMessage("Your subscription is in Suspension period.");
            }
            else {
                subStatus.setHeaderMessage(price + " रुपये का सब्स्क्रिप्शन पैक सस्पेन्शन पीरियड में है");
                if(subStatus.isAutoRenewalOn()) {
                    subStatus.setMessage("आपका सब्स्क्रिप्शन रिन्यू नहीं हो पाया है. हम इसे रिन्यू करने की फिर से कोशिश करेंगे. कृपया अपने अकाउंट में आवश्यक बैलेंस जरूर रखें. " + date
                            + " के बाद आप और गाने डाउनलोड नहीं कर पाएंगे और  पहले से डाउनलोड किए गए गाने बिना इंटरनेट के नहीं सुन सकेंगे");
                    subStatus.setStatusMessage("सस्पेन्ड");
                }
                else {
                    subStatus.setMessage("आप और गाने डाउनलोड नहीं कर पाएंगे और पहले से डाउनलोड किए गए गाने आप बिना इंटरनेट के नहीं सुन सकेंगे");
                    subStatus.setButtonText("रिन्यू");
                    subStatus.setStatusMessage("सस्पेन्ड");
                }
                subStatus.setNotificationMessage("आपका सब्स्क्रिप्शन अब सस्पेन्शन पीरियड में है.");
            }
            subStatus.setStatusMessageColor("#ed2324");
            return subStatus;
        }

        @Override
        public long getCurrentStateExpireTimestamp(long subscriptionExpireTimestmp) {
            return subscriptionExpireTimestmp + (GRACE_PERIOD + SUSPENTION_PERIOD) * DAY;
        }

        @Override
        public MusicSubscriptionStatus getAccountMessages(MusicSubscriptionStatus subStatus, User user, MusicConfig musicConfig) {
            return getInActiveSubscriptionStatus(subStatus, user, musicConfig);
        }
    },

    SUSPENDED {

        @Override
        public MusicSubscriptionStatus getNotificationMessages(MusicSubscriptionStatus subStatus, int price, String lang) {
            if(!"hi".equals(lang)) {
                subStatus.setHeaderMessage("Unlimited Download Subscription");
                subStatus.setMessage("Get Wynk subscription to download unlimited songs and play them without internet saving your data costs.");
                subStatus.setButtonText("Purchase!");
                subStatus.setStatusMessage("Inactive");
            }
            else {
                subStatus.setHeaderMessage("अनलिमिटेड डाउनलोड सब्स्क्रिप्शन");
                subStatus.setMessage("अनलिमिटेड गाने डाउनलोड करने और अपना डाटा खर्च बचाकर बिना इंटरनेट के गाने सुनने के लिए विंक सब्स्क्राइब करें.");
                subStatus.setButtonText("खरीदें!");
                subStatus.setStatusMessage("असक्रिय");
            }
            subStatus.setStatusMessageColor("#ed2324");
            return subStatus;
        }

        @Override
        public long getCurrentStateExpireTimestamp(long subscriptionExpireTimestmp) {
            return 0L;
        }
        
        @Override
        public MusicSubscriptionStatus getAccountMessages(MusicSubscriptionStatus subStatus, User user, MusicConfig musicConfig) {
            JSONObject subscriptionObject = new JSONObject();
            JSONArray infoArray = new JSONArray();

            Map<String, String> paramValues = new HashMap<>();
            String lang = ChannelContext.getLang();
            if(lang.equalsIgnoreCase("en"))
            {
                if(user != null)
                    lang = user.getLang();
            }
            Language userLang = Language.getLanguageById(lang);
            infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.WYNK_FREE_INFO_OFFER_AVAILED, userLang, paramValues));
            infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.WYNK_FREE_INFO1, userLang, paramValues));
            infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.WYNK_FREE_INFO2, userLang, paramValues));
            
//            if(StringUtils.isNotBlank(user.getOperator()) && "airtel".contains(user.getOperator().toLowerCase())) {
//                infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.WYNK_FREE_INFO_OFFER_NOT_AVAILED, userLang, paramValues));
//            }
            
            subscriptionObject.put(NotificationsText.getLangNotificationWithParams(MusicNotificationType.INFO, userLang, paramValues), infoArray);

            JSONArray infoKeysArray = new JSONArray();
            infoKeysArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.INFO, userLang, paramValues));
            subscriptionObject.put(JsonKeyNames.PACK_INFO_KEYS, infoKeysArray);

            subStatus.setSubscriptionInfo(subscriptionObject);
            return subStatus;
        }
    };

    public static final String START_STOP_INITIATED_DE_SUBSCRIPTION_SUCCESS = "Start/Stop Initiated De-Subscription Success";

    public static final String WAIVER_INITIATED_DE_SUBSCRIPTION_SUCCESS     = "VAS Waiver initiated De-Subscription Success";

    private static final SimpleDateFormat musicAppDateFormat                           = new SimpleDateFormat("dd MMM yyyy");

    private static final Map<Integer, MusicSubscriptionState> codeToStateMap;

    public static final List<Integer> failureCodes                                 = Arrays.asList(0, 4, 33, 37);

    public abstract MusicSubscriptionStatus getNotificationMessages(MusicSubscriptionStatus status, int price, String lang);
    
    public abstract MusicSubscriptionStatus getAccountMessages(MusicSubscriptionStatus status, User user, MusicConfig musicConfig);

    public abstract long getCurrentStateExpireTimestamp(long subscriptionExpireTimestmp);

    static {
        Map<Integer, MusicSubscriptionState> tempMap = new HashMap<>();
        tempMap.put(1, SUBSCRIBED_PRE_REMINDER);
        tempMap.put(13, SUSPENDED);
        codeToStateMap = Collections.unmodifiableMap(tempMap);
    }

    @Deprecated
    public static MusicSubscriptionState getStateBasedOnIBMNotificationCode(CPNotificationDTO notification, long lastSubscribedTimestamp) {
        if(failureCodes.contains(notification.getErrorCode())) {
            return getCurrentSubscriptionState(lastSubscribedTimestamp, notification.getProductId());
        }
        else if(13 == notification.getErrorCode()) {
            if(START_STOP_INITIATED_DE_SUBSCRIPTION_SUCCESS.equals(notification.getErrorMessage()) || WAIVER_INITIATED_DE_SUBSCRIPTION_SUCCESS.equals(notification.getErrorMessage())) {
                return getCurrentSubscriptionState(lastSubscribedTimestamp, notification.getProductId());
            }
            else {
                return SUSPENDED;
            }
        }
        return codeToStateMap.get(notification.getErrorCode());
    }

    public static int getPackValidityFromProductId(int productId) {
        Integer validity = MusicSubscriptionPackConstants.PRODUCT_ID_VALIDITY_MAP.get(productId);
        return validity != null ? validity : PACK_VALIDITY;
    }

    @Deprecated
    public static MusicSubscriptionState getCurrentSubscriptionStateForIBM(long lastSubscribedTimestamp, long startTime, int productId) {
        if(0L == lastSubscribedTimestamp) {
            return NEVER_SUBSCRIBED;
        }
        if(startTime + 60000 < lastSubscribedTimestamp) {
            return NEVER_SUBSCRIBED;
        }

        int validity = getPackValidityFromProductId(productId);

        long elapsed = startTime - lastSubscribedTimestamp;
        float days = elapsed / DAY;
        if(days < (validity - PRE_REMINDER_PERIOD)) {
            return SUBSCRIBED_PRE_REMINDER;
        }
        else if(days < validity) {
            return SUBSCRIBED_IN_REMINDER;
        }
        else {
            return SUSPENDED;
        }
    }
    
    public static MusicSubscriptionState getCurrentSubscriptionState(long lastSubscribedTimestamp, long startTime, int productId) {
        if(0L == lastSubscribedTimestamp) {
            return NEVER_SUBSCRIBED;
        }
        if(startTime + 1000 < lastSubscribedTimestamp) {
            return NEVER_SUBSCRIBED;
        }
        
        int validity = getPackValidityFromProductId(productId);
        
        long elapsed = startTime - lastSubscribedTimestamp;
        float days = elapsed / DAY;
        if(days < (validity - PRE_REMINDER_PERIOD)) {
            return SUBSCRIBED_PRE_REMINDER;
        }
        else if(days < validity) {
            return SUBSCRIBED_IN_REMINDER;
        }
        else {
            return SUSPENDED;
        }
    }
    
    public static MusicSubscriptionState getCurrentSubscriptionState(long lastSubscribedTimestamp, int productId) {
        return getCurrentSubscriptionState(lastSubscribedTimestamp, System.currentTimeMillis(), productId);
    }
    
    public static MusicSubscriptionStatus getActiveSubscriptionStatus(MusicSubscriptionStatus subStatus, User user,MusicConfig musicConfig) {
        JSONObject subscriptionObject = new JSONObject();
        Map<String, String> paramValues = new HashMap<>();
        String lang = ChannelContext.getLang();
        if(lang.equalsIgnoreCase("en"))
        {
            if(user != null)
                lang = user.getLang();
        }
        Language userLang = Language.getLanguageById(lang);
        
        JSONArray infoKeysArray = new JSONArray();
        JSONArray benefitArray = new JSONArray();
        benefitArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.BENEFIT_1, userLang, paramValues));
        infoKeysArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.BENEFIT, userLang, paramValues));
        if(MusicSubscriptionPackConstants.AIRTEL_FREE_DATA_MUSIC_PACK_PRICE == subStatus.getPrice()) {
            JSONArray statusArray = new JSONArray();
            int totalLimit = musicConfig.getAirtel99Limit();
            paramValues.put(MusicConstants.TOTAL_KEY, String.valueOf(totalLimit));
            benefitArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.BENEFIT_129_2, userLang, paramValues));
            infoKeysArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.STATUS, userLang, paramValues));
            FUPPack fupPack = user.getFupPack();
            int currentCount = fupPack.getStreamedCount() + fupPack.getRentalsCount();
            int leftQuota = Math.max((totalLimit - currentCount), 0);
            paramValues.put(MusicConstants.REMAINING_KEY, String.valueOf(leftQuota));
            long remainingTime = subStatus.getExpireTimestamp() - System.currentTimeMillis();
            int remainingDays = (int) (remainingTime / (DAY)) + 1; 
            paramValues.put(MusicConstants.REMAINING_DAYS_KEY, String.valueOf(remainingDays));
            statusArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.WYNK_129_STATUS, userLang, paramValues));
            subscriptionObject.put(NotificationsText.getLangNotificationWithParams(MusicNotificationType.STATUS, userLang, paramValues), statusArray);
        }
        else
        {
            benefitArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.BENEFIT_29_2, userLang, paramValues));
        }
        subscriptionObject.put(NotificationsText.getLangNotificationWithParams(MusicNotificationType.BENEFIT, userLang, paramValues), benefitArray);


        JSONArray infoArray = new JSONArray();
        int productId = subStatus.getProductId();
        if(subStatus.isUnsubscribed && MusicSubscriptionPackConstants.RECURRING_SUBSCRIPTION_MUSIC_PACKS.contains(productId))
        {
            infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.WYNK_FUP_UNSUBS_INFO1, userLang, paramValues));
            infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.WYNK_FUP_UNSUBS_INFO2, userLang, paramValues));
        }
        else if(MusicSubscriptionPackConstants.RECURRING_SUBSCRIPTION_MUSIC_PACKS.contains(productId))
        {
            String date = musicAppDateFormat.format(new Date(subStatus.getExpireTimestamp()));
            paramValues.put(JsonKeyNames.VALID_TILL, date);
            infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.WYNK_FUP_SUBS_INFO1, userLang, paramValues));
        }
        else if(MusicSubscriptionPackConstants.ALL_ITUNES_PACKS.contains(productId)) {
            infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.ITUNES_RENEW, userLang, paramValues));
        }
        else if(MusicSubscriptionPackConstants.PROMOTIONAL_AUTORENEW_MUSIC_PACKS.contains(productId)) {
            // Removing auto renew line for all promotional auto renewing packs
        	// Adding Not been charged for this subscription line for all promotional packs
        	infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.NOT_BEEN_CHARGED, userLang, paramValues));
        }
        else{
        	 infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.WYNK_FUP_UNSUBS_INFO2, userLang, paramValues));
        }
        subscriptionObject.put(NotificationsText.getLangNotificationWithParams(MusicNotificationType.INFO, userLang, paramValues), infoArray);
        infoKeysArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.INFO, userLang, paramValues));
        subscriptionObject.put(JsonKeyNames.PACK_INFO_KEYS, infoKeysArray);
        subscriptionObject.put(JsonKeyNames.STATUS_MESSAGE, NotificationsText.getLangNotificationWithParams(MusicNotificationType.ACTIVE, userLang, paramValues));
        subscriptionObject.put(JsonKeyNames.STATUS_MESSAGE_COLOR, subStatus.getStatusMessageColor());
        
        String date = musicAppDateFormat.format(new Date(subStatus.getExpireTimestamp() - 1L));
        paramValues.put(JsonKeyNames.VALID_TILL, date);
        
        // Remove valid till line for all promotional packs
        if(!MusicSubscriptionPackConstants.PROMOTIONAL_AUTORENEW_MUSIC_PACKS.contains(productId)) {
            subscriptionObject.put(JsonKeyNames.VALID_TILL, NotificationsText.getLangNotificationWithParams(MusicNotificationType.SUBS_VALID_TILL, userLang, paramValues));
        }
        
        //Placeholder
        subscriptionObject.put(JsonKeyNames.SUBSCRIPTION_PACK_INFO, "");
        subStatus.setSubscriptionInfo(subscriptionObject);
        return subStatus;
    }

    @Deprecated
    public static MusicSubscriptionStatus getInActiveSubscriptionStatus(MusicSubscriptionStatus subStatus, User user, MusicConfig musicConfig) {
        String date = musicAppDateFormat.format(new Date(subStatus.getExpireTimestamp() - 1L));
        JSONObject subscriptionObject = new JSONObject();
        Map<String, String> paramValues = new HashMap<>();
        String lang = ChannelContext.getLang();
        if(lang.equalsIgnoreCase("en"))
        {
            if(user != null)
                lang = user.getLang();
        }
        Language userLang = Language.getLanguageById(lang);
        JSONArray infoArray = new JSONArray();
        
        infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.WYNK_FREE_INFO1, userLang, paramValues));
        long graceTS = subStatus.getExpireTimestamp() + GRACE_PERIOD * DAY;
        long subscriptionEndTS = subStatus.getExpireTimestamp() + (GRACE_PERIOD + SUSPENTION_PERIOD) * DAY;
        JSONArray infoKeysArray = new JSONArray();
        String graceDate = musicAppDateFormat.format(new Date(graceTS - 1L));
        String subscriptionEndDate = musicAppDateFormat.format(new Date(subscriptionEndTS - 1L));
        paramValues.put(JsonKeyNames.VALID_FROM, graceDate);
        paramValues.put(JsonKeyNames.VALID_TILL, subscriptionEndDate);
        // Removing the line telling user that his rentals will be removed
        // infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.INACTIVE_INFO_2, userLang, paramValues));
        if(subStatus.isUnsubscribed)
        {
            infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.INACTIVE_UNSUBSCRIBED_INFO, userLang, paramValues));
        }
        else
        {
            infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.INACTIVE_SUBSCRIBED_INFO, userLang, paramValues));
        }
        
        
        if(MusicSubscriptionPackConstants.AIRTEL_FREE_DATA_MUSIC_PACK_PRICE == subStatus.getPrice()) {
            JSONArray statusArray = new JSONArray();
            int totalLimit = musicConfig.getAirtel99Limit();
            paramValues.put(MusicConstants.TOTAL_KEY, String.valueOf(totalLimit));

            FUPPack fupPack = user.getFupPack();
            int currentCount = fupPack.getStreamedCount() + fupPack.getRentalsCount();
            int leftQuota = Math.max((totalLimit - currentCount), 0);
            paramValues.put(MusicConstants.REMAINING_KEY, String.valueOf(leftQuota));
            
            paramValues.put(MusicConstants.REMAINING_KEY, String.valueOf(leftQuota));
        }
        infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.INACTIVE_INFO_3, userLang, paramValues));
        if(subStatus.getExpireTimestamp() > 0){
        	subscriptionObject.put(JsonKeyNames.STATUS_MESSAGE_COLOR, subStatus.getStatusMessageColor());
        	subscriptionObject.put(JsonKeyNames.STATUS_MESSAGE, NotificationsText.getLangNotificationWithParams(MusicNotificationType.INACTIVE, userLang, paramValues));
        	// subscriptionObject.put(JsonKeyNames.VALID_TILL, NotificationsText.getLangNotificationWithParams(MusicNotificationType.SUBS_VALID_TILL, userLang, paramValues));
        }
        
        // WYNK 6 month free offer and case of offer pack availed changed manually
//        if(musicConfig.isMusicSixMonthsOfferOn() && !subStatus.isAutoRenewalOn() && StringUtils.isNotBlank(user.getOperator()) &&  "airtel".contains(user.getOperator().toLowerCase()) && !MusicSubscriptionPackConstants.ALL_ITUNES_PACKS.contains(subStatus.getProductId())) {
//            infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.WYNK_NEW_FREE_INFO_OFFER_NOT_AVAILED, userLang, paramValues));
//        }
//        else if(!subStatus.isOfferPackAvailed())
//        {
//            infoArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.WYNK_FREE_INFO_OFFER_NOT_AVAILED, userLang, paramValues));
//        }
        
        subscriptionObject.put(NotificationsText.getLangNotificationWithParams(MusicNotificationType.INFO, userLang, paramValues), infoArray);
        
        infoKeysArray.add(NotificationsText.getLangNotificationWithParams(MusicNotificationType.INFO, userLang, paramValues));
        subscriptionObject.put(JsonKeyNames.PACK_INFO_KEYS, infoKeysArray);
        //        subscriptionObject.put(JsonKeyNames.VALID_TILL, validTill);
        subscriptionObject.put(JsonKeyNames.SUBSCRIPTION_PACK_INFO, "");
        
        
        subStatus.setSubscriptionInfo(subscriptionObject);
        return subStatus;
    }

}
