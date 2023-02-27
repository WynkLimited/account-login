package com.wynk.constants;

import com.wynk.music.constants.MusicContentLanguage;
import com.wynk.music.constants.MusicPackageType;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class MusicConstants {

    public static final int IMAGE_THUMBNAIL_WIDTH = 50;
    public static final int IMAGE_THUMBNAIL_HEIGHT = 50;
    public static final int IMAGE_SMALL_WIDTH = 120;
    public static final int IMAGE_SMALL_HEIGHT = 120;
    public static final int IMAGE_MEDIUM_WIDTH = 240;
    public static final int IMAGE_MEDIUM_HEIGHT = 240;
    public static final int IMAGE_LARGE_WIDTH = 320;
    public static final int IMAGE_LARGE_HEIGHT = 320;
    public static final int IMAGE_LARGE_HEIGHT_RECT = 180;

    public static final int DEFAULT_PAGINATION = 50;



    public static final int WINDOWS_INITIAL_BUILD_NUMBER = 1;


    public static final int ANDROID_RADIO_SUPPORT_BUILD_NUMBER = 25;

    public static final int ANDROID_FEATURED_DISABLE_SUPPORT_BUILD_NUMBER = 26;

    public static final int IOS_FEATURED_DISABLE_SUPPORT_BUILD_NUMBER = 24;

    public static final int ANDROID_USER_STATE_SEPARATION_BUILD_NUMBER = 47;

    public static final int IOS_USER_STATE_SEPARATION_BUILD_NUMBER = 54;

    public static final int ANDROID_ONDEVICE_BUILD_NUMBER = 41;

    public static final int ANDROID_ADHM_BUILD_NUMBER = 39;

    public static final int IOS_LANGSPLIT_BUILD_NUMBER = 90;
    public static final int IOS_ADHM_BUILD_NUMBER = 35;

    public static final String OFFLINE_NOTIFICATION_1 ="70";
    public static final String OFFLINE_NOTIFICATION_2 ="71";
    public static final String OFFLINE_NOTIFICATION_3 ="72";
    public static final String OFFLINE_NOTIFICATION_4 ="73";
    public static final String OFFLINE_NOTIFICATION_5 ="74";
    public static final String OFFLINE_NOTIFICATION_6 ="75";
    public static final String OFFLINE_NOTIFICATION_7 ="76";
    public static final String OFFLINE_NOTIFICATION_8 ="77";
    public static final String OFFLINE_NOTIFICATION_9 ="78";
    public static final String OFFLINE_NOTIFICATION_10 ="79";


    public static final String MUSICAPP_CONFIG_COLLECTION = "musicapp_config";

    public static final boolean    TRUE                           = true;
    public static final int    songPlayedCount                     = 3;
    public static final String songQualityThreshold                     = "h";

    public static final String Stream = "stream";
    public static final String Rent = "rent";
    public static final String Config = "config";
    public static final String Purchase = "purchase";


    public static final int OEM_BUILD_MIN_CONTENT_COUNT = 5;

    public static final int OEM_BUILD_MIN_MODULE_COUNT = 5;

    public static final List<String> ANDROID_OTHER_APP_PACKAGES = Arrays
            .asList("com.lenovo.anyshare.gps", "cn.xender");

    public static JSONArray EMPTY_JSON_ARRAY = new JSONArray();

    public static JSONArray ANDROID_OTHER_APP_PACKAGES_ARRAY;
    static{
    	ANDROID_OTHER_APP_PACKAGES_ARRAY = new JSONArray();
    	ANDROID_OTHER_APP_PACKAGES_ARRAY.addAll(ANDROID_OTHER_APP_PACKAGES);
    }

    public static final List<String> IOS_OTHER_APP_PACKAGES = Arrays.asList("gaanaApp://", "saavn://");
    public static JSONArray IOS_OTHER_APP_PACKAGES_ARRAY;
    static{
    	IOS_OTHER_APP_PACKAGES_ARRAY = new JSONArray();
    	IOS_OTHER_APP_PACKAGES_ARRAY.addAll(IOS_OTHER_APP_PACKAGES);
    }

    public static final List<MusicPackageType> gridViewPackages = new ArrayList<MusicPackageType>(
            Arrays.asList( MusicPackageType.REMIXES, MusicPackageType.CAMPAIGN_MODULE_1, //MusicPackageType.DEVOTIONAL,
                    MusicPackageType.CAMPAIGN_MODULE_2, MusicPackageType.CAMPAIGN_MODULE_3, MusicPackageType.CAMPAIGN_MODULE_4
                    , MusicPackageType.CAMPAIGN_MODULE_5, MusicPackageType.CAMPAIGN_MODULE_6, MusicPackageType.CAMPAIGN_MODULE_7
                    , MusicPackageType.CAMPAIGN_MODULE_8, MusicPackageType.HARYANVI_MODULE_1,  MusicPackageType.HARYANVI_MODULE_2,
                    MusicPackageType.HARYANVI_MODULE_3,MusicPackageType.HARYANVI_MODULE_4,MusicPackageType.HARYANVI_MODULE_5,
                    MusicPackageType.CAMPAIGN_MODULE_9,MusicPackageType.CAMPAIGN_MODULE_10,MusicPackageType.CAMPAIGN_MODULE_11,
                    MusicPackageType.CAMPAIGN_MODULE_12,MusicPackageType.CAMPAIGN_MODULE_13,MusicPackageType.CAMPAIGN_MODULE_14,
                    MusicPackageType.CAMPAIGN_MODULE_15,MusicPackageType.CAMPAIGN_MODULE_16,MusicPackageType.CAMPAIGN_MODULE_17,
                    MusicPackageType.CAMPAIGN_MODULE_18,MusicPackageType.CAMPAIGN_MODULE_19,MusicPackageType.CAMPAIGN_MODULE_20,
                    MusicPackageType.CAMPAIGN_MODULE_21,MusicPackageType.CAMPAIGN_MODULE_22,MusicPackageType.CAMPAIGN_MODULE_23,
                    MusicPackageType.CAMPAIGN_MODULE_24,MusicPackageType.CAMPAIGN_MODULE_25,MusicPackageType.CAMPAIGN_MODULE_26,
                    MusicPackageType.CAMPAIGN_MODULE_27,MusicPackageType.CAMPAIGN_MODULE_28,MusicPackageType.CAMPAIGN_MODULE_29,
                    MusicPackageType.CAMPAIGN_MODULE_30,MusicPackageType.CAMPAIGN_MODULE_31,MusicPackageType.CAMPAIGN_MODULE_32,
                    MusicPackageType.CAMPAIGN_MODULE_33));


    public static final JSONArray MYMUSIC_ITEM_IDS;
    static {
        MYMUSIC_ITEM_IDS = new JSONArray();
        MYMUSIC_ITEM_IDS.add(MusicPackageType.USER_RENTALS.getName());
        MYMUSIC_ITEM_IDS.add(MusicPackageType.USER_DOWNLOADS.getName());
       // MYMUSIC_ITEM_IDS.add(MusicPackageType.USER_FAVORITES.getName());
        MYMUSIC_ITEM_IDS.add(MusicPackageType.USER_PLAYLIST.getName());
        MYMUSIC_ITEM_IDS.add(MusicPackageType.USER_JOURNEY.getName());
    }

    public static final String CONTENT_TAKEN_DOWN = "Content Taken Down";


    public static List<String> ADHM_IMAGES;
    static {
        ADHM_IMAGES = new ArrayList<>();
        ADHM_IMAGES.add("http://s3-ap-south-1.amazonaws.com/wynk-music-cms/adhmimages/Bollywood-Running-Mix.png");
        ADHM_IMAGES.add("http://s3-ap-south-1.amazonaws.com/wynk-music-cms/adhmimages/Bring-It-Back-Down-600x600.png");
        ADHM_IMAGES.add("http://s3-ap-south-1.amazonaws.com/wynk-music-cms/adhmimages/Ready-To-Run.png");
        ADHM_IMAGES.add("http://s3-ap-south-1.amazonaws.com/wynk-music-cms/adhmimages/STRAIGHT-OUTTA-THE-600x600.png");
        ADHM_IMAGES.add("http://s3-ap-south-1.amazonaws.com/wynk-music-cms/adhmimages/Unstoppable-running-machine-600x600.png");
        //ADHM_IMAGES.add("http://s3-ap-southeast-1.amazonaws.com/bsbcms/adhmimages/morning-refresh.png");
        //ADHM_IMAGES.add("http://s3-ap-southeast-1.amazonaws.com/bsbcms/adhmimages/pump-it-up-600x600.png");
        ADHM_IMAGES.add("http://s3-ap-south-1.amazonaws.com/wynk-music-cms/adhmimages/radiooos.png");
        ADHM_IMAGES.add("http://s3-ap-south-1.amazonaws.com/wynk-music-cms/adhmimages/runners-600x600.png");
        //ADHM_IMAGES.add("http://s3-ap-southeast-1.amazonaws.com/bsbcms/adhmimages/stretch-it-out-600x600.png");
    }

    public static final int STATS_KAFKA_PARTITION = 8;

    public static final String NOT_MODIFIED_CODE = "304";

    public static final int ADTECH_KAFKA_PARTITION = 8;

    public static final int FP_KAFKA_PARTITION = 16;

    //X-BSY-DID header
    public static final String DEVICE_ID = "deviceid";
    public static final String OS = "os";
    public static final String OS_VERSION = "osversion";
    public static final String APP_BUILD_NO = "appbuildno";
    public static final String APP_VERSION_NO = "appversionno";

    //X-BSY-NET header
    public static final String NETWORK_TYPE = "networkType";
    public static final String SUB_NETWORK_TYPE = "subNetworkType";
    public static final String NETWORK_QUALITY = "networkQuality";

    //For download count limit
    public static final int DEFAULT_LIMIT_FOR_RENTED_IDS = 200;

    public static final int USER_SONG_MAX_DOWNLOAD_LIMIT = 5;

    public static String REDIS_MUSIC_SONG_PREFIX = "v_s_%s";
    public static final String REDIS_MUSIC_ALBUMS_HASH = "v_al_%s";
    public static String REDIS_MUSIC_ARTIST_HASH = "v_ar_%s";

    public static final String REDIS_PENDING_TRANSACTION_ID = "pending_transaction_id_";
    public static String MUSIC_LYRICS_ENGLISH_REDIS_HASH = "songlyrics_en_%s";
    public static final String MUSIC_SHORTURL_REDIS_HASH = "music_shorturls";
    public static final String MUSIC_REVERSE_SHORTURL_REDIS_HASH = "music_reverseshorturls";
    public static final String REDIS_MUSIC_TRANSLITERATION_HASH = "music_transliteration";
    public static String REDIS_MUSIC_USER_SONGS_PLAYED_HASH = "m_uidfavs_%s";
    public static String REDIS_MUSIC_ARTIST_IMAGE_HASH = "music_artistimagehash";
    public static final String REDIS_KEY_FOR_HT_PRODUCTS = "ht_products";
    public static final String REDIS_KEY_FOR_HT_PRODUCTS_NEW = "ht_products_new";
    public static final String REDIS_KEY_FOR_HT_TRIAL_PRODUCTS_NEW = "ht_trial_products";
    public static final String REDIS_KEY_FOR_DOWNLOADS_PRODUCTS = "downloads_products";
    public static final String REDIS_KEY_FOR_STREAMING_PRODUCTS = "streaming_products";
    public static final String REDIS_KEY_FOR_HIDE_ADS_PRODUCTS = "hide_ads_products";
    public static final String REDIS_KEY_FOR_WCF_OFFERS_META = "wcf_offers_meta";

    public static final String REDIS_KEY_FOR_WCF_OFFERS_META_COMPLETE = "wcf_offers_meta_complete";
    public static final String REDIS_KEY_FOR_WCF_HT_META = "ht_offers_meta";
    // renaming BLACK_LISTED_PRODUCT from 999992 to 483647 inorder to disable pop up. Cause 483647 doesn't exists.
    public static final int BLACK_LISTED_PRODUCT = 483647;

    public static String REDIS_DAILY_SUBSCRIPTION_PRODUCTID_COUNT = "day_%s_productId_%s";

    public static final String REDIS_LAST_BATCHID_PREFIX = "batchid_";

    public static final String REDIS_NOTIF_USER_LAST_PROCESSED = "notif_user_lastprocessed";

    public static final String REDIS_UID_HASH = "uid:%s";
    public static final String MTOKEN_UID_HASH = "mtoken:%s";
    public static final String REDIS_DEVICEID_HASH = "udidmap_%s";
    public static final String REDIS_ADHM_PLAYLIST_HASH = "adhm:%s";
    public static final int NUMBER_OF_BUCKETS = 100;
    public static final int DID_UID_NUMBER_OF_BUCKETS = 10000;

    public static final String REDIS_MUSIC_DELETED_ARTISTS_PREFIX      = "music_deleted_artists";
    public static final String REDIS_MUSIC_DELETED_SONGS_PREFIX      = "music_deleted_songs";
    public static final String REDIS_MUSIC_DELETED_ALBUM_PREFIX      = "music_deleted_album";
    public static final int     REDIS_DELETED_SONGS_EXPIRY            = 15 * 60;
    public static final int     MUSIC_SONG_NUMBER_OF_BUCKETS          = 10000;

    public static final int SHORT_URL_VERSION = 1;
    public static final String EMPTY_STATE = "http://s3-ap-southeast-1.amazonaws.com/bsy/wynk/wynk_noimg.png";
    public static final String HEART = "liked";
    public static final String ARROW = "downloaded";
    public static final String UNFINISHED = "rented";
    public static final String LOCAL = "on_device";
    public static final String Purchased = "purchased";
    public static final String FAV = "favourites";

    public static final String DEFAULT_WYNK_IMAGE = "http://s3-ap-southeast-1.amazonaws.com/bsy/wynk/wynk_noimg.png";
    public static final String NOTIFICATION_TOPIC_GLOBAL = "arn:aws:sns:ap-southeast-1:536123028970:twang-global";
    public static final String NOTIFICATION_TOPIC_REGIONAL = "arn:aws:sns:ap-southeast-1:536123028970:twang-rgn";

    public static final boolean cRadioOnSongEnabled = false;
    public static final String PAYMENT_COLLECTION_NAME = "music_payments";

    public static final String SOURCE_APP = "app";
    public static final String SOURCE_WAP = "wap";

    public static final String STATUS_PUBLISH_PUBLISHED = "PUBLISH_PUBLISHED";
    public static final String PUBLISH_CLOSED           = "PUBLISH_CLOSED";
    public static final String MUSIC_SONG_STATE         = "musicSongState";
    public static final int MINIMUM_ALBUM_CONTENT_SIZE = 1;

    //FUP Error codes
    public static final int STREAMING_LIMIT_REACHED_ERROR_CODE = 0;
    public static final int STREAMING_LIMIT_REACHED_99_ERROR_CODE = 1;
    public static final int RENTALS_LIMIT_REACHED_99_ERROR_CODE = 2;
    public static final int USER_NOT_FOUND_ERROR_CODE = 3;
    public static final int STREAMING_SUCCESS_BUT_SHOW_NOTIFICATION = 4;

    public static final int MICROMAX_BLACKLISTED_CONTENT = 5;
    public static final int GEO_BLACKLISTED_CONTENT = 6;

    public static final int INTL_ROAMING_EXPIRED = 7;
    public static final int INTL_ROAMING_INFO = 8;
    public static final int INTL_ROAMING_INVOKE_REGISTRATION = 9;
    public static final int INTL_ROAMING_INELIGIBLE = 10;

    public static final int INTL_ROAMING_NOTIF_EXPIRY = 1;
    public static final int INTL_ROAMING_NOTIF_EXPIRED = 2;
    public static final int INTL_ROAMING_NOTIF_ACTIVATED = 3;
    public static final int INTL_ROAMING_NOTIF_INELIGIBLE = 4;
    public static final int INTL_ROAMING_NOTIF_DEVICE_INELIGIBLE = 5;
    public static final int INTL_ROAMING_NOTIF_INVOKE_REGISTRATION = 6;
    public static final int INTL_ROAMING_NOTIF_DEFAULT_CODE = 100;

    public static final String INTL_ROAMING_EXPIRY_PREFIX = "ir_expired";
    public static final String INTL_ROAMING_ACTIVATE_PREFIX = "ir_activated";
    public static final String INTL_ROAMING_GENERIC_PREFIX = "ir_generic";

    public static final String INTL_ROAMING_NOTIF_DATE_INFO = "You can play songs on WiFi while abroad till %s.";
    public static final String INTL_ROAMING_NOTIFICATION_EXPIRY_WARNING = "You can use Wynk Music on this network for 28 days every quarter free of cost. Offline songs can still be played as usual.";
    public static final String INTL_ROAMING_NOTIFICATION_EXPIRED = "You can use Wynk Music on this network for 28 days every quarter free of cost. Offline songs can still be played as usual.";
    public static final String INTL_ROAMING_NOTIFICATION_ACTIVATED = "It seems you are using a WiFI/mobile network in a country where Wynk is not available. You can use Wynk Music on this network for 28 days every quarter free of cost. Offline songs can still be played as usual. In case you are on VPN, switch it off to get full benefits of Wynk Music.";
    public static final String INTL_ROAMING_NOTIF_EXPIRED_DATE_INFO = "Your international roaming pack is expired on %s.";


    //For collections
    public static final String USER_COLLECTION = "users";
    public static final String WAP_USER_COLLECTION = "wapusers";
    public static final String SUBSCRIPTION_COLLECTION = "subscription";
    public static final String WCF_TRANSACTION_HISTORY_COLLECTION = "wcfTransactionHistory";
    public static final String WCF_SUBSCRIPTION_DAILY_REPORT = "wcfSubscriptionDailyReport";
    public static final String WCF_PURCHASE_SONG_HISTORY_COLLECTION = "wcfPurchaseSongHistory";



    public static final String TITLE_FAVORITES                   = "All Liked";
    public static final String TITLE_RENTALS                     = "All Downloaded";
    public static final String TITLE_DOWNLOADS                   = "All Purchased";
    public static final String TITLE_JOURNEY                     = "My Journey";
    public static final String TITLE_PLAYLISTS                   = "My Playlists";

    public static final String REMAINING_KEY = "remaining";
    public static final String REMAINING_DAYS_KEY = "remainingDays";
    public static final String TOTAL_KEY = "total";
    public static final String PERCENTAGE_USED = "percentage_used";
    public static final String CURRENT_KEY = "current";

    public static final String MUSIC_WAP_FUP_FAQ_PAGE = "http://wynk.in/music/faq.html#fup";
    public static final String MUSIC_WAP_OFFER_PACK_FAQ_PAGE = "http://wynk.in/music/faq.html#fup";

    public static final String WAP_URL                       = "http://samsung.wynk.in/music/index.html";
    public static final String WAP_URL_KEY = "wap_url";
    public static final String WAP_URL_HOST_KEY = "wap_url_host";
    public static final String WAP_OTP_URI = "/music/v1/account/otp/generate";

    public static final String MUSIC_SINGERS_FILE = "singers.json";
    public static final String MUSIC_ACTORS_FILE = "actors.json";
    public static final String MUSIC_ARTISTS_FILE = "artists.json";
    public static final String MUSIC_COMPOSERS_FILE = "composers.json";
    public static final String MUSIC_DIRECTORS_FILE = "directors.json";
    public static final String MUSIC_LYRICIST_FILE = "lyricists.json";
    public static final String MUSIC_ALL_ARTIST_FILE = "all_artists.json";

    public static final String MUSIC_HEADER_DID = "x-bsy-did";
    public static final String MUSIC_HEADER_WAP = "x-bsy-wap";
    public static final String MUSIC_HEADER_IS_WAP = "x-bsy-iswap";
    public static final String MUSIC_HEADER_DEV = "x-bsy-dev";
    public static final String MUSIC_HEADER_CID = "x-bsy-cid";
    public static final String MUSIC_HEADER_WAPID = "x-bsy-wid";
    public static final String MUSIC_HEADER_NET = "x-bsy-net";

    public static final long    MUSIC_IFB_VALIDITY_IN_MILLIS          = 5184000000L; // 60 days
    public static final long    MUSIC_FUP_VALIDITY_IN_MILLIS          = 2592000000L; // 30 days
    public static final long    NOTIFICATION_TIME_LIMIT_WARNING       = 3600000L;    // 1 hour

    public static final long    MUSIC_AOP_VALIDITY_IN_MILLIS          =  604800000L; // 7 days
    public static final long    MUSIC_SOP_VALIDITY_IN_MILLIS          =   86400000L; // 1 days

    public static final String OTP_EXPIRED_TITLE                     = "Your PIN expired";
    public static final String OTP_EXPIRED_MESSAGE                   = "Please enter the new 4 digit PIN you received via SMS.";
    public static final String OTP_INVALID_TITLE                     = "That's not right";
    public static final String OTP_INVALID_MESSAGE                   = "Remember the 4 digit PIN you received via SMS? Enter that!";

    public static final String MODULE_ID_DOWNLOAD                    = "DOWNLOAD";
    public static final String MODULE_ID_PURCHASED                   = "PURCHASED";

    public static final int     JOURNEY_MAX_LENGTH                    = 25;

    public static final int     FAVOURITE_MAX_LENGTH                  = 50;

    public static String USER_SONG_SCORE_S3_URL                = "http://twanganalytics.s3.amazonaws.com/recommender/user_song_score/%s/%s/%s";

    public static String USER_SONG_SCORE_S3_URL_NEW            = "recommender/user_song_score_hourly_changed/%s/%s/%s";

    public static String MUSIC_ANALYTICS_S3_BUCKET             = "twanganalytics";

    public static String APP_DEFAULT_VERSION                   = "1.0.0";

    //http://dev.maxmind.com/geoip/legacy/codes/iso3166/
    public static final List<String> ALLOWED_COUNTRY_LIST = Arrays.asList("IN", "LK", "BD", "AP", "O1");
    public static final String IBM_DATA_S3_URL_PREFIX = "http://almusicapp.s3.amazonaws.com/se/ibmsubscriptionsftp/BSB_143_";
    public static final String IBM_DATA_S3_NEW_SUBS_SUCCESS_STATUS = "New-Subscription Success";
    public static final String IBM_DATA_S3_NEW_SUBS_FAILURE_STATUS = "New-Subscription Low Balance";
    public static final String IBM_DATA_S3_RENEW_SUBS_SUCCESS_STATUS = "Charging Success";
    public static final String IBM_DATA_S3_RENEW_SUBS_FAILED_STATUS = "Re-Subscription Failure";
    public static final String IBM_DATA_S3_CUSTOMER_DEPROVISION_STATUS = "Start/Stop Initiated De-Subscription Success";
    public static final String IBM_DATA_S3_TRANSTYPE_NEW_SUBS = "New-Subscription";
    public static final String IBM_DATA_S3_TRANSTYPE_RENEW_SUBS = "Re-Subscription";

    public static final String LAPU_DATA_S3_FILE_PREFIX = "lapu/";
    public static final String LAPU_DND_FILE_URL = "lapu/wynk_exclusion_base_18june15.csv";

    public static final String LAPU_FILE_PROCCESSED_PREFIX = "lapu_";
    public static final String LAPU_DND_SET_PREFIX = "lapu_dndlist_";

    public static final int    DAY                               = 24 * 3600 * 1000;

    public static final int    DAY_SECONDS                               = 24 * 3600;

    public static final String TEMP_LAPU_COLLECTION = "lapuTempSubscription";

    public static final String LAPU_NOTIFICATION_URL = "admin/lapu_subscription/create";
    public static final String POSTPAID_NOTIFICATION_URL = "admin/postpaid_subscription/create";


    public static final String WCF_TRANSACTION_BASE_URL              = "/wynk/v1/s2s/otc/init";
    public static final String WCF_POINT_DOWNLOAD_BASE_URL           = "/wynk/v1/otc/payment";
    public static final String API_POINT_DOWNLOAD_RETURN_URL    = "wcfcb";


    public static final String LAPU_WYNK_PLUS_ALREADY_SMS              = "Thanks for your Wynk Plus subscription request @Rs29 for 30 days. You are already a subscribed user. Check My Account section in App for details";
    public static final String LAPU_WYNK_PLUS_SUBSCRIBED_SMS           = "Thanks for your Wynk subscription request. You are currently in Suspension with AutoRenewal ON. Pls top-up to renew. Check My Account section in App for details";

    public static final String LAPU_WYNK_PLUS_ALREADY_CLM_SMS          = "Thanks for your free Wynk Plus subscription request with 3G recharge. You are already a subscribed user. Check My Account section in App for details";
    public static final String LAPU_WYNK_PLUS_SUBSCRIBED_CLM_SMS       = "Thanks for free Wynk subscription request with 3Grecharge. You are currently in Suspension with AutoRenewal ON. Top-up to renew. Check My Account section in app";

    public static final String LAPU_WYNK_PLUS_SUCCESS_SMS_FREE         = "FREE Wynk music subscription has been activated for 30 days with your 3G recharge. Download unlimited songs. Check My Account section in App for details";
    public static final String LAPU_WYNK_PLUS_SUCCESS_SMS_3G           = "Wynk music subscription has been activated for 30 days with your 3G recharge. Download unlimited songs. Check My Account section in App for details";
    public static final String LAPU_WYNK_PLUS_SUCCESS_SMS              = "Wynk music subscription has been activated for 30 days with your recharge. Download unlimited songs. Check My Account section in App for details";

    public static final String LAPU_WYNK_POSTPAID_SUCCESS_DOWNLOAD_SMS = "FREE Wynk Music App subscription with your 4G recharge. Enjoy unlimited free streaming and song downloads. To get app, Click http://wynk.in/mu";
    public static final String LAPU_WYNK_POSTPAID_SUCCESS_SMS          = "FREE Wynk Music subscription has been activated with your 4G recharge. Download unlimited songs to enjoy offline music. Check My Account section in App for info";
    public static final String LAPU_WYNK_POSTPAID_ACTIVATION_SUCCESS_SMS = "Wynk Music subscription has been activated for you. Download unlimited songs to enjoy offline music. Check My Account section in App for details";


    public static final String LAPU_WYNK_FREEDOM_ALREADY_SMS           = "Thanks for your Wynk Freedom subscription request @Rs129 for 30 days. You are already a subscribed user. Check My Account section in App for details";
    public static final String LAPU_WYNK_FREEDOM_SUBSCRIBED_SMS        = "Thanks for your Wynk subscription request. You are currently in Suspension with AutoRenewal ON. Pls top-up to renew. Check My Account section in App for details";

    public static final String LAPU_WYNK_FREEDOM_SUCCESS_SMS           = "Wynk Freedom subscription@Rs129 is now activated. Enjoy free streams and downloads. Renew in 30days for offline music. Check My Account section in App for details";


    public static final String POSTPAID_SUBSCRIPTION_SUCCESS_STRING_FOR_APP_USER = "Wynk Music App offer - Annual free Wynk Plus subscription is activated just for you. Start downloading music now! Check My Account section in App for details.";
    public static final String POSTPAID_SUBSCRIPTION_SUCCESS_STRING_FOR_NON_APP_USER = "Download Wynk Music App to enjoy your fav songs in HD without ads! Annual subscription worth Rs1200 FREE just for you! Click http://wynk.in/mu";
    public static final String POSTPAID_SUBSCRIPTION_ALREADY_SUBSCRIBED = "Thanks for your Wynk Plus subscription request for one year. You are already a subscribed user. Check My Account section in App for details.";

    public static final String WYNK_SUBSCRIPTION_ENDED                  = "Your Wynk music subscription has ended. Please Renew / Subscribe from My Account section in the App.";

    public static final String WYNK_MOVIES_SMS = "Bored at home? Watch 10k+ Movies, Shows, 400+ LIVE TV Channels, only on Airtel Xstream App. One app for all your entertainment. Play now bit.ly/3fEHlW8";

    public static final String WYNK_SUBSCRIPTION_AUTORENEW_MUSIC_PACK   = "Wynk Music subscription has been renewed for you for FREE. Continue enjoying unlimited songs, offline &amp; online. Check My Account section in App for details";

    public static final String RAPU_SUBSCRIPTION_AUTORENEW_MUSIC_PACK   = "Wynk Music subscription has been renewed on your number for FREE. You can continue enjoying your music offline.";

    public static final String WYNK_SUBSCRIPTION_AUTORENEW_POSTPAID_PACK = "Wynk Music subscription has been extended for you for FREE. Continue enjoying unlimited songs, offline &amp; online. Check My Account section in App for details";

    public static final String THANKS_PLATINUM_USER_MESSAGE= "Your FREE access to Wynk Premium is ACTIVE. Enjoy Ad free music with unlimited downloads & unlimited hello tunes: bit.ly/WynkPlat";

    public static final String SMS_MESSAGE_SHORTCODE                   = "A$-WYNKED";
    public static final List<String> IPAYY_RECOGNISED_OPERATORS = Arrays.asList("airtel", "idea", "vodafone");
    public static final String IPAYY_SUPPORT_EMAIL = "contact@wynk.in";



    public static final String REDIS_MIN_ANDROID_VERSION_KEY = "minAndroidVersion";
    public static final String REDIS_MIN_IOS_VERSION_KEY = "minIosVersion";
    public static final String REDIS_MIN_WINDOWS_VERSION_KEY = "minWindowsVersion";
    public static final String REDIS_TARGET_ANDROID_VERSION_KEY = "targetAndroidVersion";
    public static final String REDIS_TARGET_IOS_VERSION_KEY = "targetIosVersion";
    public static final String REDIS_TARGET_WINDOWS_VERSION_KEY = "targetWindowsVersion";

    public static final String HEADER_AUTHORIZATION                = "Authorization";
    public static final String BASIC_AUTH_HEADER_SAMSUNG_GTM       = "Basic 4VDYCwt3VrNkVSwn0pCQrlZAK7A=";

    public static final String OFFER_PACK_NOTIFICATION_ID = "1";

    //Moods
    public static final String UNKNOWN = "unknown";
    public static final String MOODS_FEELING_GOOD_URL = "http://s3-ap-southeast-1.amazonaws.com/almusicapp/moods/feeling_good.png";
    public static final String MOODS_RELAXED_URL = "http://s3-ap-southeast-1.amazonaws.com/almusicapp/moods/chill_out.png";

    //galaxy playlist
    public static final String IMG_WYNK_IN = "http://img.wynk.in/unsafe/0x0/top/";

    public static final String ACTIVATION_OFFER_PACK_NOTIFICATION_ID = "2";
    public static final String SPECIAL_DAY_OFFER_PACK_NOTIFICATION_ID = "3";
    public static final String WYNK_CONTEST_NOTIFICATION_ID = "4";
    public static final String WYNK_AIRTEL_SUBSCRIPTION_OFFER_NOTIFICATION_ID = "5";
    public static final String WYNK_SUBSCRIPTION_OFFER_NOTIFICATION_ID = "6";
    public static final String WYNK_FREE_DATA_OFFER_NOTIFICATION_ID = "11";
    public static final String WYNK_FREE_DATA_OFFER_31_NOTIFICATION_ID = "12";
    public static final String WYNK_FREE_DATA_OFFER_35_NOTIFICATION_ID = "13";

    public static final String WYNK_NON_AIRTEL_SUBSCRIPTION_OFFER_NOTIFICATION_ID = "7";
    public static final String WYNK_NON_AIRTEL_SUBSCRIPTION_OFFER_NOTIFICATION_ID_NEVER_SUBSCRIBED = "8";
    public static final String WYNK_REGISTER = "16";

    public static final String WYNK_SHARE_ON_WHATSAPP_POPUP = "9";
    public static final String WYNK_TAMIL_USER_POPUP = "10";
    public static final String WYNK_NON_AIRTEL_IDEA_NOTIFICATION_ID = "15";
    public static final String WYNK_NON_AIRTEL_VODAFONE_NOTIFICATION_ID = "17";

    public static final String WYNK_TELUGU_USER_POPUP = "18";

    public static final String INTL_ROAMING_STATIC_ID = "100";


    public static final String WYNK_NIGHT_CASHBACK_NOTIFICATION_ID = "srch_bsb_11";

    public static final int REDIS_USER_EXPIRY_DURATION = 15*60;// 15 minutes
    public static final int REDIS_USER_TOKEN_EXPIRY_DURATION = 1*12*60*60;// 12 hrs

    public static final String CP_NOTIFICATION_QUEUE_NAME = "cpNotificationQueue";

    public static final int IOS_SUPPORT_FOR_RADIO_NOTIFICATIONS = 14;

    public static final int ANDROID_RICH_NOTIFICATION_BUILD_NUMBER = 24;
    public static final int ANDROID_RICH_NOTIFICATION_BUILD_NUMBER_NEW = 48;

    public static final int IOS_SUPPORT_FOR_EXTERNAL_WEBVIEW = 35;

    public static final int ANDROID_SUPPORT_FOR_EXTERNAL_WEBVIEW = 37;
    public static final int ANDROID_SUPPORT_FOR_PACKAGE_DEEP_LINKING = 41;
    public static final int IOS_SUPPORT_FOR_PACKAGE_DEEP_LINKING = 44;

    public static final int ANDROID_NOUGAT_NOTIFICATION_OS_VERSION = 24;

    public static final int ANDROID_INITIAL_BUILD_NUMBER = 1;

    public static final int DEFAULT_ITEM_COUNT = 15;
    public static final int DEFAULT_FEATURED_COUNT = 6;
    public static final int MINIMISED_ITEM_COUNT = 9;

    public static final int DEFAULT_MY_STATION_COUNT = 8;

    public static final int DEFAULT_REDESIGN_ITEM_COUNT = 100;

    //GCM
    public static final String GCM_ERROR_CODE_NOT_REGISTERED = "NotRegistered";
    public static final String GCM_ERROR_CODE_UNAVAILABLE = "Unavailable";

    public static final String NOTIFICATION_SEND_TIME_EXCEEDED_WARNING = "notificationSendTimeWarning";
	public static final String SPECIAL_DAY = "specialDay";

	public static final String OS_ANDROID = "Android";
	public static final String OS_IOS = "iOS";
	public static final String OS_WINDOWS = "Windows";

    public static final String ALMUSICAPP = "almusicapp";
    public static final String WYNK_FRAMEWORK = "wynk-framework";


    public static final String RENTALS = "rentals";
    public static final String DOWNLOADS = "downloads";

    //InactiveUsers path
    public static final String UNR_INACTIVE = "UnRInactive";
    public static final String UNPROCESSED = "unprocessed";
    public static final String PROCESSED = "processed";


    public static String DAILY_ACTIVE_USERS_S3_BUCKET             = "twanganalytics";
    public static String REDIS_INACTIVE_USER_SORTED_SET           = "inactive_zset";
    public static String REDIS_INACTIVE_USER_LAST_PROCESSED_DATE           = "last_processed_date";


    public static final int MINIMISED_MODULE_COUNT = 8;

    public static final String ADHM_PLAYLIST                = "adhm_playlist";
    public static final String ADHM_PACKAGE_ID              = "srch_adhm_playlist";
    public static final String ADHM_PLAYLIST_TITLE          = "My Running Mix";
    public static final String ADHM_PREFIX                  = "adhm_";
    public static final String WWW                          = "www";

    public static final int ONDEVICE_MINIMUM_DURATION_SCAN  = 30;
    public static final int METAMATCH_PAYLOAD_SIZE          = 5;
    public static final int PLAYLIST_THRESHOLD              = 5;
    public static final int FINGERPRINT_PAYLOAD_SIZE        = 0;
    public static final long META_MAPPING_REPEAT_INTERVAL   = 5 * 60 * 1000;
    public static final int FINGERPRINT_FETCH_PAYLOAD_SIZE  = 0;

    public static final long FINGERPRINT_POLLING_INTERVAL    = 7200;    // in seconds

    public static final String FFMPEG_FILE_LOCATION          = "http://s3-ap-southeast-1.amazonaws.com/almusicapp/ffmpeg/";

    public static final long DATA_SAVE_NETWORK_BOUNDRY       				= 1;
    public static final long DATA_SAVE_ABOVE_NETWORK_BOUNDRY_IN_SECONDS     = 50*1000;
    public static final long DATA_SAVE_BELOW_NETWORK_BOUNDRY_IN_SECONDS   	= 30*1000;

    public static final long NORMAL_NETWORK_BOUNDRY          				= 1;
    public static final long NORMAL_ABOVE_NETWORK_BOUNDRY_IN_SECONDS        = 50*1000;
    public static final long NORMAL_BELOW_NETWORK_BOUNDRY_IN_SECONDS        = 10*60*1000;

    public static final long PREFETCH_NETWORK_BOUNDRY        				= 2;
    public static final long PREFETCH_MINIMUM_BUFFER         				= 30*1000;


    // PRE CACHING CONSTANTS
    public static final String days_of_week                      = "1,2,3,4,5,6,7";
    public static final String hour_of_day                       = "04";

    //featured Call
    public static final String FEATURED_CONTENT_OFF              = "featuredContentOff";
    public static final String USER_STATE_SEPARATE               = "isFeaturedContentOff";
    public static final String INNER_PACKAGE_SUPPORT             = "innerPackageSupport";
    public static final String OEM_BUILD                         = "oemBuild";
    public static final String SHOW_ADHM_MODULE                  = "showADHMModule";
    public static final String IS_ANDROID                        = "isAndroid";
    public static final String IS_SHUFFLE                        = "isShuffle";

    public static final int     HOMEPAGE_MY_FAV_POSITION           = 3;

    public static final int     API_CALL_TIME_INTERVAL             = 30 * 60 * 1000;     // in milli-secs
    public static final int     USER_STATE_SYNC_TIME               = 2 * 60 * 60 * 1000; // in milli-secs

    public static final int     PRE_CACHING_NUMBER_OF_SONGS        = 4;

    public static final int      COOKIE_EXPIRY_TIME                = 24*60*60;
    public static final Boolean PRE_CACHING_STATUS                = true;
    public static final Boolean PRE_CACHING_STATUS_FOR_100         = false;
    public static final Boolean PRE_CACHING_STATUS_FOR_ANDROID        = false;

    public static final int      SONG_RETRY_INTERVAL               = 15*60;
    public static final int      FLAKY_NETWORK_SONGS_COUNT         = 5;

    public static final int         SAMSUNG_MINUSONE_ITEM_COUNT           = 5;
    public static final String SAMSUNG_GTM                           = "SAMSUNG_GTM";
    public static final String POPULAR_SEARCHES                      = "POPULAR SEARCHES";

    public static final String SUBSCRIPTION_SUCCESS_PARAMETER    = "bsyreload=true";
    public static final List<MusicContentLanguage> fullyCuratedLanguages = Arrays.asList(new MusicContentLanguage[]{MusicContentLanguage.HINDI,MusicContentLanguage.ENGLISH,MusicContentLanguage.TAMIL,MusicContentLanguage.TELUGU,MusicContentLanguage.PUNJABI});

    public static final String MUSIC_CONFIGURATION_KEY = "musicConfKey";

    public static final Integer WCF_MAX_RETRY_COUNT = 10;

    public static final Integer WCF_OFFER_CALL_INTERVAL_IN_DAY = 1;

    public static final Long WCF_OFFER_CALL_INTERVAL_IN_MILLISECONDS = 7 * 24 * 60 * 60*1000L;

    public static final Long NDS_CALL_INTERVAL_IN_MILLISECONDS = 24 * 60 * 60*1000L;

    public static final double DAYS_FOR_USER_TO_BE_VALID = 1.55;

    public static final double DAYS_FOR_USER_TO_SEND_PLATINUM_SMS = 180.55;

    public static final Long FUP_TIME_SPAN_IN_DAYS = 30L;

    public static final Integer MAX_RECOMMENDED_PACK_SHOWN = 3;

    public static final Integer MAX_OFFER_PACK_SHOWN = 1;

    public static final String UNSUBSCRIBE_PACK_URL = "unsubscribePack";

    public static final String SUBSCRIBE_PACK_URL = "subscribePack";

    public static final String NEW_SUBSCRIBE_PACK_URL = "v2/subscribePack";

    public static final String SEND_OTP_URL = "account/payment/otp";

    public static final String CONFIRM_PAYMENT_URL = "account/payment/confirm";

    public static final String SUBSCRIBE_CALL_BACK_URL = "cb/subscribe/callback";

    public static final String UNSUBSCRIBE_IOS_URL = "https://support.apple.com/en-in/HT202039";

    public static final String DEFAULT_BUTTON_COLOUR = "#0000FF";

    public static final String DEFAULT_TITLE_COLOUR ="#000000";

    public static final String DEFAULT_SUBTITLE_COLOUR ="#000000";

    public static final String DEFAULT_BUTTON_TEXT_COLOUR="#000000";

    public static final String DEFAULT_STATUS_MESSAGE_COLOUR="#0000FF";

    public static final Long MINUTES_IN_MILLISECONDS = 1000 * 60L;

    public static final Long WCF_SUBSCRIPTION_CRON_MINUTES = 30L;

    public static final String USER_CONTENT_LANGUAGES      = "contentLangs";

    public static final String CONTENT_LANG_DELIMETER      = "|";

    public static final String CONTENT_LANGS_MIGRATION      = "hi,en";

    public static final String WCF_RECONCILE_FILE_NAME = "subscriptionDailyDump";

    public static final Long WCF_PURCHASE_SONG_CRON_MINUTES = 30L;

    public static final Long WCF_PURCHASE_SONG_EXPIRE_IN_SECONDS = 86400*3L;  //configure to 3 days

    public static final Long WCF_TXN_EXPIRE_IN_SECONDS = 86400*3L;  //configure to 3 days

    public static final String MOENGAGE = "MOENGAGE";

    public static final int MOENGAGE_KAFKA_PARTITION = 8;

    public static final String MOENGAGE_NOTIFY_URL ="https://api.moengage.com/v1/customer?app_id=";

    public static final String PRICE_BANNER_TEXT = "R̶s̶.9̶9̶ Free";

    public static final Integer WAP_STREAMING_BYTE_SIZE = 299999;

    public static final Boolean REG_BROADBAND_ENABLE = Boolean.TRUE;
    public static final Integer WAP_STREAMING_MAX_BYTE_SIZE = 599999;

    public static final Integer WAP_STREAMING_TIME_FACTOR_MULTIPLIER = 5;

    public static final Integer WAP_STREAMING_TIME_FACTOR_ADDER = 2;

    public static final Long LAST_INACTIVE_IN_SECONDS = 86400*90L;

    public static final Long NON_PREMIUM_SINCE_IN_DAYS = 60L;

    public static final Integer RETRY_AUTO_REGISTRATION_COUNT =5;

    public static final Integer FMF_SONGS_ADS_COUNT = 5;

    public static final Integer APPS_FLYER_TIME_SPAN = 30;

    public static final Integer APPS_FLYER_SONGS_SPAN = 5;

    public static final String TITLE_COLOUR = "#333333";

    public static final String BUTTON_COLOUR = "#149ddd";

    public static final String SUBTITLE_COLOUR = "#333333";

    public static final Integer APPS_FLYER_AIRTEL_TIME_SPAN = 7;

    public static final Integer APPS_FLYER_AIRTEL_SONGS_SPAN = 20;

    public static final int LANG_CARD_POSITION = 2;

    public static final String HARYANVI_ON_APP = "ha";

    public static final int ARTIST_EXPIRY = 3*24*60*60;
    public static final int SONG_EXPIRY = 3*24*60*60;
    public static final int ALBUM_EXPIRY = 6*60*60;
    public static final int MAX_OTP_ATTEMPTS_EXPIRY = 5*60;

    public static final String shortUrlDomain = "wynk.in";
    public static final String basicShortUrlDomain = "wynktube.in";

//    public static final List<String> EXCLUDE_HANDLERS = Arrays.asList("packs","trendingsearch","profile","statsnouser","cscgw","health");
    public static final List<String> EXCLUDE_HANDLERS = new ArrayList<>();
    public static final String UNINSTALL_MESSAGE = "Music will miss you! We at Wynk Music are so sad to see you go. Please help us improve by taking this 2 min survey";
    public static final String UNINSTALL_MESSAGE_REDIS_KEY_PREFIX = "dont_send_";

    public static final String NEW_USER = "newUser";

    public static final int WYNK_AIRTEL_USER_BASE_PACK = 10005;

    public static final int WYNK_NON_AIRTEL_USER_BASE_PACK = 10006;

    public static final String TOKEN = "token";

    public static final String REDIS_USER_UID_KEY = "useruidkey_";

    public static final int REDIS_USER_UID_KEY_TTL_IN_SEC = 30 * 60;

    public static final String BACKEND_SERVICE_NAME = "music";

    public static final String HELLO_TUNE_ENABLED = "hellotuneEnabled";
    public static final String AD_ENABLE = "adEnabled";
    public static final String PODCAST_ENABLED = "podcastEnabled";
}
