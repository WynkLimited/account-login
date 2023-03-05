package com.wynk.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.wynk.adtech.AdService;
import com.wynk.common.*;
import com.wynk.config.MusicConfig;
import com.wynk.constants.*;
import com.wynk.db.MongoDBManager;
import com.wynk.db.S3StorageService;
import com.wynk.db.ShardedRedisServiceManager;
import com.wynk.dto.*;
import com.wynk.enums.*;
import com.wynk.music.WCFServiceType;
import com.wynk.music.constants.*;
import com.wynk.music.dto.*;
import com.wynk.music.service.MusicContentService;
import com.wynk.newcode.user.core.dao.UserRecentSongDAO;
import com.wynk.newcode.user.core.entity.RecentSong;
import com.wynk.newcode.user.core.service.UserSongService;
import com.wynk.notification.MusicNotificationConstants;
import com.wynk.notification.NotificationsText;
import com.wynk.server.ChannelContext;
import com.wynk.user.dto.*;
import com.wynk.utils.*;
import com.wynk.wcf.WCFApisService;
import com.wynk.wcf.WCFApisConstants;
import com.wynk.wcf.WCFApisUtils;
import com.wynk.wcf.dto.FeatureType;
import com.wynk.wcf.dto.UserSubscription;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisCluster;
import scala.collection.mutable.StringBuilder;

import javax.annotation.PostConstruct;
import java.lang.Math;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.wynk.constants.JsonKeyNames.*;
import static com.wynk.constants.MusicConstants.*;
import static com.wynk.constants.MusicConstants.OS;
import static com.wynk.utils.MusicUtils.getCountryDetailsBasedOnCountry;
import static com.wynk.wcf.WCFApisConstants.*;

/**
 * Created with IntelliJ IDEA.
 * User: bhuvangupta
 * Date: 21/11/13
 * Time: 1:21 AM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Service
public class MusicService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(MusicService.class.getCanonicalName());
    private static final Logger mactivityLogger = LoggerFactory.getLogger("mactivityanalytics");

    @Autowired private MusicConfig musicConfig;
    @Autowired private ShardedRedisServiceManager musicShardRedisServiceManager;
    @Autowired private ShardedRedisServiceManager userPersistantRedisServiceManager;
    @Autowired private MongoDBManager mongoMusicDBManager;
    @Autowired private MongoDBManager mongoUserDBManager;
    @Autowired private GeoDBService geoDBService;
    @Autowired private AccountService accountService;
    @Autowired private AdService adService;
    @Autowired private UserConfigService userConfigService;
    @Autowired private MusicContentService musicContentService;
    @Autowired private MyAccountService myAccountService;
    @Autowired private WCFUtils wcfUtils;
    @Autowired private UserUpdateService userUpdateService;
    @Autowired private IntlRoamingService intlRoamingService;
    @Autowired private AccountRegistrationService accountRegistrationService;
    @Autowired private SubscriptionIntentService subscriptionIntentService;
    @Autowired protected S3StorageService s3ServiceManager;
    @Autowired protected KafkaProducer kafkaProducerManager;
    @Autowired private UserRecentSongDAO userSongDAO;
    @Autowired private UserSongService userSongService;

    @Autowired
    private JedisCluster wcfPayRedisCluster;

    @Autowired
    private WCFApisService wcfApisService;

    @Autowired
    private WCFApisUtils wcfApisUtils;

    private MusicMonitoringManager statsMonitoring;
    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private static final String USER_PLAYLIST_COLLECTION = "userplaylists";
    public static String BASE_CMS_SEARCH_SUGGEST_API;
    //search suggest
    public static String BASE_CMS_UNISEARCH_API;
    public static String BASE_CMS_MLT_API;
    private static final int ON_BOARDING_NONE = 0;
    private static final int ON_BOARDING_BENEFITS = 1;
    private static final int ON_BOARDING_LANGUAGE_SELECTED  = 3;
    private static final int ON_BOARDING_LANGUAGE_NOT_SELECTED  = 2;

    // TODO - Remove dependencies and remove this line
    public static String BASE_CMS_API = "";
    public static JSONObject BUFFER_UTILS_CONFIG = new JSONObject();
    public static JSONObject NETWORK_TO_BITRATE_MAP = new JSONObject();

    static {

        JSONObject dsPolicy = new JSONObject();
        dsPolicy.put(JsonKeyNames.NETWORK_BOUNDRY, MusicConstants.DATA_SAVE_NETWORK_BOUNDRY);
        dsPolicy.put(JsonKeyNames.ABOVE_NETWORK_BOUNDRY, MusicConstants.DATA_SAVE_ABOVE_NETWORK_BOUNDRY_IN_SECONDS);
        dsPolicy.put(JsonKeyNames.BELOW_NETWORK_BOUNDRY, MusicConstants.DATA_SAVE_BELOW_NETWORK_BOUNDRY_IN_SECONDS);
        BUFFER_UTILS_CONFIG.put(JsonKeyNames.DS_BUFFER_POLICY, dsPolicy);

        JSONObject normalPolicy = new JSONObject();
        normalPolicy.put(JsonKeyNames.NETWORK_BOUNDRY, MusicConstants.NORMAL_NETWORK_BOUNDRY);
        normalPolicy.put(JsonKeyNames.ABOVE_NETWORK_BOUNDRY, MusicConstants.NORMAL_ABOVE_NETWORK_BOUNDRY_IN_SECONDS);
        normalPolicy.put(JsonKeyNames.BELOW_NETWORK_BOUNDRY, MusicConstants.NORMAL_BELOW_NETWORK_BOUNDRY_IN_SECONDS);
        BUFFER_UTILS_CONFIG.put(JsonKeyNames.NORMAL_BUFFER_POLICY, normalPolicy);

        JSONObject prefetch = new JSONObject();
        prefetch.put(JsonKeyNames.PREFETCH_MINUMUM_BUFFER, MusicConstants.PREFETCH_MINIMUM_BUFFER);
        prefetch.put(JsonKeyNames.NETWORK_BOUNDRY, MusicConstants.PREFETCH_NETWORK_BOUNDRY);
        BUFFER_UTILS_CONFIG.put(JsonKeyNames.PREFETCH, prefetch);

        NETWORK_TO_BITRATE_MAP.put(NetworkQuality.UNKNOWN.getValue(), MusicSongQualityType.getBitrateForSongQuality(MusicSongQualityType.l));
        NETWORK_TO_BITRATE_MAP.put(NetworkQuality.AWFUL.getValue(), MusicSongQualityType.getBitrateForSongQuality(MusicSongQualityType.l));
        NETWORK_TO_BITRATE_MAP.put(NetworkQuality.INDIAN_POOR.getValue(), MusicSongQualityType.getBitrateForSongQuality(MusicSongQualityType.l));
        NETWORK_TO_BITRATE_MAP.put(NetworkQuality.POOR.getValue(), MusicSongQualityType.getBitrateForSongQuality(MusicSongQualityType.l));
        NETWORK_TO_BITRATE_MAP.put(NetworkQuality.MODERATE.getValue(), MusicSongQualityType.getBitrateForSongQuality(MusicSongQualityType.m));
        NETWORK_TO_BITRATE_MAP.put(NetworkQuality.GOOD.getValue(), MusicSongQualityType.getBitrateForSongQuality(MusicSongQualityType.h));
        NETWORK_TO_BITRATE_MAP.put(NetworkQuality.EXCELLENT.getValue(), MusicSongQualityType.getBitrateForSongQuality(MusicSongQualityType.hd));
    }

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private final ConcurrentMap<String, String> versionCache = new ConcurrentHashMap<String, String>();

    @PostConstruct
    public void init() {

        if (!musicConfig.isEnableMusic())
            return;

        String platformBaseUrl = musicConfig.getPlatformBaseUrl();
        BASE_CMS_SEARCH_SUGGEST_API = platformBaseUrl + "v1/content/search/suggest";
        BASE_CMS_UNISEARCH_API = platformBaseUrl + "v1/content/search/unisearch";
        BASE_CMS_MLT_API = platformBaseUrl + "v1/content/discovery/mlt";

        if (musicConfig.isEnableGeoBlocking())
            geoDBService.init();
        try {
            userConfigService.refreshConfigInfo();
        } catch (ParseException e) {
            logger.error("Error in initialition of userConfigService");
            e.printStackTrace();
        }

    }

    MusicMonitoringManager getStatsMonitoring() {
        return statsMonitoring;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 60, initialDelay = 1000 * 20)
    public void refreshVersionConfigInfo() {
        logger.info("Refreshing Version Information through Cache");
        if (!musicConfig.isEnableMusic())
            return;
        if (writeLock.tryLock() && userPersistantRedisServiceManager != null) {
            try {
                versionCache.clear();

                String minAndroidVersion = userPersistantRedisServiceManager.get(MusicConstants.REDIS_MIN_ANDROID_VERSION_KEY);
                if (minAndroidVersion != null)
                    versionCache.put(MusicConstants.REDIS_MIN_ANDROID_VERSION_KEY, minAndroidVersion);

                String miniOSVersion = userPersistantRedisServiceManager.get(MusicConstants.REDIS_MIN_IOS_VERSION_KEY);
                if (miniOSVersion != null)
                    versionCache.put(MusicConstants.REDIS_MIN_IOS_VERSION_KEY, miniOSVersion);

                String minWindowsVersion = userPersistantRedisServiceManager.get(MusicConstants.REDIS_MIN_WINDOWS_VERSION_KEY);
                if (minWindowsVersion != null)
                    versionCache.put(MusicConstants.REDIS_MIN_WINDOWS_VERSION_KEY, minWindowsVersion);

                String targetAndroidVersion = userPersistantRedisServiceManager.get(MusicConstants.REDIS_TARGET_ANDROID_VERSION_KEY);
                if (targetAndroidVersion != null)
                    versionCache.put(MusicConstants.REDIS_TARGET_ANDROID_VERSION_KEY, targetAndroidVersion);

                String targetiOSVersion = userPersistantRedisServiceManager.get(MusicConstants.REDIS_TARGET_IOS_VERSION_KEY);
                if (targetiOSVersion != null)
                    versionCache.put(MusicConstants.REDIS_TARGET_IOS_VERSION_KEY, targetiOSVersion);

                String targetWindowsVersion = userPersistantRedisServiceManager.get(MusicConstants.REDIS_TARGET_WINDOWS_VERSION_KEY);
                if (targetWindowsVersion != null)
                    versionCache.put(MusicConstants.REDIS_TARGET_WINDOWS_VERSION_KEY, targetWindowsVersion);

            } finally {
                writeLock.unlock();
            }
            logger.info("Version Information updated through Cache");
        }

    }

    public String getVersionInfo(String key) {
        readLock.lock();
        try {
            return versionCache.get(key);
        } finally {
            readLock.unlock();
        }
    }

    public JSONObject getConfigData(HttpRequest request, Boolean popupEnabled, String archType, Boolean configOnly, JSONObject simInfo, Boolean isChangeMobileRequest,boolean fromAccountCall,boolean isEnrcypted) {
        // TODO - INSERTING HACK FOR CREATING MISSED USERS
        if(ChannelContext.getUser() == null && ChannelContext.getUid() != null && !ChannelContext.getUid().endsWith("0")){
            logger.info("Creating user from config call for uid = "+ChannelContext.getUid());
            User userFromConfigCall = accountRegistrationService.createUserFromConfigCall(request, simInfo,isEnrcypted);
            if(userFromConfigCall != null) {
                ChannelContext.setUser(userFromConfigCall);
                ChannelContext.setUid(userFromConfigCall.getUid());
            }
        }

        JSONObject jsonObject = new JSONObject();
        UserSubscription subscriptionMap = null;
        String msisdnStr = ChannelContext.getMsisdn();
        User user = ChannelContext.getUser();
        UserSubscription userDbSubscription = null;
        if (user != null) {
            msisdnStr = user.getMsisdn();
            userDbSubscription = user.getUserSubscription();
        }

        if (user != null && user.getUid() != null && user.getUid().endsWith("0")) {
            // means registered user , now check for Country
            accountRegistrationService.updateAndGetUserCountryIfNotExist(user, true);
        }

        WCFServiceType wcfServiceType = wcfUtils.getWCFServiceType(UserDeviceUtils.getPlatform());
        try {
            subscriptionMap = accountRegistrationService.reRegistration(msisdnStr, wcfServiceType);
        } catch (Exception ex) {
            logger.error("Exception occured in calling Wcf Provision Call and ex mssge is {} and ex is", ex.getMessage(), ex);
        }


        MusicSubscriptionStatus musicSubscriptionStatus = accountService.getMusicSubscriptionStatus(request, false, null, true, fromAccountCall);
        JsonElement musicSubscriptionStatusJson = gson.toJsonTree(musicSubscriptionStatus);
        if (!fromAccountCall) {
            /*removing it because we haven't computed it
            in the config call and Integer has a default value of 0
             */
            musicSubscriptionStatusJson.getAsJsonObject().remove(JsonKeyNames.TOP_OFFER_ID);
        }
        jsonObject.put(JsonKeyNames.SUBSCRIPTION, musicSubscriptionStatusJson);
        JSONObject userProfile = accountService.getUserProfile(false);
        //  userProfile.put("autoRenewal", statusDto.isAutoRenewalOn());
        try {
            userProfile.put("autoRenewal", musicSubscriptionStatus.isAutoRenewalOn());
        } catch (Exception e) {
            logger.error("Error setting autoRenewal", e, e.getMessage());
        }

        jsonObject.put(JsonKeyNames.PROFILE, userProfile);

        UserSubscription wcfSubscription = wcfUtils.getUserSubscriptionDetails(user);

        if (popupEnabled) {
            Boolean isActiveUser = wcfUtils.isUserSubsActive(musicSubscriptionStatus);
            JSONObject contestJson = accountService.getSubscriptionOfferJson(isActiveUser, configOnly, isChangeMobileRequest);
            if (!contestJson.isEmpty())
                jsonObject.put(JsonKeyNames.OFFER, contestJson);
        }
        setLyricsConfig(musicSubscriptionStatus, wcfSubscription, jsonObject);
        jsonObject.put(JsonKeyNames.PRICE_BANNER, MusicConstants.PRICE_BANNER_TEXT);

        if (user != null && StringUtils.isBlank(user.getMsisdn())) {
            jsonObject.put(JsonKeyNames.NOTIFICATION_PAYLOAD, MusicNotificationConstants.REGISTER_DOWNLOAD_NOTIFICATION_NON_AIRTEL);
            if (OperatorUtils.isAirtelDevice()) {
                jsonObject.put(JsonKeyNames.NOTIFICATION_PAYLOAD, MusicNotificationConstants.REGISTER_DOWNLOAD_NOTIFICATION_AIRTEL);
            }
        }

        // Notification for tamil customization
        JSONObject tamilUserNotification = accountService.getTamilUserNotification();
        JSONObject teluguUserNotification = accountService.getTeluguUserNotification();
        if (tamilUserNotification != null) {
            jsonObject.put(JsonKeyNames.OFFER, tamilUserNotification);
        } else if (teluguUserNotification != null) {
            jsonObject.put(JsonKeyNames.OFFER, teluguUserNotification);
        }

        // process international roaming flow
        processIntlRoamingFlow(configOnly, simInfo, jsonObject);

        boolean isAndroidDevice = MusicDeviceUtils.isAndroidDevice();
        // Version Config
        JSONObject versionJson = getVersionConfig();
        if (versionJson != null && !versionJson.isEmpty()) {

            // international roaming version override
            setVersionUpgradeForIntlRoaming(isAndroidDevice, versionJson);

            jsonObject.put(JsonKeyNames.VERSION_CONFIG, versionJson);
            if (MusicDeviceUtils.isIOSDevice()) {
                jsonObject.put(JsonKeyNames.FORCE_UPGRADE, true);
            }
        }

        // show restore button if no key exists in the db
        if (!wcfUtils.isUserSubsActive(musicSubscriptionStatus) && MusicDeviceUtils.isIOSDevice()) {
            jsonObject.put("enableRestoreButton", true);
        }

        if (musicConfig.isDisableProactiveFeedback()) {
            jsonObject.put(JsonKeyNames.DISABLE_PROACTIVE_FEEDBACK, true);
        }

        if (musicConfig.isShowADHMHamburgerMenu()) {
            jsonObject.put(JsonKeyNames.ENABLE_ADHM_HAMBURGER_MENU, false);
        }

        if (musicConfig.isShowOnDeviceHamburgerMenu()) {
            jsonObject.put(JsonKeyNames.ONDEVICE_HAM_MENU, true);
        }

        if (MusicBuildUtils.isAdSupported()) {
            UserSubscription latestSubsDetails = (subscriptionMap != null ? subscriptionMap : userDbSubscription);
            if (!wcfApisUtils.getFeature(FeatureType.SHOW_ADS, latestSubsDetails).isSubscribed()) {
                jsonObject.put("ads", adService.getConfig(wcfServiceType.getServiceName(), musicSubscriptionStatus, msisdnStr));
            } else {
                jsonObject.put("ads", new JSONObject());
            }
        }

        // TODO - MOVE ALL TO STATIC.

        if (MusicBuildUtils.isSubscriptionIntentSupported()) {
            jsonObject.put(JsonKeyNames.SUBSCRIPTION_INTENT, subscriptionIntentService.getSubscriptionIntent());
            jsonObject.put(JsonKeyNames.REMOVE_ADS_LIMITS, SubscriptionIntentService.getRemoveAdsIntentConfig());
            jsonObject.put(JsonKeyNames.ADS_INTENT_CHECK_FOR_NON_AIRTEL, Boolean.FALSE);
        }

        if (MusicBuildUtils.isOnDeviceSupported()) {
            jsonObject.put(ONDEVICE, getOnDeviceConfigs(archType));
        }
        if ((boolean) userConfigService.getHooksConfigKey(JsonKeyNames.HOOKS_ENABLED)) {

            jsonObject.put(JsonKeyNames.HOOKS_CONFIG, userConfigService.getHooksUserConfig());
        }

        jsonObject.put(JsonKeyNames.OTHER_INSTALLED_APPS, getThirdPartAppInstallConfig(isAndroidDevice));

        jsonObject.put(JsonKeyNames.SHOW_ADHM_POPUP, false);
        jsonObject.put(JsonKeyNames.RETRY_AUTO_REGISTRATION, MusicConstants.RETRY_AUTO_REGISTRATION_COUNT);

        // Dev stat config
        jsonObject.put(JsonKeyNames.ENABLE_SONG_INIT_STAT, true);
        jsonObject.put(JsonKeyNames.SONG_INIT_STAT_FREQUENCY, 10);

        jsonObject.put(JsonKeyNames.COOKIE_EXPIRY_TIME, MusicConstants.COOKIE_EXPIRY_TIME);
        if ((ChannelContext.getOS()!=null && ChannelContext.getOS().toLowerCase().contains("ios")) && ChannelContext.getBuildnumber() != null && ChannelContext.getBuildnumber() == MusicBuildConstants.PRE_CACHING_CRASH_BUILD_IOS ) {
            jsonObject.put(JsonKeyNames.PRE_CACHING_STATUS, MusicConstants.PRE_CACHING_STATUS_FOR_100);
        }
        else if (ChannelContext.getOS()!=null && ChannelContext.getOS().toLowerCase().contains("android") && !(UserDeviceUtils.getPlatform().equals(MusicPlatformType.SAMSUNG_SDK)) ) {
            jsonObject.put(JsonKeyNames.PRE_CACHING_STATUS, MusicConstants.PRE_CACHING_STATUS_FOR_ANDROID);
        }
        else {
            jsonObject.put(JsonKeyNames.PRE_CACHING_STATUS, MusicConstants.PRE_CACHING_STATUS);
        }

        jsonObject.put(JsonKeyNames.BUFFER_UTILS, BUFFER_UTILS_CONFIG);
        jsonObject.put(JsonKeyNames.OTP_SMS_CONFIG, OtpService.getPinIndex());

        // GEO Restriction Config
        JSONObject geo = new JSONObject();
        Boolean geoRestrictionPassed = getGeoRestrictionPassed(simInfo);
        geo.put(JsonKeyNames.IS_GEO_RESTRICTION_PASSED, geoRestrictionPassed);
        geo.put(JsonKeyNames.DESCRIPTION, getGeoBlacklistedResponse());
        if (MusicBuildUtils.isInternationalSupportedBuild()) {
            String isoCodeVal = MusicUtils.getFirstIPCountry(geoDBService, request);
            geo.put("isoCode", isoCodeVal);
            logger.info("Setting up context with COA in MusicService {}", isoCodeVal);
            ChannelContext.setUserCoaContext(isoCodeVal); // coa set in context
        }
        jsonObject.put(JsonKeyNames.GEO, geo);

        // FUP
        jsonObject.put(JsonKeyNames.FUP, getFUPConfig());

        // Network Bitrate ordering
        jsonObject.put(JsonKeyNames.NETWORK_QUALITY_BITRATE_ORDER, NETWORK_TO_BITRATE_MAP);

        // FLAKY NETWORK
        jsonObject.put(JsonKeyNames.SONG_RETRY_INTERVAL, MusicConstants.SONG_RETRY_INTERVAL);
        jsonObject.put(JsonKeyNames.FLAKY_NETWORK_SONGS_COUNT, MusicConstants.FLAKY_NETWORK_SONGS_COUNT);

        jsonObject.put(JsonKeyNames.DEFAULT_API_CALL_INTERVAL, MusicConstants.API_CALL_TIME_INTERVAL);
        jsonObject.put(JsonKeyNames.USER_STATE_SYNC_TIME, MusicConstants.USER_STATE_SYNC_TIME);

        // HACK for APP to discard SOS events
        jsonObject.put("discardSOS", true);

        // buffered config
        JSONObject bufferedConfig = new JSONObject();
        bufferedConfig.put(JsonKeyNames.FEATURE_AVAILABLE, MusicConstants.TRUE);
        bufferedConfig.put(JsonKeyNames.SONG_PLAYED_COUNT, MusicConstants.songPlayedCount);
        bufferedConfig.put(JsonKeyNames.SONG_QUALITY_THRESHOLD, MusicConstants.songQualityThreshold);
        jsonObject.put(JsonKeyNames.BUFFER_CONFIG, bufferedConfig);

        // download on wifi
        // TODO - MAKE downloadOnWifiConfig static
        JSONObject downloadOnWifiConfig = new JSONObject();
        downloadOnWifiConfig.put(JsonKeyNames.FEATURE_AVAILABLE, false);
        downloadOnWifiConfig.put(JsonKeyNames.NOTIFICATION_INTERVAL, 7);
        downloadOnWifiConfig.put(JsonKeyNames.MIN_UNFINISHED_SONG_THRESHOLD, 3);
        downloadOnWifiConfig.put(JsonKeyNames.MEMORY_THRESHOLD, 2048);
        downloadOnWifiConfig.put(JsonKeyNames.MAX_SONG_COUNT, 50);
        jsonObject.put(JsonKeyNames.DOWNLOAD_ON_WIFI_CONFIG, downloadOnWifiConfig);

        //offline Notification Config
        // TODO - MAKE Notification static
        JSONObject offlineNotificationConfig = new JSONObject();
        offlineNotificationConfig.put(JsonKeyNames.FEATURE_AVAILABLE, MusicConstants.TRUE);
        offlineNotificationConfig.put(JsonKeyNames.NOTIFICATION_INTERVAL, 3);
        offlineNotificationConfig.put(JsonKeyNames.NOTIFICATION_SYNC_INTERVAL, 2);
        jsonObject.put(JsonKeyNames.OFFLINE_NOTIFICATION_CONFIG, offlineNotificationConfig);

        jsonObject.put("offlineQueueSortingConfig", userConfigService.getOfflineQueueSortingConfig());
        //For local mp3 rail position
        jsonObject.put(JsonKeyNames.LOCAL_MP3_RAIL_POSITION, RailPositions.getLocalMp3RailPosition());
        jsonObject.put(JsonKeyNames.DEFAULT_MIGRATION_LANGUAGE, MusicConstants.CONTENT_LANGS_MIGRATION);
        //MyMusic Top Playlists
//       JSONObject myMusicTopPlaylist = getTopPlaylistNavigationMeta();
//        if (myMusicTopPlaylist != null) {
//            jsonObject.put("myMusicNavigation", myMusicTopPlaylist);
//        }

        //ioS pre-caching config
        JSONObject preCache = new JSONObject();
        preCache.put(JsonKeyNames.DAYS_OF_WEEK, MusicConstants.days_of_week);
        preCache.put(JsonKeyNames.HOUR_OF_DAY, MusicConstants.hour_of_day);
        preCache.put(QUALTIY_SYMBOL, MusicSongQualityType.h.name());
        preCache.put(SEGMENT_COUNT, 3);
        jsonObject.put(PRE_CACHING_CONFIG, preCache);

        //search config object
        JSONObject searchConfig = new JSONObject();
        searchConfig.put(MAX_RECENT_SEARCH, 10);
        searchConfig.put(SPACE_IN_SEARCH_ENABLED, false);
        searchConfig.put(VOICE_IN_SEARCH_ENABLED, musicConfig.isSearchVoiceEnable());
        jsonObject.put("search", searchConfig);

        jsonObject.put(IPL, musicConfig.isEnableIpl());
        jsonObject.put("folderMigrationStatus", true);
        jsonObject.put("isAirtelUser", OperatorUtils.isAirtelCurrentDevice());
        jsonObject.put("isHtAirtelUser",OperatorUtils.isAirtelUser());
        jsonObject.put("cpmapping", MusicCPMapping.getCPMappings());
        jsonObject.put("happyHour", true);

        if (ChannelContext.getBuildnumber() != null && MusicBuildUtils.isGreaterBuildNumber(ChannelContext.getBuildnumber(), 92)) {
            jsonObject.put(JsonKeyNames.ENABLE_WYNK_DIRECT, true);
        } else
            jsonObject.put(JsonKeyNames.ENABLE_WYNK_DIRECT, false);


        // TODO - Move to constants
        jsonObject.put("my_radio_image", "http://img.wynk.in/unsafe/320x180/top/http://s3-ap-south-1.amazonaws.com/wynk-music-cms/Card_Mask%20Copy.jpg");
        jsonObject.put("ap_pkg_skp_tld",3);     // autoplay_package_skip_threshold
        jsonObject.put("ap_gre_aff_tld",60);    // autoplay_genre_affinity_threshold


        JSONArray user_content_keys = new JSONArray();
        user_content_keys.addAll(Arrays.asList("dailyAvgSongPlayCount","playCount","genres"));
        jsonObject.put("user_content_keys", user_content_keys);

        setWynkDirectConfig(jsonObject);
        setSubscriptionConfig(jsonObject);
        setSubscriptionSettings(jsonObject);

        // set auto guggest config
        setAutoSuggestConfig(jsonObject);

        //Set Home Config
        setHomeConfig(jsonObject);
        setTargetingKeys(jsonObject);

        //Set App Shortcuts
        if(MusicBuildUtils.isAppShortCutSupported())
            setAppShortcuts(jsonObject);

        //Set OnBoarding keys
        if (fromAccountCall){
            setInstallOnBoardingKeys(jsonObject);
        }
        else{
            setUpgradeOnBoardingKeys(jsonObject);
        }

        setConfigMetaKeys(jsonObject);

        JSONObject appsFlyerConfig = new JSONObject();
        appsFlyerConfig.put(JsonKeyNames.TIME_SPAN, MusicConstants.APPS_FLYER_TIME_SPAN);
        appsFlyerConfig.put(JsonKeyNames.SONG_SPAN, MusicConstants.APPS_FLYER_SONGS_SPAN);
        jsonObject.put(JsonKeyNames.APPSFLYER_EVENT, appsFlyerConfig);

        JSONObject appsFlyerAirtelConfig = new JSONObject();
        appsFlyerAirtelConfig.put(JsonKeyNames.TIME_SPAN, MusicConstants.APPS_FLYER_AIRTEL_TIME_SPAN);
        appsFlyerAirtelConfig.put(JsonKeyNames.SONG_SPAN, MusicConstants.APPS_FLYER_AIRTEL_SONGS_SPAN);
        jsonObject.put(JsonKeyNames.APPSFLYER_AIRTEL_EVENT, appsFlyerAirtelConfig);
        if (MusicBuildUtils.isSriLankaSupported()) {
            jsonObject.put(JsonKeyNames.SUPPORTED_COUNTRIES,
                    MusicUtils.getAvailableCountryList(MusicUtils.getCountryCodeFromRequest(geoDBService, request)));
        }
        //search config object
        JSONObject buffer_policy = new JSONObject();
        buffer_policy.put(ABOVE_NETWORK_BOUNDRY_MAX_BUFFER, 60000);
        buffer_policy.put(ABOVE_NETWORK_BOUNDRY_MIN_BUFFER, 30000);
        buffer_policy.put(NETWORK_BOUNDARY, 1);
        buffer_policy.put(BELOW_NETWORK_BOUNDRY_MAX_BUFFER, 600000);
        buffer_policy.put(BELOW_NETWORK_BOUNDRY_MIN_BUFFER, 60000);
        jsonObject.put(BUFFER_POLICY, buffer_policy);

        jsonObject.put(RELATED_WYNK_TOP,"srch_bsb_1399890012242");

        //Auto Follow Screen
        jsonObject.put(JsonKeyNames.OPEN_AUTO_FOLLOW,true);

        setDefferedDeepLink(jsonObject);
        setAVWebHookConfig(jsonObject);
        setHtFeatureEnable(jsonObject,user);
        //Personalised Radio
        jsonObject.put(PERSONALISATION_META_COUNT,50);

        //twitter consumer credentials (encrypt and send)
        String encryptedKey = null;
        String encryptedSecret = null;
        String encryptionKey = null;
        if (MusicDeviceUtils.isAndroidDevice() || MusicDeviceUtils.isIOSDevice()) {
            try {
                encryptionKey = EncryptUtils.getDeviceKey();
                encryptedKey = EncryptUtils.encrypt_256(musicConfig.getTwitterKey(), encryptionKey);
                encryptedSecret = EncryptUtils.encrypt_256(musicConfig.getTwitterSecret(), encryptionKey);
            } catch (Exception e) {
                logger.error("Error encrypting twitter key and secret through encryption key : {}, Exception : {}", encryptionKey, e);
            }
            if (Objects.nonNull(encryptedKey) && Objects.nonNull(encryptedSecret)) {
                JSONObject twitterKeySecret = new JSONObject();
                twitterKeySecret.put(TWIITER_KEY, encryptedKey);
                twitterKeySecret.put(TWIITER_SECRET, encryptedSecret);
                jsonObject.put(TWIITER_CREDS, twitterKeySecret);
            }
            // updating user build number , app version
            UserDevice udFromDb = MusicDeviceUtils.getUserDeviceFromDid(user, MusicDeviceUtils.getDeviceId());
            if (udFromDb != null && ((Integer) udFromDb.getBuildNumber() == null || udFromDb.getBuildNumber() < MusicDeviceUtils.getBuildNumber())) {
                logger.info("updating bn for uid:{} from:{} to:{}", user.getUid(), udFromDb.getBuildNumber(), MusicDeviceUtils.getBuildNumber());
                udFromDb.setBuildNumber(MusicDeviceUtils.getBuildNumber());
                udFromDb.setAppVersion(MusicDeviceUtils.getAppVersion());
                udFromDb.setLastActivityTime(System.currentTimeMillis());
                accountService.updateDeviceInUserDevicesAndDb(udFromDb);
            }
        }
        if (MusicBuildUtils.isInternationalSupportedBuild() && ChannelContext.getUser() != null && ChannelContext.getUser().getCountryId() != null) {
            String countryId = ChannelContext.getUser().getCountryId();
            if (StringUtils.isBlank(countryId)) {
                String updatedCountry = accountRegistrationService.updateAndGetUserCountryIfNotExist(ChannelContext.getUser(), true);
                logger.info("Successfully updated in db");
                countryId = updatedCountry;
            }
            jsonObject.put("countryId", countryId);
            ChannelContext.setUserCooContext(countryId); // coo set in context
        }

        if (MusicBuildUtils.isInternationalSupportedBuild()) {
            Optional<CountryDetails> countryDetails = Optional.empty();
            logger.info("Checking with this coo {}", ChannelContext.getUserCooContext());
            if (StringUtils.isNotBlank(ChannelContext.getUserCooContext())) {
                countryDetails = getCountryDetailsBasedOnCountry(MusicUtils.preparedCountryDetailsList(), ChannelContext.getUserCooContext());
                if (countryDetails.isPresent() && countryDetails.get() != null) {
                    jsonObject.put(HELLO_TUNE_ENABLED, countryDetails.get().getHelloTuneEnabled());
                    jsonObject.put(AD_ENABLE, countryDetails.get().getAdEnabled());
                    jsonObject.put(PODCAST_ENABLED, countryDetails.get().getPodcastEnabled());
                    logger.info("Added keys via coo : {}", countryDetails.get().getIsoCode());
                }
            } else if (!countryDetails.isPresent()) {
                logger.info("Checking with this coa {}", ChannelContext.getUserCoaContext());
                countryDetails = getCountryDetailsBasedOnCountry(MusicUtils.preparedCountryDetailsList(), ChannelContext.getUserCoaContext());
                if (countryDetails.isPresent() && countryDetails.get() != null) {
                    jsonObject.put(HELLO_TUNE_ENABLED, countryDetails.get().getHelloTuneEnabled());
                    jsonObject.put(AD_ENABLE, countryDetails.get().getAdEnabled());
                    jsonObject.put(PODCAST_ENABLED, countryDetails.get().getPodcastEnabled());
                    logger.info("Added keys via coa : {}", countryDetails.get().getIsoCode());
                } else {
                    jsonObject.put(HELLO_TUNE_ENABLED, Boolean.FALSE.toString());
                    jsonObject.put(AD_ENABLE, Boolean.FALSE.toString());
                    jsonObject.put(PODCAST_ENABLED, Boolean.FALSE.toString());
                    logger.info("Neither coo : {} or coa : {}", ChannelContext.getUserCooContext(), ChannelContext.getUserCoaContext());
                }
            }
        }

        accountService.updateStreamCount(user != null ? user.getUid() : null);

        if (accountService.fupResetRequired(user,System.currentTimeMillis() - (30 * DAY))) {
            accountService.resetFUPLimitsForUser(user != null ? user.getUid() : null);
        }
        logger.info("CLEARING CACHE for : {}",user != null ? user.getUid() : null);
        jsonObject.put("isValidUser", !accountService.isUserDeleted(user.getUid()));
        accountService.removeUserFromCache(user != null ? user.getUid() : null);
        return jsonObject;

    }

    private void setLyricsConfig(MusicSubscriptionStatus subscriptionStatus, UserSubscription wcfSubscription, JSONObject jsonObject){
        JSONObject obj = new JSONObject();

//        String wcfServiceType = wcfUtils.getWCFServiceType(UserDeviceUtils.getPlatform()).toString();
//        boolean isPremiumUser = wcfUtils.isPremiumUserAccount(wcfServiceType, subscriptionStatus.getStatus(), subscriptionStatus.getProductId());
//        boolean isFmfUser = wcfUtils.isFmfUser(wcfServiceType, subscriptionStatus, subscriptionStatus.getProductId());

//        boolean trialShowLyrics = wcfUtils.isLyricsPackActive(subscriptionStatus);

//        List<Integer> eligibleProductList = null;
//        if(wcfSubscription != null){
//            eligibleProductList = wcfSubscription.getEligibleOfferProductId();
//        }
//        boolean showTrialAgainOption = eligibleProductList != null
//                && (eligibleProductList.contains(musicConfig.getTrialAgainProductId()) || eligibleProductList.contains(musicConfig.getBundledTrialAgainProductId()));

        boolean showLyrics = true; //(isPremiumUser || isFmfUser || trialShowLyrics);
//        String trialOption = showLyrics ? "NONE" : showTrialAgainOption ? "TRY_AGAIN" : "TRY_NOW";

        obj.put("show_lyrics", showLyrics);
        obj.put("show_ads", true);
//        obj.put("state", trialOption);
//        if(trialShowLyrics){
//            obj.put( "days_remaining", subscriptionStatus.getDaysToExpire()+" Days remaining");
//        }
//        if(!showLyrics){
//            JSONObject playerScreenMeta = new JSONObject();
//            playerScreenMeta.put("title", showTrialAgainOption ? "Try Again": "Try Now");
//            playerScreenMeta.put("subtitle", "Sing Along Available");
//            obj.put("player_screen_meta", playerScreenMeta);
//        }
        String uToken = MusicUtils.encryptAndEncodeParam(musicConfig.getEncryptionKey(), ChannelContext.getUser() != null ? ChannelContext.getUser().getUid() : "");
        obj.put("premium_url", musicConfig.getWcfWebViewBaseUrl() + "redesign-wcf/plan?u=" + uToken);
        obj.put("req_lyrics", "Request Lyrics");

        JSONArray opts = new JSONArray();
        opts.add("Lyrics are Inaccurate");
        opts.add("Lyrics are Inappropriate or Offensive");
        opts.add("Lyrics are Sexually Explicit");
        opts.add("Other Reasons");
        obj.put("report_lyrics_opt", opts);
        obj.put("enable_lyrics", musicConfig.getEnableLyrics());
        jsonObject.put("lyricsConfig", obj);

    }

    private void setAVWebHookConfig(JSONObject jsonObject){
        JSONObject obj = new JSONObject();
        obj.put("setting_refer_enabled", false);
        obj.put("search_refer_enabled", false);
        obj.put( "nav_refer_enabled", false);
        jsonObject.put("referHooksConfig", obj);
    }

    private void setHtFeatureEnable(JSONObject jsonObject,User user){
        JSONObject obj = new JSONObject();
        if(user != null && user.getCircle() != null && Circle.getCircleById(user.getCircle()) == Circle.SRILANKA){
            obj.put("ht_page_enabled", false);
            obj.put("ht_search_enabled", false);
            obj.put( "ht_feature_enabled", false);
        }
        else {
            obj.put("ht_page_enabled", true);
            obj.put("ht_search_enabled", true);
            obj.put("ht_feature_enabled", true);
        }
        jsonObject.put("htHooksConfig", obj);
    }

    private void  setConfigMetaKeys(JSONObject jsonObject){
        JSONObject jsonObject1 = new JSONObject();
        JSONObject obj1 = new JSONObject();
        JSONObject obj2 = new JSONObject();
        JSONObject obj3 = new JSONObject();
        JSONObject obj4 = new JSONObject();
        obj1.put("title","Radio");
        obj1.put("iconChange",false);
        obj2.put("title","Radio");
        obj2.put("iconChange",true);
        obj3.put("title","Stations");
        obj3.put("iconChange",true);
        obj4.put("title","Non-Stop");
        obj4.put("iconChange",true);
        jsonObject1.put("0",obj1);
        jsonObject1.put("1",obj2);
        jsonObject1.put("2",obj3);
        jsonObject1.put("3",obj4);
        jsonObject.put("radio_tab_button_meta",jsonObject1);

    }
    private int getABExperimentCode(int[] cases, int[] weights){
        double random = Math.random() * (100.0);
        double[] classes = new double[weights.length];
        double weightSum = IntStream.of(weights).sum();

        for (int i = 0; i < weights.length; i++){
            double prev = 0;
            if (i != 0){
                prev = classes[i-1];
            }
            classes[i] = prev + weights[i]*100/weightSum;
            if (random < classes[i]){
                return cases[i];
            }
        }
        // Should never come to this
        return cases[weights.length-1];
    }



    private void  setInstallOnBoardingKeys(JSONObject jsonObject){
        User user = ChannelContext.getUser();
        boolean isRegistered = !user.getUid().endsWith("2");
//        typeCode = getABExperimentCode(new int[]{ON_BOARDING_NONE, ON_BOARDING_LANGUAGE_NOT_SELECTED},new int[]{1,1});
        int typeCode = ON_BOARDING_LANGUAGE_NOT_SELECTED;
//        if (isRegistered){
//            typeCode = getABExperimentCode(new int[]{ON_BOARDING_NONE,ON_BOARDING_BENEFITS,ON_BOARDING_LANGUAGE_SELECTED, ON_BOARDING_LANGUAGE_NOT_SELECTED},new int[]{1,1,1,1});
//        }
//        else{
//            typeCode = getABExperimentCode(new int[]{ON_BOARDING_NONE,ON_BOARDING_LANGUAGE_SELECTED, ON_BOARDING_LANGUAGE_NOT_SELECTED},new int[]{1,1,1});
//        }
        JSONObject jsonObject1 = new JSONObject();
        JSONArray meta = new JSONArray();
        JSONObject benefit1 = new JSONObject();
        JSONObject benefit2 = new JSONObject();
        JSONObject benefit3 = new JSONObject();

        jsonObject1.put("type",String.valueOf(typeCode));
        jsonObject1.put("disclaimer","Benefit available for 30 days. After that, you can play limited number of songs. For unlimited experience, get Wynk Premium");
        switch (typeCode){
            case ON_BOARDING_BENEFITS:
                if (accountService.isAirtelUser(user)){
                    jsonObject1.put("subtitle","Exclusive Airtel Benefits");
                }
                else {
                    jsonObject1.put("subtitle","Exclusive Wynk Benefits");
                }
                jsonObject1.put("title","You have unlocked");
                benefit1.put("title","Unlimited Free Songs");
                benefit1.put("subtitle","3 Million Songs in 15 Languages");
                benefit1.put("color","green");
                benefit1.put("icon","http://s3-ap-south-1.amazonaws.com/wynk-music-cms/images/onboarding/noun_music_684877.png");
                benefit2.put("title","Unlimited Free Downloads");
                benefit2.put("subtitle","Music anywhere, anytime");
                benefit2.put("color","red");
                benefit2.put("icon","http://s3-ap-south-1.amazonaws.com/wynk-music-cms/images/onboarding/noun_download_213406.png");
                benefit3.put("title","Play without Internet");
                benefit3.put("subtitle","Offline access to songs and playlists");
                benefit3.put("color","blue");
                benefit3.put("icon","http://s3-ap-south-1.amazonaws.com/wynk-music-cms/images/onboarding/offline_play.png");
                meta.add(benefit1);
                meta.add(benefit2);
                meta.add(benefit3);
                jsonObject1.put("meta", meta);
                break;

            case ON_BOARDING_LANGUAGE_SELECTED:
                jsonObject1.put("title","What would you like to listen to?");
                jsonObject1.put("subtitle","Select your preferred languages");
                jsonObject1.put("meta", meta);
                break;

            case ON_BOARDING_LANGUAGE_NOT_SELECTED:
                jsonObject1.put("title","What would you like to listen to?");
                jsonObject1.put("subtitle","Select your preferred languages");
                jsonObject1.put("meta", meta);
                break;
            case ON_BOARDING_NONE:
                jsonObject1.put("title","");
                jsonObject1.put("subtitle","");
                jsonObject1.put("meta", meta);
                break;
        }
        jsonObject.put("on_boarding", jsonObject1);
    }

    private void  setUpgradeOnBoardingKeys(JSONObject jsonObject){
        User user = ChannelContext.getUser();
        if (user == null || (user.getSelectedLanguages()!=null && user.getSelectedLanguages().size()>0)){
            return ;
        }

        int typeCode = getABExperimentCode(new int[]{ON_BOARDING_NONE,ON_BOARDING_LANGUAGE_SELECTED},new int[]{1,1});
        JSONObject jsonObject1 = new JSONObject();
        JSONArray meta = new JSONArray();
        jsonObject1.put("type",String.valueOf(typeCode));
        jsonObject1.put("disclaimer","Benefit available for 30 days. After that, you can play limited number of songs. For unlimited experience, get Wynk Premium");
        switch (typeCode){
            case ON_BOARDING_LANGUAGE_SELECTED:
                jsonObject1.put("title","What would you like to listen to?");
                jsonObject1.put("subtitle","Select your preferred languages");
                jsonObject1.put("meta", meta);
                break;

            case ON_BOARDING_NONE:
                jsonObject1.put("title","");
                jsonObject1.put("subtitle","");
                jsonObject1.put("meta", meta);
                break;
        }
        jsonObject.put("on_boarding", jsonObject1);
    }

    private void setAppShortcuts(JSONObject jsonObject){
        JSONArray keys = new JSONArray();

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("shortcutId","my_music");
        jsonObject1.put("deeplinkUrl","android-app://com.bsbportal.music/applink/www.wynk.in/music/my-music");
        jsonObject1.put("shortLabel","My music");
        jsonObject1.put("longLabel","My Music");
        jsonObject1.put("iconId","1");

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("shortcutId","search");
        jsonObject2.put("deeplinkUrl","android-app://com.bsbportal.music/applink/www.wynk.in/music/search");
        jsonObject2.put("shortLabel","Search");
        jsonObject2.put("longLabel","Search");
        jsonObject2.put("iconId","2");

        keys.add(jsonObject1);
        keys.add(jsonObject2);

        jsonObject.put("app_shortcuts",keys);
        jsonObject.put("app_shortcut_disabled_msg","Shortcut isn't available");
    }

    private void setTargetingKeys(JSONObject jsonObject) {
        JSONArray keys = new JSONArray();
        //keys.add("genre_mapping");
        keys.add("playCount");
        keys.add("dailyAvgSongPlayCount");
        jsonObject.put("targeting_keys", keys);
    }

    private void setDefferedDeepLink(JSONObject jsonObject) {
      jsonObject.put("deferred_deeplink_timeout", 5);
      jsonObject.put("deferred_deeplink_lock_time", 15000);
      jsonObject.put("appsflyer_url", "https://onelink.appsflyer.com/shortlink/v1/3330602766");
      jsonObject.put("appsflyer_enabled", false);
    }

	private void setSubscriptionSettings(JSONObject jsonObject) {
		JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("subscribedCardPosition", 3);
        jsonObject1.put("nonSubscribedCardPosition", 1);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("id", "srch_bsb_1399890012242");
        jsonObject2.put("type", "PACKAGE");

        jsonObject.put("emptyStateCta", jsonObject2);
        jsonObject.put("hellotuneEnabled",false);
        jsonObject.put("subscription_settings", jsonObject1);
	}

    private static void setHomeConfig(JSONObject jsonObject) {
        JSONObject homeConfig = new JSONObject();
        homeConfig.put("artist_rail_threshold", 3);
        homeConfig.put("playlist_rail_threshold", 3);
        homeConfig.put("song_rail_threshold", 5);
        JSONArray qsArray = new JSONArray();
        String[] qsArrayValues = {"DOWNLOAD_QUALITY", "SONG_LANGUAGES", "SLEEP_TIMER"};
        qsArray.addAll(Arrays.asList(qsArrayValues));
        homeConfig.put("quick_settings", qsArray);
        jsonObject.put(JsonKeyNames.HOME_CONFIG, homeConfig);
    }

    private void setSubscriptionConfig(JSONObject jsonObject) {
        if (ChannelContext.getBuildnumber() != null
                && MusicBuildUtils.isNewSubscriptionBuildSupported()) {
            JSONArray resourcesList = new JSONArray();
            resourcesList.add(musicConfig.getWcfWebViewBaseUrl() + "assets/airtel-logo.png");
            resourcesList.add(musicConfig.getWcfWebViewBaseUrl() + "assets/icon-insuffecient-fund.png");
            resourcesList.add(musicConfig.getWcfWebViewBaseUrl() + "assets/tick-popup.png");
            resourcesList.add(musicConfig.getWcfWebViewBaseUrl() + "assets/wynk.jpg");
            resourcesList.add("https://d1svfjavydl9ml.cloudfront.net/fe/newPaymentPage/newImages/sprite.png");
            resourcesList.add("https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css");
            JSONObject subscriptionConfig = new JSONObject();
            subscriptionConfig.put("subscription_resources_uri", resourcesList);

            Map<String, String> deviceInfo = MusicDeviceUtils.parseMusicHeaderDID();
            String settingsPageWcfWebViewUrl = null;
            //uid not ending with 0, hence non logged in user
            if (!ChannelContext.getUid().endsWith("0")) {
                settingsPageWcfWebViewUrl = getWcfWebViewUrlForNonLoggedInUser(deviceInfo);
            } else {
                settingsPageWcfWebViewUrl = getWcfStaticWebViewUrl(null, null, WCFView.SMALL, deviceInfo, Intent.MY_ACCOUNT);
            }
            String otherWcfWebViewsUrl = getWcfStaticWebViewUrl(WCFPackGroup.WYNK_MUSIC, null, WCFView.LARGE, deviceInfo, null);
            String adsRemovalWcfWebViewsUrl = getWcfStaticWebViewUrl(WCFPackGroup.WYNK_MUSIC, null, WCFView.POPUP, deviceInfo, null);
            if (MusicBuildUtils.isRedesignSubscriptionBuildSupported()) {
                subscriptionConfig.put("subscription_address", otherWcfWebViewsUrl);
                subscriptionConfig.put("subscription_address_ads", adsRemovalWcfWebViewsUrl);
                subscriptionConfig.put("subscription_settings_address", settingsPageWcfWebViewUrl);
            } else {
                subscriptionConfig.put("subscription_address", otherWcfWebViewsUrl);
                subscriptionConfig.put("subscription_address_ads", adsRemovalWcfWebViewsUrl);
            }
            subscriptionConfig.put("parse_address", musicConfig.getWcfWebViewBaseUrl() + "index.json");
            subscriptionConfig.put("base_url", musicConfig.getWcfWebViewBaseUrl());
            jsonObject.put("subscription_config", subscriptionConfig);
        }
    }

    private String getWcfStaticWebViewUrl(WCFPackGroup packGroup, Theme theme, WCFView view, Map<String, String> deviceInfo, Intent intent) {
        String uri = WCF_STATIC_WEBVIEW_ENDPOINT;
        try {
            StringBuilder queryParams = new StringBuilder();
            queryParams.append("?" + APPID + "=" + APPID_MOBILITY);
            appendQueryParam(queryParams, WCFApisConstants.APP_VERSION, deviceInfo.get(APP_VERSION_NO));
            appendQueryParam(queryParams, WCFApisConstants.BUILD_NO, deviceInfo.get(APP_BUILD_NO));
            appendQueryParam(queryParams, WCFApisConstants.DEVICE_ID, deviceInfo.get(MusicConstants.DEVICE_ID));
            appendQueryParam(queryParams, MSISDN, ChannelContext.getUser().getMsisdn());
            appendQueryParam(queryParams, OS, deviceInfo.get(OS));
            appendQueryParam(queryParams, SERVICE, MUSIC);
            appendQueryParam(queryParams, UID, ChannelContext.getUid());
            if (intent == null)
                appendQueryParam(queryParams, INGRESS_INTENT, "null");
            else
                appendQueryParam(queryParams, INGRESS_INTENT, intent);
            if (packGroup != null)
                appendQueryParam(queryParams, PACK_GROUP, packGroup);
            if (theme == null)
                theme = Theme.LIGHT;
            appendQueryParam(queryParams, THEME, theme);
            if (view == null)
                view = WCFView.LARGE;
            appendQueryParam(queryParams, WCFApisConstants.VIEW, view);
            long tms = System.currentTimeMillis();
            appendQueryParam(queryParams, WCFApisConstants.TIMESTAMP, tms);
            String digestString = uri.split("#")[1] + ChannelContext.getUid() + tms;
            String hash = EncryptUtils.calculateRFC2104HMAC(digestString, musicConfig.getStaticWebViewSecretKey());
            appendQueryParam(queryParams, WCFApisConstants.HASH, URLEncoder.encode(hash, "UTF-8"));
            return musicConfig.getWcfWebViewBaseUrl().concat(uri).concat(queryParams.toString());
        } catch (Exception e) {
            logger.error("Unable to append query params to static base url. Error : {} Request : {}", e.toString(), ChannelContext.getRequest().toString());
        }
        return musicConfig.getWcfWebViewBaseUrl().concat(uri);
    }

    public void appendQueryParam(StringBuilder queryParams, String key, Object val) {
        queryParams.append("&" + key + "=" + val);
    }

    private void setWynkDirectConfig(JSONObject jsonObject) {
        if (ChannelContext.getBuildnumber() != null && MusicBuildUtils.isGreaterBuildNumber(ChannelContext.getBuildnumber(), 93)) {
            JSONObject wynkDirectConfig = new JSONObject();
            JSONArray disabledDevices = new JSONArray();
            JSONObject disabledDevice = new JSONObject();
            disabledDevice.put("devicetype", "ONEPLUS A3003");
            disabledDevice.put("osversion", 26);
            disabledDevices.add(disabledDevice);
            wynkDirectConfig.put("enable", true);
            wynkDirectConfig.put("disabled_devices", disabledDevices);
            jsonObject.put("wynk_direct", wynkDirectConfig);
        }
    }

    private Boolean getGeoRestrictionPassed(JSONObject simInfo) {
        Boolean geoRestrictionPassed = isGeoRestrictionPassed();
        if (!geoRestrictionPassed && intlRoamingService.isIndianMCC(simInfo, JsonKeyNames.MCC)) {
            geoRestrictionPassed = true;
            createAllowedMCCStreamLog(ChannelContext.getRequest(), ChannelContext.getUid(), MusicDeviceUtils.getUserDevice(), intlRoamingService.getSimInfoArray(simInfo), "mcc_allow_config",
                    String.join(",", MusicUtils.getIPsListFromXForwardedFor(ChannelContext.getRequest())));
        }
        return geoRestrictionPassed;
    }

    private void setAutoSuggestConfig(JSONObject musicConfig) {
        JSONObject autoSuggestConfig = new JSONObject();
        autoSuggestConfig.put(JsonKeyNames.AS_ENABLE, true);
        autoSuggestConfig.put(JsonKeyNames.AS_LOCAL_ENABLE, true);
        autoSuggestConfig.put(JsonKeyNames.AS_MIN_CHAR, 3);
        autoSuggestConfig.put(JsonKeyNames.AS_LOCAL_MIN_CHAR, 5);
        autoSuggestConfig.put(JsonKeyNames.AS_MAX_CHAR, 12);
        musicConfig.put(JsonKeyNames.AUTO_SUGGEST, autoSuggestConfig);
    }

    private void setVersionUpgradeForIntlRoaming(boolean isAndroidDevice, JSONObject versionJson) {
        try {
            int minVersion = intlRoamingService.forceUpgradeCheck(isAndroidDevice);
            if (versionJson.containsKey(JsonKeyNames.MIN_ANDROID_VERSION)) {
                int currentMinAndroidVersion = (Integer) versionJson.get(JsonKeyNames.MIN_ANDROID_VERSION);
                if (currentMinAndroidVersion <= musicConfig.getIntlRoamingAndroidVersionsToUpgrade() && minVersion > currentMinAndroidVersion && isAndroidDevice) {
                    versionJson.put(JsonKeyNames.MIN_ANDROID_VERSION, minVersion);
                }
            }
            if (versionJson.containsKey(JsonKeyNames.MIN_IOS_VERSION)) {
                int currentMinIosVersion = (Integer) versionJson.get(JsonKeyNames.MIN_IOS_VERSION);
                if (currentMinIosVersion <= musicConfig.getIntlRoamingIosVersionsToUpgrade() && minVersion > currentMinIosVersion && !isAndroidDevice) {
                    versionJson.put(JsonKeyNames.MIN_IOS_VERSION, minVersion);
                }
            }
        } catch (Exception e) {
            logger.error("International roaming : unable to fetch and update build check" + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void processIntlRoamingFlow(Boolean configOnly, JSONObject simInfo, JSONObject jsonObject) {
        jsonObject.put("internationalRoaming", false);
        //international Roaming
        if (intlRoamingService.isIntlRoamingEnabled()) {
            try {
                JSONObject intlRoamingResponse = intlRoamingService.intlRoamingFlow(simInfo, !configOnly);
                boolean deviceOnIntlRoaming = intlRoamingService.isDeviceOnIntlRoaming();
                jsonObject.put("internationalRoaming", deviceOnIntlRoaming);
                if (!intlRoamingResponse.isEmpty()) {
                    Integer intlRoamingCode = (Integer) intlRoamingResponse.get("code");
                    if (MusicConstants.INTL_ROAMING_NOTIF_DEFAULT_CODE != intlRoamingCode) {
                        jsonObject.put(JsonKeyNames.INTERNATIONAL_ROAMING_OFFER, intlRoamingResponse);
                    }
                    // set international roaming flag as false, if expired.
                    if (MusicConstants.INTL_ROAMING_EXPIRED == intlRoamingCode) {
                        jsonObject.put("internationalRoaming", false);
                    }
                }
            } catch (Exception e) {
                logger.error("International roaming : Error in getting config" + e.getMessage());
            }
        }
    }

    private JSONArray getThirdPartAppInstallConfig(boolean isAndroidDevice) {
        JSONObject thirdPartyConf = userConfigService.getThirdPartyAppConfig();
        if (isAndroidDevice) {
            if (thirdPartyConf.containsKey("androidPkgList"))
                return (JSONArray) thirdPartyConf.get("androidPkgList");
        } else {
            if (thirdPartyConf.containsKey("iosPkgList"))
                return (JSONArray) thirdPartyConf.get("iosPkgList");
        }
        return MusicConstants.EMPTY_JSON_ARRAY;
    }

//    private JSONObject getTopPlaylistNavigationMeta() {
//        NavigationMetadata nvm = new NavigationMetadata();
//        nvm.setTitle("My Top Playlists");
//        MusicPackageMetadata mtd = null;
//        for (MusicContentLanguage contentLanguage : ChannelContext.getContentLang()) {
//            mtd = musicContentService.getPkgForTopAlbumsPlaylistsCharts(MusicPackageType.BSB_PLAYLISTS, contentLanguage);
//            if (mtd != null)
//                break;
//        }
//
//        if (mtd == null)
//            return null;
//        nvm.setId(mtd.getId());
//        nvm.setType(MusicContentType.PACKAGE);
//        AppScreenMapping app = new AppScreenMapping();
//        app.setScreenCode(ScreenCode.PACKAGE_GRID);
//        app.setTargetContentType(MusicContentType.PACKAGE);
//        app.setTargetContentId(mtd.getId());
//        nvm.setAppScreenMapping(app);
//        nvm.setContentState(MusicContentState.LIVE);
//        return nvm.toJsonObject();
//
//    }

    private JSONObject getFUPConfig() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonKeyNames.TOTAL, Integer.MAX_VALUE);
        jsonObject.put(JsonKeyNames.FUP_CURRENT_COUNT, -1);
        jsonObject.put(JsonKeyNames.FUP_LIMIT_STATUS, false);
        return jsonObject;
    }

    public List<RecentSong> getLastNDaysRecentSongCount(String userId, int count, long sinceTimeStamp) {
        List<RecentSong> songs = userSongDAO.getRecentlyPlayedSongs(userId);
        if (count == 0) {
            return songs;
        }
        return songs.stream().filter(e -> e.getUpdatedOn().getTime() > sinceTimeStamp).sorted().limit(count).collect(Collectors.toList());
    }

    private JSONObject getOnDeviceConfigs(String archType) {

        int onDeviceBucket = getUserOnDeviceBucket();
        int metaMatchPayload;
        int fingerPrintPayload;

        metaMatchPayload = METAMATCH_PAYLOAD_SIZE;
        fingerPrintPayload = FINGERPRINT_PAYLOAD_SIZE;
        //making payloadSize and pollingPayloadSize 0 as per asked by andriod team in task WAND-339
        JSONObject jsonObject = new JSONObject();
        JSONObject metaMatchingConfig = new JSONObject();
        metaMatchingConfig.put("minScanDuration", ONDEVICE_MINIMUM_DURATION_SCAN);
        metaMatchingConfig.put("payloadSize", metaMatchPayload);
        metaMatchingConfig.put("playlistThreshold", PLAYLIST_THRESHOLD);
        metaMatchingConfig.put("metaMappingRepeatInterval", META_MAPPING_REPEAT_INTERVAL);
        jsonObject.put("metaMatch", metaMatchingConfig);

        JSONObject fingerPrintConfig = new JSONObject();
        fingerPrintConfig.put("payloadSize", fingerPrintPayload);
        //Multiple of payloadSize of FP
        fingerPrintConfig.put("delimiterPayloadSize", fingerPrintPayload * 5);
        fingerPrintConfig.put("pollingPayloadSize", FINGERPRINT_FETCH_PAYLOAD_SIZE);
        fingerPrintConfig.put("fpPollingInterval", FINGERPRINT_POLLING_INTERVAL);
        JSONArray chunkArray = new JSONArray();
        JSONObject chunk1 = new JSONObject();
        chunk1.put("start", 10);
        chunk1.put("duration", 40);
        chunkArray.add(chunk1);

        fingerPrintConfig.put("codeChunk", chunkArray);

        if (!StringUtils.isEmpty(archType) && fingerPrintPayload > 0) {
            String[] abis = archType.split(",");
            for (String abi : abis) {
                if (abi.contains("arm")) {
                    //send empty string as per asked by andriod team in task WAND-339
                    fingerPrintConfig.put("ffmpegFileLocation", "");
                    break;
                } else if (abi.contains("x86")) {
                    //send empty string as per asked by andriod team in task WAND-339
                    fingerPrintConfig.put("ffmpegFileLocation", "");
                    break;
                } else if (abi.contains("mips")) {
                    //no ffmpeg file for mips as of now
                }
            }
        }

        fingerPrintConfig.put("ffmpegVersion", 1);
        fingerPrintConfig.put("fingerprintOnData", true);

        jsonObject.put("fingerPrint", fingerPrintConfig);

        return jsonObject;
    }

    private int getUserOnDeviceBucket() {

        String uid = ChannelContext.getUid();

        //bypass HACK for QA !!
        if (MusicUtils.isFPMetaDefaultUid(uid))
            return 1;

        int hashCode = Math.abs(uid.hashCode());
        int mod = hashCode % 10;
        if (mod < 7) {
            return 1;
        }
        return 2;
    }

    private JSONObject getVersionConfig() {
        JSONObject versionJsonObject = new JSONObject();

        String minAndroidVersion = getVersionInfo(MusicConstants.REDIS_MIN_ANDROID_VERSION_KEY);
        int minAndroidVersionInt = ObjectUtils.getNumber(minAndroidVersion, 0).intValue();

        String miniOSVersion = getVersionInfo(MusicConstants.REDIS_MIN_IOS_VERSION_KEY);
        int miniOSVersionInt = ObjectUtils.getNumber(miniOSVersion, 0).intValue();

        String minWindowsVersion = getVersionInfo(MusicConstants.REDIS_MIN_WINDOWS_VERSION_KEY);
        int minWindowsVersionInt = ObjectUtils.getNumber(minWindowsVersion, 0).intValue();

        String targetAndroidVersion = getVersionInfo(MusicConstants.REDIS_TARGET_ANDROID_VERSION_KEY);
        int targetAndroidVersionInt = ObjectUtils.getNumber(targetAndroidVersion, 0).intValue();

        String targetiOSVersion = getVersionInfo(MusicConstants.REDIS_TARGET_IOS_VERSION_KEY);
        int targetiOSVersionInt = ObjectUtils.getNumber(targetiOSVersion, 0).intValue();

        String targetWindowsVersion = getVersionInfo(MusicConstants.REDIS_TARGET_WINDOWS_VERSION_KEY);
        int targetWindowsVersionInt = ObjectUtils.getNumber(targetWindowsVersion, 0).intValue();

        if (StringUtils.isBlank(minAndroidVersion) && StringUtils.isBlank(miniOSVersion) && StringUtils.isBlank(minWindowsVersion) && StringUtils.isBlank(targetAndroidVersion) && StringUtils
                .isBlank(targetiOSVersion) && StringUtils.isBlank(targetWindowsVersion))
            return null;

        if (minAndroidVersionInt > 0)
            versionJsonObject.put(JsonKeyNames.MIN_ANDROID_VERSION, minAndroidVersionInt);

        if (miniOSVersionInt > 0)
            versionJsonObject.put(JsonKeyNames.MIN_IOS_VERSION, miniOSVersionInt);

        if (minWindowsVersionInt > 0)
            versionJsonObject.put(JsonKeyNames.MIN_WINDOWS_VERSION, minWindowsVersionInt);

        if (targetAndroidVersionInt > 0)
            versionJsonObject.put(JsonKeyNames.TARGET_ANDROID_VERSION, targetAndroidVersionInt);

        if (targetiOSVersionInt > 0)
            versionJsonObject.put(JsonKeyNames.TARGET_IOS_VERSION, targetiOSVersionInt);

        if (targetWindowsVersionInt > 0)
            versionJsonObject.put(JsonKeyNames.TARGET_WINDOWS_VERSION, targetWindowsVersionInt);

        String apkDownloadUrl = musicConfig.getBaseFeUrl() + "music/android/download.html";

        Map<String, String> paramValues = new HashMap<>();
        Language userLang = Language.getLanguageById(ChannelContext.getLang());
        versionJsonObject.put(JsonKeyNames.CRITICAL_UPDATE_MESSAGE, NotificationsText.getLangNotificationWithParams(MusicNotificationType.CRITICAL_UPDATE_MESSAGE, userLang, paramValues));
        versionJsonObject.put(JsonKeyNames.CRITICAL_UPDATE_TITLE, NotificationsText.getLangNotificationWithParams(MusicNotificationType.CRITICAL_UPDATE_TITLE, userLang, paramValues));
        versionJsonObject.put(JsonKeyNames.UPDATE_AVAILABLE_MESSAGE, NotificationsText.getLangNotificationWithParams(MusicNotificationType.UPDATE_AVAILABLE_MESSAGE, userLang, paramValues));
        versionJsonObject.put(JsonKeyNames.UPDATE_AVAILABLE_TITLE, NotificationsText.getLangNotificationWithParams(MusicNotificationType.UPDATE_AVAILABLE_TITLE, userLang, paramValues));
        //versionJsonObject.put(JsonKeyNames.APK_DOWNLOAD_URL, apkDownloadUrl);
        return versionJsonObject;
    }

    /*** Bought and Rented
     ***/

    public String getUserCollections() {
        User user = ChannelContext.getUser();
        if (user == null)
            return JsonUtils.EMPTY_JSON.toJSONString();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ID, MusicPackageType.USER_COLLECTIONS.getName());
        jsonObject.put(TITLE, MusicPackageType.USER_COLLECTIONS.getLabel());
        jsonObject.put(TYPE, MusicContentType.PACKAGE.name());

        JSONArray itemsArray = new JSONArray();
        JSONObject rentalsCollection = getRentalsCollection();
        itemsArray.add(rentalsCollection);
        JSONObject downloadsCollection = getDownloadsCollection();
        itemsArray.add(downloadsCollection);
        JSONObject favoritesCollection = getFavoritesCollections();
        itemsArray.add(favoritesCollection);
        JSONObject journeyCollection = getJourneyCollection();
        itemsArray.add(journeyCollection);
        jsonObject.put(ITEMS, itemsArray);
        jsonObject.put(TOTAL, itemsArray.size());
        jsonObject.put(OFFSET, 0);
        jsonObject.put(COUNT, itemsArray.size());
        jsonObject.put(LANG, ChannelContext.getLang());
        jsonObject.put(ITEMTYPES, MusicUtils.getItemTypesJson(MusicPackageType.USER_COLLECTIONS));

        return jsonObject.toJSONString();

    }

    private JSONObject getRentalsCollection() {
        JSONObject rentalsJson = new JSONObject();
        rentalsJson.put(ID, MusicPackageType.USER_RENTALS.getName());
        rentalsJson.put(SUBTYPE, MusicPackageType.USER_RENTALS.getName());
        rentalsJson.put(TITLE, TITLE_RENTALS);
        rentalsJson.put(TYPE, MusicContentType.PACKAGE.name());
        rentalsJson.put(TOTAL, 0);
        rentalsJson.put(OFFSET, 0);
        rentalsJson.put(COUNT, 0);
        rentalsJson.put(SMALL_IMAGE, "");
        rentalsJson.put(LARGE_IMAGE, "");
        rentalsJson.put(EMPTY_STATE_ICON, ARROW);
        rentalsJson.put(LANG, ChannelContext.getLang());
        rentalsJson.put(ITEMTYPES, MusicUtils.getItemTypesJson(MusicPackageType.USER_RENTALS));
        return rentalsJson;
    }

    public JSONObject getDownloadsCollection() {
        JSONObject purchaseJson = new JSONObject();
        purchaseJson.put(ID, MusicPackageType.USER_DOWNLOADS.getName());
        purchaseJson.put(SUBTYPE, MusicPackageType.USER_DOWNLOADS.getName());
        purchaseJson.put(TITLE, TITLE_DOWNLOADS);
        purchaseJson.put(TYPE, MusicContentType.PACKAGE.name());
        purchaseJson.put(TOTAL, 0);
        purchaseJson.put(THUMBNAIL, "");
        purchaseJson.put(SMALL_IMAGE, "");
        purchaseJson.put(LARGE_IMAGE, "");
        purchaseJson.put(EMPTY_STATE_ICON, Purchased);
        purchaseJson.put(OFFSET, 0);
        purchaseJson.put(COUNT, 0);
        purchaseJson.put(LANG, ChannelContext.getLang());
        purchaseJson.put(ITEMTYPES, MusicUtils.getItemTypesJson(MusicPackageType.USER_DOWNLOADS));
        return purchaseJson;
    }

    public JSONObject getFavoritesCollections() {
        JSONObject favJson = new JSONObject();
        favJson.put(ID, MusicPackageType.USER_FAVORITES.getName());
        favJson.put(SUBTYPE, MusicPackageType.USER_FAVORITES.getName());
        favJson.put(TITLE, TITLE_FAVORITES);
        favJson.put(TYPE, MusicContentType.PACKAGE.name());
        favJson.put(TOTAL, 0);
        favJson.put(OFFSET, 0);
        favJson.put(COUNT, 0);
        favJson.put(LANG, ChannelContext.getLang());
        favJson.put(ITEMTYPES, MusicUtils.getItemTypesJson(MusicPackageType.USER_FAVORITES));
        favJson.put(SMALL_IMAGE, "");
        favJson.put(LARGE_IMAGE, "");
        favJson.put(EMPTY_STATE_ICON, HEART);
        return favJson;
    }

    private JSONObject getJourneyCollection() {
        JSONObject journeyJson = new JSONObject();
        journeyJson.put(ID, MusicPackageType.USER_JOURNEY.getName());
        journeyJson.put(SUBTYPE, MusicPackageType.USER_JOURNEY.getName());
        journeyJson.put(TITLE, TITLE_JOURNEY);
        journeyJson.put(TYPE, MusicContentType.PACKAGE.name());
        journeyJson.put(TOTAL, 0);
        journeyJson.put(OFFSET, 0);
        journeyJson.put(COUNT, 0);
        journeyJson.put(LANG, ChannelContext.getLang());
        return journeyJson;
    }

    public void updateUserPlaylistToNewUser(String oldUserId, String newUserId) {
        if (StringUtils.isEmpty(newUserId)) {
            return;
        }
        Map<String, Object> queryParams = new HashMap();
        queryParams.put(OWNERID, oldUserId);
        queryParams.put(DELETED, false);

        Map<String, Object> queryValues = new HashMap();
        queryValues.put(OWNERID, newUserId);

        mongoMusicDBManager.setField(USER_PLAYLIST_COLLECTION, queryParams, queryValues, false, true);
    }

    public void updateADHMPlaylistToNewUser(String oldUserId, String newUserId) {

        try {
            if (StringUtils.isEmpty(newUserId)) {
                return;
            }
            String jsonString = musicShardRedisServiceManager.get(String.format(MusicConstants.REDIS_ADHM_PLAYLIST_HASH, oldUserId));
            if (StringUtils.isEmpty(jsonString))
                return;

            //set playlist for newuser id
            musicShardRedisServiceManager.set(String.format(MusicConstants.REDIS_ADHM_PLAYLIST_HASH, newUserId), jsonString);
            //delete playlist from old userId
            musicShardRedisServiceManager.delete(String.format(MusicConstants.REDIS_ADHM_PLAYLIST_HASH, oldUserId));
        } catch (Exception e) {
            logger.error("Error migrating ADHM playlist from " + oldUserId + " to " + newUserId);
        }

    }

    public void removeUserJourney(String uid) {
        String journeyKey = "m_journey_" + uid;
        userPersistantRedisServiceManager.delete(journeyKey);
    }

    /**
     * createEventLog
     *
     * @param event
     * @return String
     * https://docs.google.com/a/bsb.in/document/d/1ZK9sR91QLHtgDi1aFNZ4aflPBTgQ71OUIqecQoikm4o/edit
     * time_stamp, ip, user_id, x-rat, circle_id, language_id, device_no, network operator, os, os version, app_version, current_screen_id, search_keyword,
     * item_id, activity_type_id,action_id, action_parameters, app_type, server_time, content_lang, device_id, user_type
     */
    private String createEventLog(UserActivityEvent event, User user, UserDevice device) {
        StringBuffer logBuff = new StringBuffer();

        //Timestamp
        appendParam(logBuff, Long.toString(event.getTimestamp()));

        //ip
        appendParam(logBuff, "-");

        //UUID
        appendParam(logBuff, user.getUid());

        //X-rat
        appendParam(logBuff, "-");
        //Circle Info
        appendParam(logBuff, user.getCircle());
        //Language Info
        appendParam(logBuff, user.getLang());
        //Device Name Info
        if (device != null)
            appendParam(logBuff, replaceComma(device.getDeviceType()));
        else
            appendParam(logBuff, "-");

        //Network Operator
        appendParam(logBuff, "-");

        //OS and OS Version
        if (device != null)
            appendParam(logBuff, replaceComma(device.getOs()));
        else
            appendParam(logBuff, "-");

        if (device != null)
            appendParam(logBuff, replaceComma(device.getOsVersion()));
        else
            appendParam(logBuff, "-");

        if (device != null)
            appendParam(logBuff, replaceComma(device.getAppVersion()));
        else
            appendParam(logBuff, "-");

        //App Screen ID
        JSONObject eventMeta = event.getMeta();
        if ((eventMeta != null) && (eventMeta.get(SCREEN_ID) != null)) {
            appendParam(logBuff, eventMeta.get(SCREEN_ID).toString());
        } else {
            appendParam(logBuff, null);
        }

        //Search Keyword
        if ((eventMeta != null) && (eventMeta.get("keyword") != null)) {
            appendParam(logBuff, replaceComma(eventMeta.get("keyword").toString()));
        } else {
            appendParam(logBuff, null);
        }

        // Item_id
        if ((eventMeta != null) && (((JSONObject) eventMeta).get(ID) != null)) {
            appendParam(logBuff, ((JSONObject) eventMeta).get(ID).toString()); // This Item ID of song/album/playlist
        } else {
            appendParam(logBuff, null);
        }

        //Item_type / Activity_type
        if ((eventMeta != null) && (((JSONObject) eventMeta).get(TYPE) != null)) {
            appendParam(logBuff, ((JSONObject) eventMeta).get(TYPE).toString().toUpperCase());
        } else {
            appendParam(logBuff, null);
        }

        //Event Action
        if (event != null && event.getType() != null) {
            appendParam(logBuff, event.getType().toString().toUpperCase());
        } else {
            appendParam(logBuff, null);
        }

        //Event Action param
        String actionParam = checkAndGetActionParam(event);
        String modifiedActionParam = replaceComma(actionParam);
        appendParam(logBuff, modifiedActionParam);

        //App OR Wap OR Basic parameter
        if(WynkAppType.isWynkBasicApp()) {
            appendParam(logBuff, WynkAppType.BASIC_APP);
        } else  {
            String app_wap_param = event.getAppType();
            appendParam(logBuff, MusicUtils.getAppType());
        }

        //Server Timestamp
        appendParam(logBuff, Long.toString(new Date().getTime()));

        //Music Preference Content Language Info
        String contentLang = "-";
        List<String> contentLanguages = user.getSelectedLanguages();
        if (!CollectionUtils.isEmpty(contentLanguages))
            contentLang = contentLanguages.get(0);
        appendParam(logBuff, contentLang);

        // DeviceId
        appendParam(logBuff, device.getDeviceId());

        // Prepaid / Postpaid
        appendParam(logBuff, user.getUserType());

        return logBuff.toString();
    }

    /**
     * createEventLogNoAuth
     *
     * @param event
     * @param request
     * @return String
     * https://docs.google.com/a/bsb.in/document/d/1ZK9sR91QLHtgDi1aFNZ4aflPBTgQ71OUIqecQoikm4o/edit
     * time_stamp, ip, user_id, x-rat, circle_id, language_id, device_no, network operator, os, os version, app_version, current_screen_id, search_keyword,
     * item_id, activity_type_id,action_id, action_parameters, app_type, server_time, content_lang, device_id, user_type
     */
    public String createEventLogNoAuth(UserActivityEvent event, HttpRequest request) {
        StringBuffer logBuff = new StringBuffer();

        //Timestamp
        appendParam(logBuff, Long.toString(event.getTimestamp()));

        //ip
        appendParam(logBuff, UserDeviceUtils.getClientIP(request));

        appendParam(logBuff, "-");

        //X-rat
        appendParam(logBuff, checkAndGetXRatSnetAndNet(event, request));
        //Circle Info
        appendParam(logBuff, "-");
        //Language Info
        appendParam(logBuff, event.getLang());
        //Device Name Info
        appendParam(logBuff, "-");
        //Network Operator
        appendParam(logBuff, "-");

        //OS and OS Version
        appendParam(logBuff, "-");
        appendParam(logBuff, "-");
        // App Version
        appendParam(logBuff, "-");

        //App Screen ID
        JSONObject eventMeta = event.getMeta();
        if ((eventMeta != null) && (eventMeta.get(SCREEN_ID) != null)) {
            appendParam(logBuff, eventMeta.get(SCREEN_ID).toString());
        } else {
            if ((eventMeta != null) && (event != null) && (eventMeta.get(ID) != null) && (event.getType() == UserEventType.SCREEN_CLOSED || event.getType() == UserEventType.SCREEN_OPENED))
                appendParam(logBuff, eventMeta.get(ID).toString());
            else
                appendParam(logBuff, null);
        }

        //Search Keyword
        appendParam(logBuff, "-");

        // Item_id
        if ((eventMeta != null) && (((JSONObject) eventMeta).get(ID) != null)) {
            appendParam(logBuff, ((JSONObject) eventMeta).get(ID).toString()); // This Item ID of song/album/playlist
        } else {
            appendParam(logBuff, null);
        }

        //Item_type / Activity_type
        if ((eventMeta != null) && (((JSONObject) eventMeta).get(TYPE) != null)) {
            appendParam(logBuff, ((JSONObject) eventMeta).get(TYPE).toString().toUpperCase());
        } else {
            appendParam(logBuff, null);
        }

        //Event Action
        if (event != null && event.getType() != null) {
            appendParam(logBuff, event.getType().toString().toUpperCase());
        } else {
            appendParam(logBuff, null);
        }

        //Event Action param
        String actionParam = checkAndGetActionParam(event);
        String modifiedActionParam = replaceComma(actionParam);
        appendParam(logBuff, modifiedActionParam);

        //App OR Wap OR Basic parameter
        if(WynkAppType.isWynkBasicApp()) {
            appendParam(logBuff, WynkAppType.BASIC_APP);
        } else  {
            appendParam(logBuff, "-");
        }

        //Server Timestamp
        appendParam(logBuff, Long.toString(new Date().getTime()));

        //Music Preference Content Language Info
        String contentLang = "-";
        String userType = "-";

        appendParam(logBuff, contentLang);

        // DeviceId
        appendParam(logBuff, "-");

        // Prepaid / Postpaid
        appendParam(logBuff, userType);

        return logBuff.toString();
    }

    /**
     * createEventLog
     *
     * @param event
     * @param request
     * @return String
     * https://docs.google.com/a/bsb.in/document/d/1ZK9sR91QLHtgDi1aFNZ4aflPBTgQ71OUIqecQoikm4o/edit
     * time_stamp, ip, user_id, x-rat, circle_id, language_id, device_no, network operator, os, os version, app_version, current_screen_id, search_keyword,
     * item_id, activity_type_id,action_id, action_parameters, app_type, server_time, content_lang, device_id, user_type
     */
    public String createEventLog(UserActivityEvent event, HttpRequest request) {
        StringBuffer logBuff = new StringBuffer();

        //Timestamp
        appendParam(logBuff, Long.toString(event.getTimestamp()));

        //ip
        appendParam(logBuff, UserDeviceUtils.getClientIP(request));

        //UUID
        if (ChannelContext.getUid() != null) {
            appendParam(logBuff, ChannelContext.getUid());
        } else {
            appendParam(logBuff, null);
        }

        //X-rat
        appendParam(logBuff, checkAndGetXRatSnetAndNet(event, request));
        //Circle Info
        appendParam(logBuff, accountService.getCircleShortName());
        //Language Info
        appendParam(logBuff, event.getLang());
        //Device Name Info
        UserDevice device = MusicDeviceUtils.getUserDevice();
        if (device != null)
            appendParam(logBuff, device.getDeviceType());
        else
            appendParam(logBuff, "-");
        //Network Operator
        if (!StringUtils.isBlank(accountService.getNetworkOperator()))
            appendParam(logBuff, accountService.getNetworkOperator().replace(",", "|"));
        else
            appendParam(logBuff, "-");

        //OS and OS Version
        appendParam(logBuff, replaceComma(accountService.getOS()));
        appendParam(logBuff, replaceComma(accountService.getOSVersion()));
        // App Version
        appendParam(logBuff, accountService.getAppVersion());

        //App Screen ID
        JSONObject eventMeta = event.getMeta();
        if ((eventMeta != null) && (eventMeta.get(SCREEN_ID) != null)) {
            appendParam(logBuff, eventMeta.get(SCREEN_ID).toString());
        } else {
            if ((eventMeta != null) && (event != null) && (eventMeta.get(ID) != null) && (event.getType() == UserEventType.SCREEN_CLOSED || event.getType() == UserEventType.SCREEN_OPENED))
                appendParam(logBuff, eventMeta.get(ID).toString());
            else
                appendParam(logBuff, null);
        }


        //Search Keyword
        if ((eventMeta != null) && (eventMeta.get("keyword") != null)) {
            appendParam(logBuff, replaceComma(eventMeta.get("keyword").toString()));
        } else {
            appendParam(logBuff, null);
        }

        // Item_id
        if ((eventMeta != null) && (((JSONObject) eventMeta).get(ID) != null)) {
            appendParam(logBuff, ((JSONObject) eventMeta).get(ID).toString()); // This Item ID of song/album/playlist
        } else {
            appendParam(logBuff, null);
        }

        //Item_type / Activity_type
        if ((eventMeta != null) && (((JSONObject) eventMeta).get(TYPE) != null)) {
            appendParam(logBuff, ((JSONObject) eventMeta).get(TYPE).toString().toUpperCase());
        } else {
            appendParam(logBuff, null);
        }

        //Event Action
        if (event != null && event.getType() != null) {
            appendParam(logBuff, event.getType().toString().toUpperCase());
        } else {
            appendParam(logBuff, null);
        }

        //Event Action param
        String actionParam = checkAndGetActionParam(event);
        String modifiedActionParam = replaceComma(actionParam);
        if(event.getType() == UserEventType.MOBILE_CONNECT) {
          actionParam = "";
            if (event.getId() != null) actionParam = appendActionParam(actionParam, ID + "=" + event.getId().toString());
            if (eventMeta.get("authcode") != null) actionParam = appendActionParam(actionParam, "authcode=" +eventMeta.get("authcode").toString());
            if (eventMeta.get("access_token") != null) actionParam = appendActionParam(actionParam, "access_token=" + eventMeta.get("access_token").toString());
            if (eventMeta.get("error") != null) actionParam = appendActionParam(actionParam, "error=" + eventMeta.get("error").toString());
            if (eventMeta.get("error_description") != null) actionParam = appendActionParam(actionParam, "error_description=" + eventMeta.get("error_description").toString());
            if (event.getId() != null) actionParam = appendActionParam(actionParam, ID + "=" + event.getId());
            modifiedActionParam = actionParam;
        }
        appendParam(logBuff, modifiedActionParam);

        //App OR Wap OR Basic parameter
        if(WynkAppType.isWynkBasicApp()) {
            appendParam(logBuff, WynkAppType.BASIC_APP);
        } else  {
            String app_wap_param = event.getAppType();
            appendParam(logBuff, MusicUtils.getAppType());
        }

        //Server Timestamp
        appendParam(logBuff, Long.toString(new Date().getTime()));

        //Music Preference Content Language Info
        String contentLang = "-";
        String userType = "-";
        if (ChannelContext.getUser() != null) {
            List<String> contentLanguages = ChannelContext.getUser().getSelectedLanguages();
            if (contentLanguages != null && !CollectionUtils.isEmpty(contentLanguages))
                contentLang = Utils.ConvertContentLangStringListToString(contentLanguages);
            userType = ChannelContext.getUser().getUserType();
        }

        appendParam(logBuff, contentLang);

        // DeviceId
        appendParam(logBuff, accountService.getDeviceId());

        // Prepaid / Postpaid
        appendParam(logBuff, userType);

        return logBuff.toString();
    }

    public String createEventLog(UserActivityEvent event, String uid, UserDevice device, HttpRequest request) {
        StringBuffer logBuff = new StringBuffer();

        //Timestamp
        appendParam(logBuff, Long.toString(event.getTimestamp()));

        //ip
        appendParam(logBuff, UserDeviceUtils.getClientIP(request));

        //UUID
        appendParam(logBuff, uid);

        //X-rat
        appendParam(logBuff, checkAndGetXRatSnetAndNet(event, request));
        //Circle Info
        appendParam(logBuff, accountService.getCircleShortName());
        //Language Info
        appendParam(logBuff, event.getLang());

        //Device Name Info
        if (device != null)
            appendParam(logBuff, device.getDeviceType());
        else
            appendParam(logBuff, "-");

        //Network Operator
        if (!StringUtils.isBlank(accountService.getNetworkOperator()))
            appendParam(logBuff, accountService.getNetworkOperator().replace(",", "|"));
        else
            appendParam(logBuff, "-");

        //OS and OS Version
        if (device != null)
            appendParam(logBuff, replaceComma(device.getOs()));
        else
            appendParam(logBuff, accountService.getOS());

        if (device != null)
            appendParam(logBuff, replaceComma(device.getOsVersion()));
        else
            appendParam(logBuff, accountService.getOSVersion());

        // App Version
        if (device != null)
            appendParam(logBuff, device.getAppVersion());
        else
            appendParam(logBuff, accountService.getAppVersion());

        //App Screen ID
        JSONObject eventMeta = event.getMeta();
        if ((eventMeta != null) && (eventMeta.get(SCREEN_ID) != null)) {
            appendParam(logBuff, eventMeta.get(SCREEN_ID).toString());
        } else {
            if ((eventMeta != null) && (event != null) && (eventMeta.get(ID) != null) && (event.getType() == UserEventType.SCREEN_CLOSED || event.getType() == UserEventType.SCREEN_OPENED))
                appendParam(logBuff, eventMeta.get(ID).toString());
            else
                appendParam(logBuff, null);
        }

        //Search Keyword
        if ((eventMeta != null) && (eventMeta.get("keyword") != null)) {
            appendParam(logBuff, replaceComma(eventMeta.get("keyword").toString()));
        } else {
            appendParam(logBuff, null);
        }

        // Item_id
        if ((eventMeta != null) && (((JSONObject) eventMeta).get(ID) != null)) {
            appendParam(logBuff, ((JSONObject) eventMeta).get(ID).toString()); // This Item ID of song/album/playlist
        } else {
            appendParam(logBuff, null);
        }

        //Item_type / Activity_type
        if ((eventMeta != null) && (((JSONObject) eventMeta).get(TYPE) != null)) {
            appendParam(logBuff, ((JSONObject) eventMeta).get(TYPE).toString().toUpperCase());
        } else {
            appendParam(logBuff, null);
        }

        //Event Action
        if (event != null && event.getType() != null) {
            appendParam(logBuff, event.getType().toString().toUpperCase());
        } else {
            appendParam(logBuff, null);
        }

        //Event Action param
        String actionParam = checkAndGetActionParam(event);
        String modifiedActionParam = replaceComma(actionParam);
        appendParam(logBuff, modifiedActionParam);

        //App OR Wap OR Basic parameter
        if(WynkAppType.isWynkBasicApp()) {
            appendParam(logBuff, WynkAppType.BASIC_APP);
        } else  {
            String app_wap_param = event.getAppType();
            appendParam(logBuff, MusicUtils.getAppType());
        }

        //Server Timestamp
        appendParam(logBuff, Long.toString(new Date().getTime()));

        //Music Preference Content Language Info
        String contentLang = "-";
        String userType = "-";

        appendParam(logBuff, contentLang);

        // DeviceId
        if (device != null)
            appendParam(logBuff, device.getDeviceId());
        else
            appendParam(logBuff, accountService.getDeviceId());

        // Prepaid / Postpaid
        appendParam(logBuff, userType);

        return logBuff.toString();
    }

    public String createEventLog(UserActivityEvent event, String uid, UserDevice device) {
        StringBuffer logBuff = new StringBuffer();

        //Timestamp
        appendParam(logBuff, Long.toString(event.getTimestamp()));

        //ip
        appendParam(logBuff, "-");

        //UUID
        appendParam(logBuff, uid);

        //X-rat
        appendParam(logBuff, "-");
        //Circle Info
        appendParam(logBuff, "-");
        //Language Info
        appendParam(logBuff, "-");
        //Device Name Info
        if (device != null)
            appendParam(logBuff, replaceComma(device.getDeviceType()));
        else
            appendParam(logBuff, "-");

        //Network Operator
        appendParam(logBuff, "-");

        //OS and OS Version
        if (device != null)
            appendParam(logBuff, replaceComma(device.getOs()));
        else
            appendParam(logBuff, "-");

        if (device != null)
            appendParam(logBuff, replaceComma(device.getOsVersion()));
        else
            appendParam(logBuff, "-");

        if (device != null)
            appendParam(logBuff, replaceComma(device.getAppVersion()));
        else
            appendParam(logBuff, "-");

        //App Screen ID
        JSONObject eventMeta = event.getMeta();
        if ((eventMeta != null) && (eventMeta.get(SCREEN_ID) != null)) {
            appendParam(logBuff, eventMeta.get(SCREEN_ID).toString());
        } else {
            appendParam(logBuff, null);
        }

        //Search Keyword
        if ((eventMeta != null) && (eventMeta.get("keyword") != null)) {
            appendParam(logBuff, replaceComma(eventMeta.get("keyword").toString()));
        } else {
            appendParam(logBuff, null);
        }

        // Item_id
        if ((eventMeta != null) && (((JSONObject) eventMeta).get(ID) != null)) {
            appendParam(logBuff, ((JSONObject) eventMeta).get(ID).toString()); // This Item ID of song/album/playlist
        } else {
            appendParam(logBuff, null);
        }

        //Item_type / Activity_type
        if ((eventMeta != null) && (((JSONObject) eventMeta).get(TYPE) != null)) {
            appendParam(logBuff, ((JSONObject) eventMeta).get(TYPE).toString().toUpperCase());
        } else {
            appendParam(logBuff, null);
        }

        //Event Action
        if (event != null && event.getType() != null) {
            appendParam(logBuff, event.getType().toString().toUpperCase());
        } else {
            appendParam(logBuff, null);
        }

        //Event Action param
        String actionParam = checkAndGetActionParam(event);
        String modifiedActionParam = replaceComma(actionParam);
        appendParam(logBuff, modifiedActionParam);

        //App OR Wap OR Basic parameter
        if(WynkAppType.isWynkBasicApp()) {
            appendParam(logBuff, WynkAppType.BASIC_APP);
        } else  {
            String app_wap_param = event.getAppType();
            appendParam(logBuff, MusicUtils.getAppType());
        }

        //Server Timestamp
        appendParam(logBuff, Long.toString(new Date().getTime()));

        //Music Preference Content Language Info
        String contentLang = "-";
        appendParam(logBuff, contentLang);

        // DeviceId
        if (device != null)
            appendParam(logBuff, device.getDeviceId());
        else
            appendParam(logBuff, "-");

        // Prepaid / Postpaid
        appendParam(logBuff, "-");

        return logBuff.toString();
    }

    private String checkAndGetXRatSnetAndNet(UserActivityEvent event, HttpRequest request) {
        String xRatString = accountService.getXRAT(request);
        xRatString += "|" + event.getSnet();
        xRatString += "|" + event.getNet();
        xRatString += "|" + event.getNetq();
        return xRatString;
    }

    private String addItemsInActionParam(String actionParam, JSONArray items) {
        for (int i = 0; i < items.size(); i++) {
            String item = items.get(i).toString();
            actionParam = appendActionParam(actionParam, item);
        }
        return actionParam;
    }

    // For appending action_param based on event type - code refactored
    private String checkAndGetActionParam(UserActivityEvent event) {
        String actionParam = null;

        if (event != null && event.getType() != null) {
            JSONObject eventMeta = event.getMeta();
            if (eventMeta != null) {
                if (UserEventType.ITEM_SHARED.getId().equals(event.getType().getId()) && (eventMeta.get(CHANNEL) != null)) {
                    actionParam = CHANNEL + "=" + ((JSONObject) eventMeta).get(CHANNEL);
                } else if (UserEventType.APP_EXT_INSTALL.getId().equals(event.getType().getId()) && (eventMeta.get(CHANNEL) != null)) {
                    actionParam = CHANNEL + "=" + ((JSONObject) eventMeta).get(CHANNEL);
                } else if (UserEventType.PAGE_LOADED.getId().equals(event.getType().getId())) {
                    if (null != eventMeta.get("duration")) {
                        actionParam = "duration=" + eventMeta.get("duration");
                    }
                    if (org.apache.commons.lang.StringUtils.isNotEmpty((String) eventMeta.get("url"))) {
                        actionParam = appendActionParam(actionParam, "url=" + eventMeta.get("url"));
                    }
                } else if (UserEventType.USER_PLAYLIST_CREATED.getId().equals(event.getType().getId()) || UserEventType.FOLDER_PLAYLIST_CREATED.getId().equals(event.getType().getId())) {
                    if (null != eventMeta.get("autoCreated")) {
                        actionParam = appendActionParam(actionParam, "autoCreated=" + (Boolean) eventMeta.get("autoCreated"));
                    }

                    if (null != eventMeta.get("items") && eventMeta.get("items") instanceof JSONArray) {
                        actionParam = addItemsInActionParam(actionParam, (JSONArray) eventMeta.get("items"));
                    }
                    if (null != eventMeta.get("title")) {
                        actionParam = appendActionParam(actionParam, "title=" + (String) eventMeta.get("title"));
                    }
                } else if (UserEventType.USER_PLAYLIST_ADD_SONGS.getId().equals(event.getType().getId()) || UserEventType.USER_PLAYLIST_REMOVE_SONGS.getId().equals(event.getType().getId())) {

                    if (null != eventMeta.get("items") && eventMeta.get("items") instanceof JSONArray) {
                        actionParam = addItemsInActionParam(actionParam, (JSONArray) eventMeta.get("items"));
                    }
                    if (null != event.getMeta().get(ONDEVICE)) {
                        actionParam = appendActionParam(actionParam, ONDEVICE + "=" + event.getMeta().get(ONDEVICE));
                    }
                } else if (UserEventType.USER_PLAYLIST_UPDATED.getId().equals(event.getType().getId())) {
                    if (null != eventMeta.get("title")) {
                        actionParam = appendActionParam(actionParam, "title=" + (String) eventMeta.get("title"));
                    }
                } else if (UserEventType.SONG_COMPLETED.getId().equals(event.getType().getId())) {
                    if (null != eventMeta.get("offline")) {
                        actionParam = appendActionParam(actionParam, "offline=" + (Boolean) eventMeta.get("offline"));
                    }
                    if (null != event.getMeta().get(PURCHASED)) {
                        actionParam = appendActionParam(actionParam, "purchased=" + event.getMeta().get(PURCHASED));
                    }
                    if (null != event.getMeta().get(BUFFERED)) {
                        actionParam = appendActionParam(actionParam, BUFFERED + "=" + event.getMeta().get(BUFFERED));
                    }
                    if (null != event.getMeta().get(ONDEVICE)) {
                        actionParam = appendActionParam(actionParam, ONDEVICE + "=" + event.getMeta().get(ONDEVICE));
                    }
                    if (null != event.getMeta().get(OUTSIDEPLAY)) {
                        actionParam = appendActionParam(actionParam, OUTSIDEPLAY + "=" + event.getMeta().get(OUTSIDEPLAY));
                    }
                } else if (UserEventType.PAGE_FAILED.getId().equals(event.getType().getId())) {
                    String msisdnStr = accountService.getMSISDN();

                    if (null != eventMeta.get("duration")) {
                        actionParam = "duration=" + eventMeta.get("duration");
                    }
                    if (null != eventMeta.get("error_code")) {
                        actionParam = appendActionParam(actionParam, "error_code=" + eventMeta.get("error_code"));
                    }
                    if (StringUtils.isNotEmpty(msisdnStr)) {
                        String encryptMSISDN;
                        try {
                            encryptMSISDN = MusicUtils.encryptMSISDN(msisdnStr);
                        } catch (Exception e) {
                            logger.warn("Encryption Failed. " + e.getMessage() + "Returning to normal mode.");
                            encryptMSISDN = msisdnStr;
                        }
                        actionParam = appendActionParam(actionParam, "msisdn=" + encryptMSISDN);
                    }
                    if (StringUtils.isNotEmpty((String) eventMeta.get("url"))) {
                        actionParam = appendActionParam(actionParam, "url=" + eventMeta.get("url"));
                    }
                } else if ((UserEventType.SONG_PLAYED.getId().equals(event.getType().getId())) || (UserEventType.SONG_PLAYED_LONG.getId().equals(event.getType().getId()))) {
                    actionParam = "radio=false";
                    if (null != event.getMeta().get(RADIO)) {
                        actionParam = "radio=" + event.getMeta().get(RADIO);
                    }
                    if (null != event.getMeta().get(OFFLINE)) {
                        actionParam = appendActionParam(actionParam, "offline=" + event.getMeta().get(OFFLINE));
                    }
                    if (null != event.getMeta().get(DURATION)) {
                        actionParam = appendActionParam(actionParam, "duration=" + event.getMeta().get(DURATION));
                    }
                    if (null != eventMeta.get("cast")) {
                        actionParam = appendActionParam(actionParam, "chromecast=" + eventMeta.get("cast"));
                    }
                    if (null != eventMeta.get("sq")) {
                        actionParam = appendActionParam(actionParam, "sq=" + eventMeta.get("sq"));
                    }
                    if (null != event.getMeta().get(DATA_SAVE)) {
                        actionParam = actionParam + "|ds=" + event.getMeta().get(DATA_SAVE);
                    }
                    if (null != event.getMeta().get(PURCHASED)) {
                        actionParam = appendActionParam(actionParam, "purchased=" + event.getMeta().get(PURCHASED));
                    }
                    if (null != event.getMeta().get(BUFFERED)) {
                        actionParam = appendActionParam(actionParam, BUFFERED + "=" + event.getMeta().get(BUFFERED));
                    }
                    if (null != event.getMeta().get(ONDEVICE)) {
                        actionParam = appendActionParam(actionParam, ONDEVICE + "=" + event.getMeta().get(ONDEVICE));
                    }
                    if (null != event.getMeta().get(OUTSIDEPLAY)) {
                        actionParam = appendActionParam(actionParam, OUTSIDEPLAY + "=" + event.getMeta().get(OUTSIDEPLAY));
                    }
                    if (null != eventMeta.get(AUTO_PLAYED)) {
                        actionParam = appendActionParam(actionParam, AUTO_PLAYED + "=" + eventMeta.get(AUTO_PLAYED));
                    }
                } else if (UserEventType.CLICK.getId().equals(event.getType().getId())) {
                    if (null != eventMeta.get(MODULE_ID)) {
                        actionParam = "module_id=" + eventMeta.get(MODULE_ID);
                    }
                    if (null != eventMeta.get(THREEDTOUCH)) {
                        actionParam = appendActionParam(actionParam, THREEDTOUCH + "=" + event.getMeta().get(THREEDTOUCH));
                    }
                    if (null != eventMeta.get(ACTION)) {
                        actionParam = appendActionParam(actionParam, "action=" + event.getMeta().get(ACTION));
                    }
                    if (null != eventMeta.get(ITEM_RANK)) {
                        actionParam = appendActionParam(actionParam, ITEM_RANK + "=" + eventMeta.get(ITEM_RANK));
                    }
                    if (null != eventMeta.get(PLAYER_STATUS)) {
                        actionParam = appendActionParam(actionParam, PLAYER_STATUS + "=" + eventMeta.get(PLAYER_STATUS));
                    }
                    if (null != eventMeta.get(PLAYER_SONG_ID)) {
                        actionParam = appendActionParam(actionParam, PLAYER_SONG_ID + "=" + eventMeta.get(PLAYER_SONG_ID));
                    }
                    if (null != eventMeta.get(REPEAT_STATUS)) {
                        actionParam = appendActionParam(actionParam, REPEAT_STATUS + "=" + eventMeta.get(REPEAT_STATUS));
                    }
                    if (null != eventMeta.get(SHUFFLE_STATUS)) {
                        actionParam = appendActionParam(actionParam, SHUFFLE_STATUS + "=" + eventMeta.get(SHUFFLE_STATUS));
                    }
                    if (null != eventMeta.get(PLAYED_DURATION)) {
                        actionParam = appendActionParam(actionParam, PLAYED_DURATION + "=" + eventMeta.get(PLAYED_DURATION));
                    }
                    if (null != eventMeta.get(KEYWORD)) {
                        actionParam = appendActionParam(actionParam, KEYWORD + "=" + eventMeta.get(KEYWORD));
                    }
                    if (null != eventMeta.get(RESULTS_ID)) {
                        actionParam = appendActionParam(actionParam, RESULTS_ID + "=" + eventMeta.get(RESULTS_ID));
                    }
                    if (null != eventMeta.get(NUMBER_MODIFIED)) {
                        actionParam = appendActionParam(actionParam, NUMBER_MODIFIED + "=" + eventMeta.get(NUMBER_MODIFIED));
                    }
                    if (null != eventMeta.get(ROW)) {
                        actionParam = appendActionParam(actionParam, ROW + "=" + eventMeta.get(ROW));
                    }
                    if (null != eventMeta.get(COLUMN)) {
                        actionParam = appendActionParam(actionParam, COLUMN + "=" + eventMeta.get(COLUMN));
                    }
                    if (null != eventMeta.get(SONQ_QUALITY)) {
                        actionParam = appendActionParam(actionParam, SONQ_QUALITY + "=" + eventMeta.get(SONQ_QUALITY));
                    }
                    if (null != eventMeta.get(VALUE)) {
                        actionParam = appendActionParam(actionParam, VALUE + "=" + eventMeta.get(VALUE));
                    }
                    if (null != eventMeta.get(QUEUED)) {
                        actionParam = appendActionParam(actionParam, QUEUED + "=" + eventMeta.get(QUEUED));
                    }
                    if (null != event.getMeta().get(ONDEVICE)) {
                        actionParam = appendActionParam(actionParam, ONDEVICE + "=" + event.getMeta().get(ONDEVICE));
                    }
                    if (null != eventMeta.get(ID)) {
                        actionParam = appendActionParam(actionParam, ID + "=" + eventMeta.get(ID));
                    }
                    if (null != eventMeta.get(OBJECT_ID)) {
                        actionParam = appendActionParam(actionParam, OBJECT_ID + "=" + eventMeta.get(OBJECT_ID));
                    }
                    if (null != eventMeta.get(SCREEN_ID)) {
                        actionParam = appendActionParam(actionParam, SCREEN_ID + "=" + eventMeta.get(SCREEN_ID));
                    }
                    if (null != eventMeta.get(OVERFLOW_ACTION)) {
                        actionParam = appendActionParam(actionParam, OVERFLOW_ACTION + "=" + eventMeta.get(OVERFLOW_ACTION));
                    }
                    if (null != eventMeta.get(OVERFLOW_TYPE)) {
                        actionParam = appendActionParam(actionParam, OVERFLOW_TYPE + "=" + eventMeta.get(OVERFLOW_TYPE));
                    }
                    if (null != eventMeta.get(SWIPE_TYPE)) {
                        actionParam = appendActionParam(actionParam, SWIPE_TYPE + "=" + eventMeta.get(SWIPE_TYPE));
                    }
                } else if (UserEventType.APP_INSTALL.getId().equals(event.getType().getId())) {
                    if (null != eventMeta.get(REFERRER)) {
                        actionParam = "referrer=" + eventMeta.get(REFERRER);
                    }
                } else if ((UserEventType.ITEM_RENTING_STARTED.getId().equals(event.getType().getId())) || (UserEventType.ITEM_DOWNLOAD_STARTED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(QUEUED)) {
                        actionParam = "queued=" + eventMeta.get(QUEUED);
                    }
                } else if (UserEventType.SOS.getId().equals(event.getType().getId())) {
                    if (null != eventMeta.get(URL)) {
                        actionParam = "url=" + eventMeta.get(URL);
                    }
                    if (null != eventMeta.get(STATUS)) {
                        actionParam = appendActionParam(actionParam, "status=" + eventMeta.get(STATUS));
                    }

                } else if ((UserEventType.ITEM_QUEUED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get("cast")) {
                        actionParam = "chromecast=" + eventMeta.get("cast");
                    }
                    if (null != eventMeta.get(AUTO_PLAYED)) {
                        actionParam = appendActionParam(actionParam, AUTO_PLAYED + "=" + eventMeta.get(AUTO_PLAYED));
                    }
                    if (null != eventMeta.get(MODULE_ID)) {
                        actionParam = appendActionParam(actionParam, "module_id=" + eventMeta.get(MODULE_ID));
                    }
                    if (null != eventMeta.get(SOURCE)) {
                        actionParam = appendActionParam(actionParam, SOURCE + "=" + eventMeta.get(SOURCE));
                    }
                } else if ((UserEventType.ADHM_PLAYLIST_CREATED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(SOURCE)) {
                        actionParam = SOURCE + "=" + eventMeta.get(SOURCE);
                    }
                } else if ((UserEventType.ITEM_RENTED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(REQUESTED)) {
                        actionParam = REQUESTED + "=" + eventMeta.get(REQUESTED);
                    }
                    if (null != eventMeta.get(CACHE)) {
                        actionParam = appendActionParam(actionParam, CACHE + "=" + eventMeta.get(CACHE));
                    }
                    if (null != eventMeta.get(AUTO_RECOVERED)) {
                        actionParam = appendActionParam(actionParam, AUTO_RECOVERED + "=" + eventMeta.get(AUTO_RECOVERED));
                    }
                } else if ((UserEventType.DATA_SAVE_TOGGLE.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(AUTO)) {
                        actionParam = AUTO + "=" + eventMeta.get(AUTO);
                    }
                    if (null != eventMeta.get(VALUE)) {
                        actionParam += "|" + VALUE + "=" + eventMeta.get(VALUE);
                    }
                } else if ((UserEventType.SONG_ENDED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(REQUESTED)) {
                        actionParam = REQUESTED + "=" + eventMeta.get(REQUESTED);
                    }
                    if (null != eventMeta.get(CACHE)) {
                        actionParam = appendActionParam(actionParam, CACHE + "=" + eventMeta.get(CACHE));
                    }
                    if (null != event.getMeta().get(OFFLINE)) {
                        actionParam = appendActionParam(actionParam, OFFLINE + "=" + event.getMeta().get(OFFLINE));
                    }
                    if (null != event.getMeta().get(PURCHASED)) {
                        actionParam = appendActionParam(actionParam, "purchased=" + event.getMeta().get(PURCHASED));
                    }
                    if (null != event.getMeta().get(BUFFERED)) {
                        actionParam = appendActionParam(actionParam, BUFFERED + "=" + event.getMeta().get(BUFFERED));
                    }
                    if (null != event.getMeta().get(ONDEVICE)) {
                        actionParam = appendActionParam(actionParam, ONDEVICE + "=" + event.getMeta().get(ONDEVICE));
                    }
                    if (null != event.getMeta().get(OUTSIDEPLAY)) {
                        actionParam = appendActionParam(actionParam, OUTSIDEPLAY + "=" + event.getMeta().get(OUTSIDEPLAY));
                    }
                } else if ((UserEventType.APP_STARTED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(PRE_INSTALL_OEM)) {
                        actionParam = PRE_INSTALL_OEM + "=" + eventMeta.get(PRE_INSTALL_OEM);
                    }
                    if (null != eventMeta.get(ARCH_TYPE)) {
                        actionParam = appendActionParam(actionParam, ARCH_TYPE + "=" + eventMeta.get(ARCH_TYPE));
                    }

                    if (null != eventMeta.get(INSTALLED_PACKAGES)) {
                        String installedPackages = (String) eventMeta.get(INSTALLED_PACKAGES);
                        StringBuilder sb = new StringBuilder();
                        try {
                            JSONArray installedPackagesArray = (JSONArray) JSONValue.parseWithException(installedPackages);
                            for (int j = 0; j < installedPackagesArray.size(); j++) {
                                String installedPackage = (String) installedPackagesArray.get(j);
                                sb.append(installedPackage).append("/");
                            }
                        } catch (ParseException e) {
                            logger.error("Error parsing installedPackages JSON " + e.getMessage(), e);
                        }
                        if (sb.nonEmpty()) {
                            actionParam = appendActionParam(actionParam, INSTALLED_PACKAGES + "=" + sb.toString());
                        }
                    }
                    if (null != eventMeta.get(EMAILS) && MusicUtils.isValidEmail((String) event.getMeta().get(EMAILS))) {
                        String emails = (String) eventMeta.get(EMAILS);
                        if (StringUtils.isNotBlank(emails)) {
                            emails = emails.replace(",", "/");
                            actionParam = appendActionParam(actionParam, EMAILS + "=" + emails);
                        }
                    }
                    if (null != eventMeta.get(DEVICE_DATA)) {
                        JSONObject deviceData = (JSONObject) eventMeta.get(DEVICE_DATA);
                        JSONArray simInfoArray = (JSONArray) deviceData.get(SIMINFO);
                        if (simInfoArray != null) {
                            for (int i = 0; i < simInfoArray.size(); i++) {
                                actionParam = appendActionParam(actionParam, SIMID + "=" + (i + 1));
                                JSONObject simInfoObject = (JSONObject) simInfoArray.get(i);
                                if (null != simInfoObject.get(MCC)) {
                                    actionParam = appendActionParam(actionParam, MCC + "=" + simInfoObject.get(MCC));
                                }
                                if (null != simInfoObject.get(CARRIER)) {
                                    actionParam = appendActionParam(actionParam, CARRIER + "=" + simInfoObject.get(CARRIER));
                                }
                                if (null != simInfoObject.get(ROAMING)) {
                                    actionParam = appendActionParam(actionParam, ROAMING + "=" + simInfoObject.get(ROAMING));
                                }
                                if (null != simInfoObject.get(NETWORK)) {
                                    actionParam = appendActionParam(actionParam, NETWORK + "=" + simInfoObject.get(NETWORK));
                                }
                            }
                        }

                    }
                } else if ((UserEventType.FILE_DELETED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(FILE_SIZE)) {
                        actionParam = FILE_SIZE + "=" + eventMeta.get(FILE_SIZE);
                    }
                    if (null != eventMeta.get(REASON)) {
                        actionParam = appendActionParam(actionParam, REASON + "=" + eventMeta.get(REASON));
                    }
                } else if ((UserEventType.REGISTRATION_FAILED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(STATUS)) {
                        actionParam = STATUS + "=" + eventMeta.get(STATUS);
                    }
                } else if ((UserEventType.ITEM_DELETED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(ONDEVICE)) {
                        actionParam = ONDEVICE + "=" + eventMeta.get(ONDEVICE);
                    }
                } else if ((UserEventType.SCREEN_CLOSED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(SOURCE)) {
                        actionParam = SOURCE + "=" + eventMeta.get(SOURCE);
                    }
                    if (null != eventMeta.get(ID)) {
                        actionParam = appendActionParam(actionParam, ID + "=" + eventMeta.get(ID));
                    }
                    if (null != eventMeta.get(SCREEN_ID)) {
                        actionParam = appendActionParam(actionParam, SCREEN_ID + "=" + eventMeta.get(SCREEN_ID));
                    }
                } else if ((UserEventType.SCREEN_OPENED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(SOURCE)) {
                        actionParam = SOURCE + "=" + eventMeta.get(SOURCE);
                    }
                    if (null != eventMeta.get(ID)) {
                        actionParam = appendActionParam(actionParam, ID + "=" + eventMeta.get(ID));
                    }
                    if (null != eventMeta.get(SCREEN_ID)) {
                        actionParam = appendActionParam(actionParam, SCREEN_ID + "=" + eventMeta.get(SCREEN_ID));
                    }
                } else if ((UserEventType.GCM_REGISTRATION_SUCCESSFUL.getId().equals(event.getType().getId())) && null != eventMeta.get(DEVICE_KEY)) {
                    actionParam = DEVICE_KEY + "=" + eventMeta.get(DEVICE_KEY);
                } else if ((UserEventType.REGISTERED.getId().equals(event.getType().getId())) && null != eventMeta.get(JsonKeyNames.MSISDN)) {
                    actionParam = JsonKeyNames.MSISDN + "=" + eventMeta.get(JsonKeyNames.MSISDN);
                } else if (UserEventType.USER_SUBSCRIPTION_TYPE.getId().equals(event.getType().getId())) {

                    for (Object jsonData : (JSONArray)eventMeta.get(JsonKeyNames.PRODUCTS_META)) {
                        if (null != ((JSONObject)jsonData).get(SUBSCRIPTION_STATE))
                            actionParam = SUBSCRIPTION_STATE + "=" + ((JSONObject)jsonData).get(SUBSCRIPTION_STATE);

                        if (null != ((JSONObject)jsonData).get(PRODUCT_ID))
                            actionParam = appendActionParam(actionParam, PRODUCT_ID + "=" + ((JSONObject)jsonData).get(PRODUCT_ID));

                        if (null != ((JSONObject)jsonData).get(EXPIRE_TIMESTAMP))
                            actionParam = appendActionParam(actionParam, EXPIRE_TIMESTAMP + "=" + ((JSONObject)jsonData).get(EXPIRE_TIMESTAMP));

                        if (null != ((JSONObject)jsonData).get(IS_PREMIUM_ACCOUNT))
                            actionParam = appendActionParam(actionParam, IS_PREMIUM_ACCOUNT + "=" + ((JSONObject)jsonData).get(IS_PREMIUM_ACCOUNT));
                    }

                } else if (UserEventType.USER_DEVICE_REMOVED.getId().equals(event.getType().getId())) {

                    if (null != eventMeta.get(DEVICEID))
                        actionParam = appendActionParam(actionParam, DEVICEID + "=" + eventMeta.get(DEVICEID));

                    if (null != eventMeta.get(EXISTING_UID))
                        actionParam = appendActionParam(actionParam, EXISTING_UID + "=" + eventMeta.get(EXISTING_UID));

                } else if (UserEventType.USER_DEVICE_ADD.getId().equals(event.getType().getId()) || UserEventType.USER_DEVICE_UPDATE.getId().equals(event.getType().getId())) {

                    if (null != eventMeta.get(DEVICEID))
                        actionParam = appendActionParam(actionParam, DEVICEID + "=" + eventMeta.get(DEVICEID));

                    if (null != eventMeta.get(OPERATOR))
                        actionParam = appendActionParam(actionParam, OPERATOR + "=" + eventMeta.get(OPERATOR));

                    if (null != eventMeta.get(JsonKeyNames.OS))
                        actionParam = appendActionParam(actionParam, JsonKeyNames.OS + "=" + eventMeta.get(JsonKeyNames.OS));

                    if (null != eventMeta.get(DEVICETYPE))
                        actionParam = appendActionParam(actionParam, DEVICETYPE + "=" + eventMeta.get(DEVICETYPE));

                    if (null != eventMeta.get(DEVICEKEY))
                        actionParam = appendActionParam(actionParam, DEVICEKEY + "=" + eventMeta.get(DEVICEKEY));

                    if (null != eventMeta.get(DEVICE_RESOLUTION))
                        actionParam = appendActionParam(actionParam, DEVICE_RESOLUTION + "=" + eventMeta.get(DEVICE_RESOLUTION));

                    if (null != eventMeta.get(DEVICE_REGIS_DATE))
                        actionParam = appendActionParam(actionParam, DEVICE_REGIS_DATE + "=" + eventMeta.get(DEVICE_REGIS_DATE));

                    if (null != eventMeta.get(DEVICE_IMEI))
                        actionParam = appendActionParam(actionParam, DEVICE_IMEI + "=" + eventMeta.get(DEVICE_IMEI));

                    if (null != eventMeta.get(DEVICE_AD_ID))
                        actionParam = appendActionParam(actionParam, DEVICE_AD_ID + "=" + eventMeta.get(DEVICE_AD_ID));

                    if (null != eventMeta.get(DEVICE_IS_ACTIVE))
                        actionParam = appendActionParam(actionParam, DEVICE_IS_ACTIVE + "=" + eventMeta.get(DEVICE_IS_ACTIVE));

                    if (null != eventMeta.get(DEVICE_APP_VER))
                        actionParam = appendActionParam(actionParam, DEVICE_APP_VER + "=" + eventMeta.get(DEVICE_APP_VER));

                    if (null != eventMeta.get(DEVICE_BUILD_NOS))
                        actionParam = appendActionParam(actionParam, DEVICE_BUILD_NOS + "=" + eventMeta.get(DEVICE_BUILD_NOS));

                    if (null != eventMeta.get(DEVICE_LAST_UPDATE))
                        actionParam = appendActionParam(actionParam, DEVICE_LAST_UPDATE + "=" + eventMeta.get(DEVICE_LAST_UPDATE));

                    if (null != eventMeta.get(INTERNATIONAL_ROAMING))
                        actionParam = appendActionParam(actionParam, INTERNATIONAL_ROAMING + "=" + eventMeta.get(INTERNATIONAL_ROAMING));

                } else if (UserEventType.USER_PROFILE_UPDATE.getId().equals(event.getType().getId())) {

                    if (null != eventMeta.get(CIRCLE))
                        actionParam = CIRCLE + "=" + eventMeta.get(CIRCLE);

                    if (null != eventMeta.get(CONTENT_LANGUAGE))
                        actionParam = CONTENT_LANGUAGE + "=" + eventMeta.get(CONTENT_LANGUAGE);

                    if (null != eventMeta.get(NAME))
                        actionParam = NAME + "=" + eventMeta.get(NAME);

                    if (null != eventMeta.get(EMAIL))
                        actionParam = EMAIL + "=" + eventMeta.get(EMAIL);

                    if (null != eventMeta.get(DOB))
                        actionParam = DOB + "=" + eventMeta.get(DOB);

                    if (null != eventMeta.get(GENDER))
                        actionParam = GENDER + "=" + eventMeta.get(GENDER);

                    if (null != eventMeta.get(SONQ_QUAL))
                        actionParam = SONQ_QUAL + "=" + eventMeta.get(SONQ_QUAL);

                    if (null != eventMeta.get(OPERATOR))
                        actionParam = appendActionParam(actionParam, OPERATOR + "=" + eventMeta.get(OPERATOR));

                    if (null != eventMeta.get(DEVICEID))
                        actionParam = appendActionParam(actionParam, DEVICEID + "=" + eventMeta.get(DEVICEID));

                } else if ((UserEventType.GCM_REGISTRATION_FAILED.getId().equals(event.getType().getId())) && null != eventMeta.get(ERROR)) {
                    actionParam = ERROR + "=" + eventMeta.get(ERROR);
                } else if ((UserEventType.GCM_EXPIRED.getId().equals(event.getType().getId())) && null != eventMeta.get(REASON)) {
                    actionParam = REASON + "=" + eventMeta.get(REASON);
                } else if ((UserEventType.URBAN_AIRSHIP_CHANNEL_UPDATED.getId().equals(event.getType().getId())) && null != eventMeta.get(URBAN_AIRSHIP_CHANNEL)) {
                    actionParam = URBAN_AIRSHIP_CHANNEL + "=" + eventMeta.get(URBAN_AIRSHIP_CHANNEL);
                } else if ((UserEventType.AUTO_PLAY_BANNER_HIDDEN.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(REASON)) {
                        actionParam = REASON + "=" + eventMeta.get(REASON);
                    }
                } else if ((UserEventType.NOT_CONSUMED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(SUCCESS)) {
                        actionParam = SUCCESS + "=" + eventMeta.get(SUCCESS);
                    }
                    if (null != eventMeta.get(SEARCHED_STRING)) {
                        actionParam = appendActionParam(actionParam, SEARCHED_STRING + "=" + eventMeta.get(SEARCHED_STRING));
                    }
                } else if ((UserEventType.EXTERNAL_ID.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(LIMITED_AD_TRACKING)) {
                        actionParam = LIMITED_AD_TRACKING + "=" + eventMeta.get(LIMITED_AD_TRACKING);
                    }
                } else if ((UserEventType.APP_INFO.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(CARRIER)) {
                        actionParam = CARRIER + "=" + eventMeta.get(CARRIER);
                    }
                    if (null != eventMeta.get(CURRENT_QUEUE_SIZE)) {
                        actionParam = appendActionParam(actionParam, CURRENT_QUEUE_SIZE + "=" + eventMeta.get(CURRENT_QUEUE_SIZE));
                    }
                    if (null != eventMeta.get(STORAGE)) {
                        JSONArray storageArray = (JSONArray) eventMeta.get(STORAGE);
                        if (storageArray != null) {
                            for (int i = 0; i < storageArray.size(); i++) {
                                JSONObject storageObject = (JSONObject) storageArray.get(i);
                                if (null != storageObject.get(TYPE)) {
                                    actionParam += "|" + TYPE + "=" + storageObject.get(TYPE);
                                }
                                if (null != storageObject.get(TOTAL)) {
                                    actionParam += "|" + TOTAL + "=" + storageObject.get(TOTAL);
                                }
                                if (null != storageObject.get(FREE)) {
                                    actionParam += "|" + FREE + "=" + storageObject.get(FREE);
                                }
                            }
                        }
                    }
                } else if ((UserEventType.REQUEST_TIME.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(URL)) {
                        actionParam = URL + "=" + eventMeta.get(URL);
                    }
                    if (null != eventMeta.get(DURATION)) {
                        actionParam = appendActionParam(actionParam, DURATION + "=" + eventMeta.get(DURATION));
                    }
                    if (null != eventMeta.get(STREAM)) {
                        actionParam = appendActionParam(actionParam, STREAM + "=" + eventMeta.get(STREAM));
                    }
                } else if ((UserEventType.UNSUBSCRIBED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(PRODUCT_ID)) {
                        actionParam = PRODUCT_ID + "=" + eventMeta.get(PRODUCT_ID);
                    }
                } else if ((UserEventType.ONDEVICE_SONG_MATCHED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(ONLINEID)) {
                        actionParam = ONLINEID + "=" + eventMeta.get(ONLINEID);
                    }
                    if (null != event.getMeta().get(TYPE)) {
                        actionParam = appendActionParam(actionParam, TYPE + "=" + event.getMeta().get(TYPE));
                    }
                    if (null != eventMeta.get(OFFLINE_TITLE)) {
                        actionParam = appendActionParam(actionParam, OFFLINE_TITLE + "=" + eventMeta.get(OFFLINE_TITLE));
                    }
                    if (null != event.getMeta().get(OFFLINE_ALBUM)) {
                        actionParam = appendActionParam(actionParam, OFFLINE_ALBUM + "=" + event.getMeta().get(OFFLINE_ALBUM));
                    }
                    if (null != event.getMeta().get(ONLINE_TITLE)) {
                        actionParam = appendActionParam(actionParam, ONLINE_TITLE + "=" + event.getMeta().get(ONLINE_TITLE));
                    }
                    if (null != event.getMeta().get(ONLINE_ALBUM)) {
                        actionParam = appendActionParam(actionParam, ONLINE_ALBUM + "=" + event.getMeta().get(ONLINE_ALBUM));
                    }
                } else if ((UserEventType.ONDEVICE_SONG_NOT_MATCHED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(TYPE)) {
                        actionParam = TYPE + "=" + eventMeta.get(TYPE);
                    }
                    if (null != eventMeta.get(ONDEVICEID)) {
                        actionParam = appendActionParam(actionParam, ONDEVICEID + "=" + eventMeta.get(ONDEVICEID));
                    }
                    if (null != eventMeta.get(OFFLINE_TITLE)) {
                        actionParam = appendActionParam(actionParam, OFFLINE_TITLE + "=" + eventMeta.get(OFFLINE_TITLE));
                    }
                    if (null != event.getMeta().get(OFFLINE_ALBUM)) {
                        actionParam = appendActionParam(actionParam, OFFLINE_ALBUM + "=" + event.getMeta().get(OFFLINE_ALBUM));
                    }
                    if (null != event.getMeta().get(DURATION)) {
                        actionParam = appendActionParam(actionParam, DURATION + "=" + event.getMeta().get(DURATION));
                    }
                } else if ((UserEventType.SONGS_MAPPING.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(META_MATCHED)) {
                        actionParam = META_MATCHED + "=" + eventMeta.get(META_MATCHED);
                    }
                    if (null != event.getMeta().get(FINGERPRINT_MATCHED)) {
                        actionParam = appendActionParam(actionParam, FINGERPRINT_MATCHED + "=" + event.getMeta().get(FINGERPRINT_MATCHED));
                    }
                    if (null != eventMeta.get(UN_MATCHED)) {
                        actionParam = appendActionParam(actionParam, UN_MATCHED + "=" + eventMeta.get(UN_MATCHED));
                    }
                } else if ((UserEventType.FILE_EXP_PLAYBACK_FAILED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(ID)) {
                        actionParam = ID + "=" + eventMeta.get(ID);
                    }
                    if (null != event.getMeta().get(TYPE)) {
                        actionParam = appendActionParam(actionParam, TYPE + "=" + event.getMeta().get(TYPE));
                    }
                    if (null != eventMeta.get(ONDEVICE)) {
                        actionParam = appendActionParam(actionParam, ONDEVICE + "=" + eventMeta.get(ONDEVICE));
                    }
                    if (null != event.getMeta().get(OUTSIDEPLAY)) {
                        actionParam = appendActionParam(actionParam, OUTSIDEPLAY + "=" + event.getMeta().get(OUTSIDEPLAY));
                    }
                } else if ((UserEventType.ON_DEVICE_SONG_SCAN.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(SONGS_ADDED)) {
                        actionParam = SONGS_ADDED + "=" + eventMeta.get(SONGS_ADDED);
                    }
                    if (null != event.getMeta().get(SONGS_DELETED)) {
                        actionParam = appendActionParam(actionParam, SONGS_DELETED + "=" + event.getMeta().get(SONGS_DELETED));
                    }
                    if (null != event.getMeta().get(TIME_TAKEN)) {
                        actionParam = appendActionParam(actionParam, TIME_TAKEN + "=" + event.getMeta().get(TIME_TAKEN));
                    }
                } else if ((UserEventType.ONDEVICE_SONG_WRONGLY_MATCHED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(TYPE)) {
                        actionParam = TYPE + "=" + eventMeta.get(TYPE);
                    }
                    if (null != event.getMeta().get(OFFLINEID)) {
                        actionParam = appendActionParam(actionParam, OFFLINEID + "=" + event.getMeta().get(OFFLINEID));
                    }
                    if (null != eventMeta.get(OFFLINE_TITLE)) {
                        actionParam = appendActionParam(actionParam, OFFLINE_TITLE + "=" + eventMeta.get(OFFLINE_TITLE));
                    }
                    if (null != event.getMeta().get(OFFLINE_ALBUM)) {
                        actionParam = appendActionParam(actionParam, OFFLINE_ALBUM + "=" + event.getMeta().get(OFFLINE_ALBUM));
                    }
                    if (null != event.getMeta().get(ONLINEID)) {
                        actionParam = appendActionParam(actionParam, ONLINEID + "=" + event.getMeta().get(ONLINEID));
                    }
                    if (null != event.getMeta().get(ONLINE_TITLE)) {
                        actionParam = appendActionParam(actionParam, ONLINE_TITLE + "=" + event.getMeta().get(ONLINE_TITLE));
                    }
                    if (null != event.getMeta().get(ONLINE_ALBUM)) {
                        actionParam = appendActionParam(actionParam, ONLINE_ALBUM + "=" + event.getMeta().get(ONLINE_ALBUM));
                    }
                } else if ((UserEventType.SPOTLIGHT_INDEXING_FAILED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(ERROR_CODE)) {
                        actionParam = ERROR_CODE + "=" + eventMeta.get(ERROR_CODE);
                    }
                    if (null != eventMeta.get(ERROR_DESCRIPTION)) {
                        actionParam = appendActionParam(actionParam, ERROR_DESCRIPTION + "=" + eventMeta.get(ERROR_DESCRIPTION));
                    }
                } else if ((UserEventType.MUSIC_PREFERENCE_CHANGED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(CONTENT_LANGUAGE)) {
                        actionParam = CONTENT_LANGUAGE + "=" + eventMeta.get(CONTENT_LANGUAGE);
                    }
                } else if ((UserEventType.FFMPEG_DOWNLOADED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(FFMPEG_DOWNLOADED_STATUS)) {
                        actionParam = FFMPEG_DOWNLOADED_STATUS + "=" + eventMeta.get(FFMPEG_DOWNLOADED_STATUS);
                    }
                    if (null != eventMeta.get(FFMPEG_VERSION)) {
                        actionParam = appendActionParam(actionParam, FFMPEG_VERSION + "=" + eventMeta.get(FFMPEG_VERSION));
                    }
                } else if ((UserEventType.META_BATCH_PROCESSING.getId().equals(event.getType().getId())) || (UserEventType.FINGERPRINT_BATCH_PROCESSING.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(STATUS)) {
                        actionParam = STATUS + "=" + eventMeta.get(STATUS);
                    }
                } else if (UserEventType.OTHER_INSTALLED_APPS.getId().equals(event.getType().getId())) {
                    if (null != eventMeta.get(JsonKeyNames.INSTALLED_PACKAGES)) {
                        String installedPackages = (String) eventMeta.get(JsonKeyNames.INSTALLED_PACKAGES);
                        StringBuilder sb = new StringBuilder();
                        JSONArray installedPackagesArray;
                        try {
                            installedPackagesArray = (JSONArray) JSONValue.parseWithException(installedPackages);
                            for (int j = 0; j < installedPackagesArray.size(); j++) {
                                String installedPackage = (String) installedPackagesArray.get(j);
                                sb.append(installedPackage).append("/");
                            }
                        } catch (ParseException e) {
                            logger.error("Error parsing other installed packages - Error : ", e.getMessage(), e);
                        }
                        if (sb.length() > 0) {
                            actionParam = appendActionParam(actionParam, JsonKeyNames.INSTALLED_PACKAGES + "=" + sb.toString());
                        }
                    }
                } else if ((UserEventType.DEV_STATS.getId().equals(event.getType().getId()))) {
                    int i = 0;
                    for (Object key : eventMeta.keySet()) {
                        String keyStr = (String) key;
                        if (i == 0) {
                            actionParam = keyStr + "=" + eventMeta.get(keyStr);
                            i++;
                        } else
                            actionParam = appendActionParam(actionParam, keyStr + "=" + eventMeta.get(keyStr));
                    }
                } else if ((UserEventType.ITEM_SEARCH.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(RESULTS_SHOWN)) {
                        actionParam = RESULTS_SHOWN + "=" + eventMeta.get(RESULTS_SHOWN);
                    }
                } else if ((UserEventType.PS_CLICK_CONTINUE.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get("build_number")) {
                        actionParam = "build_number" + "=" + eventMeta.get("build_number");
                    }
                    if (null != eventMeta.get("Permission")) {
                        actionParam = appendActionParam(actionParam, "Permission" + "=" + eventMeta.get("Permission"));
                    }
                } else if ((UserEventType.PERMISSION_DENIED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get("build_number")) {
                        actionParam = "build_number" + "=" + eventMeta.get("build_number");
                    }
                } else if ((UserEventType.STREAM_BLOCKED.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(SOURCE_REQUEST)) {
                        actionParam = SOURCE_REQUEST + "=" + eventMeta.get(SOURCE_REQUEST);
                    }
                } else if ((UserEventType.INTL_ROAMING.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(SIMINFO)) {
                        JSONArray simInfoArray = (JSONArray) eventMeta.get(SIMINFO);
                        if (simInfoArray != null) {
                            for (int i = 0; i < simInfoArray.size(); i++) {
                                actionParam = appendActionParam(actionParam, NAME + "=" + (i + 1));
                                JSONObject simInfoObject = (JSONObject) simInfoArray.get(i);
                                if (null != simInfoObject.get(CARRIER)) {
                                    actionParam = appendActionParam(actionParam, CARRIER + "=" + simInfoObject.get(CARRIER));
                                }
                                if (null != simInfoObject.get(MCC)) {
                                    actionParam = appendActionParam(actionParam, MCC + "=" + simInfoObject.get(MCC));
                                }
                            }
                        }
                    }
                    if (null != eventMeta.get(SOURCE_REQUEST)) {
                        actionParam = appendActionParam(actionParam, SOURCE_REQUEST + "=" + eventMeta.get(SOURCE_REQUEST));
                    }
                    if (null != eventMeta.get(START_TIME)) {
                        actionParam = appendActionParam(actionParam, START_TIME + "=" + eventMeta.get(START_TIME));
                    }
                    if (null != eventMeta.get(END_TIME)) {
                        actionParam = appendActionParam(actionParam, END_TIME + "=" + eventMeta.get(END_TIME));
                    }
                    if (null != eventMeta.get(TYPE)) {
                        actionParam = appendActionParam(actionParam, TYPE + "=" + eventMeta.get(TYPE));
                    }
                } else if ((UserEventType.PS_OPEN.getId().equals(event.getType().getId())) || (UserEventType.PS_CONTINUE.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get("build_number")) {
                        actionParam = "build_number" + "=" + eventMeta.get("build_number");
                    }
                    if (null != eventMeta.get("Permission")) {
						actionParam = appendActionParam(actionParam, "Permission" + "=" + eventMeta.get("Permission"));
					}
                    if((UserEventType.PS_OPEN.getId().equals(event.getType().getId())) && null != eventMeta.get("permission_variant_id")){
                        actionParam = appendActionParam(actionParam, "permission_variant_id" + "=" + eventMeta.get("permission_variant_id"));
                    }
                }else if ((UserEventType.ALLOWED_MCC_STREAM.getId().equals(event.getType().getId()))) {
                    if (null != eventMeta.get(SIMINFO)) {
                        JSONArray simInfoArray = (JSONArray) eventMeta.get(SIMINFO);
                        if (simInfoArray != null) {
                            for (int i = 0; i < simInfoArray.size(); i++) {
                                actionParam = appendActionParam(actionParam, NAME + "=" + (i + 1));
                                JSONObject simInfoObject = (JSONObject) simInfoArray.get(i);
                                if (null != simInfoObject.get(CARRIER)) {
                                    actionParam = appendActionParam(actionParam, CARRIER + "=" + simInfoObject.get(CARRIER));
                                }
                                if (null != simInfoObject.get(MCC)) {
                                    actionParam = appendActionParam(actionParam, MCC + "=" + simInfoObject.get(MCC));
                                }
                            }
                        }
                    }
                    if (null != eventMeta.get(SOURCE_REQUEST)) {
                        actionParam = appendActionParam(actionParam, SOURCE_REQUEST + "=" + eventMeta.get(SOURCE_REQUEST));
                    }
                    if (null != eventMeta.get(INCOMING_FORWARDED_IPS)) {
                        actionParam = appendActionParam(actionParam, INCOMING_FORWARDED_IPS + "=" + eventMeta.get(INCOMING_FORWARDED_IPS));
                    }
                    if (null != eventMeta.get(TYPE)) {
                        actionParam = appendActionParam(actionParam, TYPE + "=" + eventMeta.get(TYPE));
                    }
                }
            }
        } else {
            logger.warn("Event with no type : " + event.toJsonObject().toJSONString());
        }

        return actionParam;
    }

    public void createGCMUninstallLog(User user, UserDevice device) {

        UserActivityEvent event = new UserActivityEvent();
        event.setType(UserEventType.APP_UNINSTALL_BE);
        event.setTimestamp(System.currentTimeMillis());
        event.setAppType("app");
        JSONObject meta = new JSONObject();
        meta.put(ID, device.getDeviceId());
        meta.put(TYPE, "deviceId");
        event.setMeta(meta);

        String eventLog = createEventLog(event, user, device);
        mactivityLogger.info(eventLog);
    }

    public void createIntlRoamingLog(HttpRequest httpRequest, String uid, UserDevice device, Long startDate, Long endDate, String type, JSONArray simInfo) {

        UserActivityEvent event = new UserActivityEvent();
        event.setType(UserEventType.INTL_ROAMING);
        event.setTimestamp(System.currentTimeMillis());
        event.setAppType("app");
        JSONObject meta = new JSONObject();
        meta.put(TYPE, type);
        meta.put(SIMINFO, simInfo);
        meta.put(START_TIME, startDate);
        meta.put(END_TIME, endDate);
        String requestUri = httpRequest.getUri();
        if (requestUri.contains("cscgw"))
            meta.put(SOURCE_REQUEST, Stream);
        else if (requestUri.contains("crcgw"))
            meta.put(SOURCE_REQUEST, Rent);
        else if (requestUri.contains("config"))
            meta.put(SOURCE_REQUEST, Config);
        else if (requestUri.contains("cdcurl"))
            meta.put(SOURCE_REQUEST, Purchase);
        event.setMeta(meta);

        String eventLog = createEventLog(event, uid, device, httpRequest);
        mactivityLogger.info(eventLog);
    }

    public void createAllowedMCCStreamLog(HttpRequest httpRequest, String uid, UserDevice device, JSONArray simInfo, String type, String forwardedIps) {

        HashMap<String, String> metaLog = new HashMap<>();
        metaLog.put("simInfo", Utils.getMCCs(simInfo));
        metaLog.put("forwardedIps", forwardedIps);

        if (null != device) {
            LogstashLoggerUtils.createStandardLog("STREAM_INFO", uid, null == device.getMsisdn() ? "" : device.getMsisdn(), null == device.getDeviceId() ? "" : device.getDeviceId(), metaLog);
        }

        UserActivityEvent event = new UserActivityEvent();
        event.setType(UserEventType.ALLOWED_MCC_STREAM);
        event.setTimestamp(System.currentTimeMillis());
        event.setAppType("app");
        JSONObject meta = new JSONObject();
        meta.put(TYPE, type);
        meta.put(SIMINFO, simInfo);
        meta.put(INCOMING_FORWARDED_IPS, forwardedIps);
        String requestUri = httpRequest.getUri();
        if (requestUri.contains("cscgw"))
            meta.put(SOURCE_REQUEST, Stream);
        else if (requestUri.contains("crcgw"))
            meta.put(SOURCE_REQUEST, Rent);
        else if (requestUri.contains("config"))
            meta.put(SOURCE_REQUEST, Config);
        else if (requestUri.contains("cdcurl"))
            meta.put(SOURCE_REQUEST, Purchase);
        event.setMeta(meta);

        String eventLog = createEventLog(event, uid, device, httpRequest);
        mactivityLogger.info(eventLog);
    }

    private String replaceComma(String str) {
        String resultStr = str;
        if (StringUtils.isNotEmpty(str) && str.contains(",")) {
            resultStr = str.replace(',', '_');
        }

        return resultStr;
    }

    private String appendActionParam(String actionParam, String param) {
        if (StringUtils.isNotBlank(actionParam))
            return actionParam + "|" + param;
        else
            return param;
    }

    private void appendParam(StringBuffer logBuff, String param) {
        String defParamVal = "-";

        if (StringUtils.isNotEmpty(param)) {
            defParamVal = param.replace(',', '_').replace("\n", "").replace("\r", "");                // Handling punctuation

        }

        logBuff.append(defParamVal);
        logBuff.append(",");
    }

    public boolean isAllowedCountry() {
        if (!musicConfig.isEnableGeoBlocking())
            return true;

        return MusicUtils.isAllowedCountry(geoDBService, ChannelContext.getRequest());
    }

    public void addAppInstallEventOnAccountCreation() {
        // Added APP install event from BE
        UserActivityEvent event = new UserActivityEvent();
        event.setType(UserEventType.APP_INSTALL_BE);
        event.setTimestamp(System.currentTimeMillis());
        if (ChannelContext.getUser() != null) {
            event.setLang(ChannelContext.getUser().getLang());
        }
        if (!MusicUtils.isDevEnv(ChannelContext.getRequest())) {
            if (event != null) //If Valid event is there, then only log it. In few cases event is null and NPE is there
            {
                String eventLog = createEventLog(event, ChannelContext.getRequest());
                mactivityLogger.info(eventLog);
            }
        }
    }

    public void fireIMEIReceivedEvent(User user, UserDevice device) {
        UserActivityEvent event = new UserActivityEvent();
        event.setType(UserEventType.IMEI_NUMBER);
        event.setTimestamp(System.currentTimeMillis());
        event.setAppType("app");
        JSONObject meta = new JSONObject();
        meta.put(ID, device.getImeiNumber());
        meta.put(TYPE, "imeiNumber");
        event.setMeta(meta);

        String eventLog = createEventLog(event, user, device);
        mactivityLogger.info(eventLog);
    }

    public void addMusicPreferenceChangedEvent(String oldContentLang) {
        // Registered event
        UserActivityEvent event = new UserActivityEvent();
        event.setType(UserEventType.MUSIC_PREFERENCE_CHANGED);
        event.setTimestamp(System.currentTimeMillis());
        if (ChannelContext.getUser() != null) {
            event.setLang(ChannelContext.getUser().getLang());
        }
        JSONObject meta = new JSONObject();
        meta.put(CONTENT_LANGUAGE, oldContentLang);
        event.setMeta(meta);
        if (!MusicUtils.isDevEnv(ChannelContext.getRequest())) {
            if (event != null) //If Valid event is there, then only log it. In few cases event is null and NPE is there
            {
                String eventLog = createEventLog(event, ChannelContext.getRequest());
                mactivityLogger.info(eventLog);
            }
        }
    }

    public JSONObject getGeoBlacklistedResponse() {
        JSONObject json = new JSONObject();
        json.put(JsonKeyNames.SUCCESS, false);
        json.put("code", MusicConstants.GEO_BLACKLISTED_CONTENT);
        User user = ChannelContext.getUser();
        String message = "Wynk Music is not accessible in your region";
        if (ChannelContext.getLang() != null) {
            if ("hi".equalsIgnoreCase(ChannelContext.getLang())) {
                message = "आपके क्षेत्र में Wynk म्यूज़िक सेवा उपलब्ध नहीं है.";
            }
        }
        json.put("line2", message);
        return json;
    }

    public Boolean isGeoRestrictionPassed() {
        if (!MusicUtils.isGeoBlockingDisabled(ChannelContext.getMsisdn()) && !isAllowedCountry()) {
            HttpRequest request = ChannelContext.getRequest();
            //LogstashLoggerUtils
            //      .createInfoLog("Blocking streaming for uid (" + ChannelContext.getUid() + ")." + " IP1:" + UserDeviceUtils.getClientIP(request) + ", IP2:" + request.headers().get("x-bsy-ip"));
            createEventForBlockedStream();

            return false;
        }
        return true;
    }

    private void createEventForBlockedStream() {
        HttpRequest request = ChannelContext.getRequest();
        UserActivityEvent activityEvent = new UserActivityEvent();
        activityEvent.setLang(ChannelContext.getLang());
        activityEvent.setId("" + System.currentTimeMillis());
        activityEvent.setTimestamp(System.currentTimeMillis());
        activityEvent.setType(UserEventType.STREAM_BLOCKED);
        JSONObject meta = new JSONObject();
        String requestUri = request.getUri();
        if (requestUri.contains("cscgw"))
            meta.put(SOURCE_REQUEST, Stream);
        else if (requestUri.contains("crcgw"))
            meta.put(SOURCE_REQUEST, Rent);
        else if (requestUri.contains("config"))
            meta.put(SOURCE_REQUEST, Config);
        activityEvent.setMeta(meta);
        String eventLog = createEventLog(activityEvent, ChannelContext.getUid(), ChannelContext.getUserDevice(), request);
        mactivityLogger.info(eventLog);
    }

    public JSONObject verifyPaymentReceipt(String requestPayload, PaymentCode paymentCode) {

        logger.info("received verify payment receipt for uid : {} payload : {}", ChannelContext.getUid(), requestPayload);
        boolean status = Boolean.TRUE;
        Double planId = null;
        String sid = null;
        String uid = ChannelContext.getUid();
        String msisdn = ChannelContext.getUser().getMsisdn();
        Integer buildNo = null;

        try {
            JSONObject requestBody = gson.fromJson(requestPayload, JSONObject.class);
            Map<String, String> deviceInfo = MusicDeviceUtils.parseMusicHeaderDID();
            try {
                buildNo = Integer.parseInt(deviceInfo.get(APP_BUILD_NO));
            } catch (NumberFormatException e) {
                logger.error("Error while parsing build number for UID : {}", uid);
            }
            String deviceId = deviceInfo.get(MusicConstants.DEVICE_ID);
            String os = deviceInfo.get(OS);
            requestBody.put(WCFApisConstants.DEVICE_ID, deviceId);
            requestBody.put(MSISDN, msisdn);
            requestBody.put(PAYMENT_CODE, paymentCode.toString());
            requestBody.put(SERVICE, MUSIC);
            requestBody.put(UID, uid);
            if (os != null)
                os = os.toLowerCase();
            requestBody.put(OS, os);

            //request coming from older builds
            if (!requestBody.containsKey(PLAN_ID)) {
                String key = StringUtils.join(Arrays.asList(uid, os, deviceId), "_");
                String sessionDataString = wcfPayRedisCluster.get(key);
                if (sessionDataString != null) {
                    Map<String, Object> redisData = gson.fromJson(sessionDataString, Map.class);
                    if (redisData != null) {
                        for (Map.Entry<String, Object> entry : redisData.entrySet()) {
                            requestBody.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }

            if (requestBody.containsKey(RECEIPT_DATA)) {
                requestBody.put(RECEIPT, requestPayload);
                //removing the key sent in older builds
                requestBody.remove(RECEIPT_DATA);
            }

            if (requestBody.containsKey(SID)) {
                sid = (String) requestBody.get(SID);
            }
            if (requestBody.containsKey(PLAN_ID)) {
                planId = (Double) requestBody.get(PLAN_ID);
            }
            String responseBody = wcfApisService.verifyPaymentReceipt(requestBody.toString());
            WCFVerifyReceiptResponse verifyReceiptResponse = gson.fromJson(responseBody, WCFVerifyReceiptResponse.class);
            String url = verifyReceiptResponse.getData().getUrl();
            if (url == null || url.contains("payment-failed")) {
                status = Boolean.FALSE;
            }
        } catch (Exception e) {
            status = Boolean.FALSE;
            logger.error("Error occurred while verifying WCF payment receipt with uid : {} , payload : {}, paymentCode : {}, error : {} ", ChannelContext.getUid(), requestPayload, paymentCode, e.toString());
            logger.info("Exception caught in first {} , {}", e.getMessage(), e);
        }

        try {
            WCFTransactionHistory txnHistory = new WCFTransactionHistory();
            if (planId != null) {
                txnHistory.setProductId(planId.intValue());
            }
            txnHistory.setUid(uid);
            txnHistory.setMsisdn(msisdn);
            txnHistory.setTransactionId(sid);
            txnHistory.setRetryCount(0);
            txnHistory.setCreationDate(System.currentTimeMillis());
            txnHistory.setPayload(requestPayload);
            txnHistory.setServiceType(MUSIC);
            txnHistory.setOtpRequired(Boolean.FALSE);
            mongoMusicDBManager.addObject(WCF_TRANSACTION_HISTORY_COLLECTION, gson.toJson(txnHistory));
        } catch(Exception e) {
            logger.error("Error while inserting payment transaction history object in DB with uid : {}, planId : {} payload : {}", uid, planId, requestPayload);
            logger.info("Exception caught in second {} , {}", e.getMessage(), e);
        }

        JSONObject response = new JSONObject();
        response.put("status", "ok");
        if (paymentCode != null && paymentCode.equals(PaymentCode.ITUNES)
                && buildNo != null && buildNo <= 651) {
            response.put("susbcriptionStatus", status ? "ok" : "fail");
        } else {
            response.put("subscriptionStatus", status ? "ok" : "fail");
        }
        return response;
    }

    public JSONObject forwardInitWCFWebViewRequest(WCFPackGroup packGroup, Theme theme, String planId, WCFView view, String intent) {

        JSONObject response = new JSONObject();
        Map<String, String> deviceInfo = MusicDeviceUtils.parseMusicHeaderDID();

        // uid not ending with 0, hence not registered
        if (!ChannelContext.getUid().endsWith("0")) {
            String redirectUrl = getWcfWebViewUrlForNonLoggedInUser(deviceInfo);
            response.put(WCFApisConstants.REDIRECT_URL, redirectUrl);
            return response;
        }

        try {
            WCFInitWebViewRequest initWebViewRequest = new WCFInitWebViewRequest();
            initWebViewRequest.setAppVersion(deviceInfo.get(APP_VERSION_NO));
            initWebViewRequest.setBuildNo(Integer.parseInt(deviceInfo.get(APP_BUILD_NO)));
            initWebViewRequest.setDeviceId(deviceInfo.get(MusicConstants.DEVICE_ID));
            initWebViewRequest.setMsisdn(ChannelContext.getUser().getMsisdn());
            initWebViewRequest.setOs(deviceInfo.get(OS));
            initWebViewRequest.setUid(ChannelContext.getUid());
            initWebViewRequest.setIngressIntent(intent);
            if (UserDeviceUtils.isRequestFromWAP()) {
                initWebViewRequest.setAppId(WEB_APP_ID);
            }
            initWebViewRequest.setGeoLocation(prepareGeoLocationOnDemand(ChannelContext.getUser()));
            if (packGroup != null)
                initWebViewRequest.setPackGroup(packGroup);
            if (theme == null)
                theme = Theme.LIGHT;
            initWebViewRequest.setTheme(theme);
            if (planId != null)
                initWebViewRequest.setPlanId(planId);
            Gson gson = new GsonBuilder().serializeNulls().create();
            String payload = gson.toJson(initWebViewRequest);
            String responseString = wcfApisService.forwardInitWebViewRequest(payload, view);
            WCFWebViewResponse wcfWebViewResponse = gson.fromJson(responseString, WCFWebViewResponse.class);
            if (wcfWebViewResponse != null && wcfWebViewResponse.getData() != null) {
                response.put(WCFApisConstants.REDIRECT_URL, wcfWebViewResponse.getData().getRedirectUrl());
                response.put(WCFApisConstants.SID, wcfWebViewResponse.getData().getSid());
            }
        } catch(Exception e) {
            response.put(ERROR, e.getMessage());
        }
        return response;
    }

    private String getWcfWebViewUrlForNonLoggedInUser(Map<String, String> deviceInfo) {
        StringBuilder queryParams = new StringBuilder();
        queryParams.append("?" + OS + "=" + deviceInfo.get(OS));
        appendQueryParam(queryParams, WCFApisConstants.BUILD_NO, deviceInfo.get(APP_BUILD_NO));
        appendQueryParam(queryParams, WCFApisConstants.COUNTRY_OF_ACCESS, ChannelContext.getUserCoaContext());
        return musicConfig.getWcfWebViewBaseUrl()
                .concat(WCF_REGISTER_WEBVIEW_ENDPOINT).concat(queryParams.toString());
    }

    public void addConfigInfo(JSONObject json, JSONObject simInfo) {
        JSONObject configData = new JSONObject();
        processIntlRoamingFlow(true, simInfo, configData);
        JSONObject geo = new JSONObject();
        geo.put(JsonKeyNames.IS_GEO_RESTRICTION_PASSED, getGeoRestrictionPassed(simInfo));
        geo.put(JsonKeyNames.DESCRIPTION, getGeoBlacklistedResponse());
        configData.put(JsonKeyNames.GEO, geo);;
        json.put("config", configData);
        logger.info("prepared roaming and geo info is {}", configData);
    }

    public GeoLocation prepareGeoLocationOnDemand(User userInContext) {
        logger.info("Preparing geo location for this request for uid : {}", userInContext.getUid());
        GeoLocation geoLocation = new GeoLocation();
        if (StringUtils.isNotBlank(userInContext.getCountryId())) {
            geoLocation.setCountryCode(userInContext.getCountryId());
        } else if (StringUtils.isBlank(userInContext.getCountryId())) {
            userInContext = accountService.getUserFromDB(userInContext.getUid());
            accountRegistrationService.updateAndGetUserCountryIfNotExist(userInContext, true);
            geoLocation.setCountryCode(userInContext.getCountryId());
        }
        geoLocation.setAccessCountryCode(ChannelContext.getUserCoaContext());
        logger.info("Prepared GeoLocation with values {}", geoLocation);
        return geoLocation;
    }

}
