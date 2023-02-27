package com.wynk.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MusicBuildConstants {
	// Build Number specific support configuration
    public static final int NOT_SUPPORTED_ON_OS = -1;
    public static final int WINDOWS_INITIAL_BUILD_NUMBER = 1;
    public static final int ANDROID_INITIAL_BUILD_NUMBER = 1;
    public static final int IOS_ETAG_SUPPORTED = 64;
    
    // HLS support
    public static final int IOS_BUILD_NUMBER_WITH_HLS_SUPPORT = 17;
    public static final int ANDROID_BUILD_NUMBER_WITH_HLS_SUPPORT = 22;
    public static final int ANDROID_BUILD_NUMBER_WITH_HLS_SUPPORT_RENTING = 25;
    // Adaptive HLS support
    public static final int ANDROID_ADAPTIVE_HLS_BE_CONTROL_SUPPORTED = 47;
    public static final int ANDROID_SERVE_ONLY_ADAPTIVE_BITRATES = 48;
    public static final int IOS_SERVE_ONLY_ADAPTIVE_BITRATES = 80;
    public static final int IOS_ADAPTIVE_HLS_BE_CONTROL_SUPPORTED = 52;
    
    public static final int IOS_BUILD_NUMBER_WITH_DUP_STATS_SUPPORT = 24;
    public static final int ANDROID_BUILD_NUMBER_WITH_DUP_STATS_SUPPORT = 0;
    
    public static final int IOS_BUILD_NUMBER_WITH_GEO_RESTRICTION_SUPPORT = 21;
    public static final int ANDROID_BUILD_NUMBER_WITH_GEO_RESTRICTION_SUPPORT = 25;
    
    public static final int IOS_SUPPORT_FOR_EXTERNAL_WEBVIEW = 35;
    public static final int ANDROID_SUPPORT_FOR_EXTERNAL_WEBVIEW = 37;
    
    public static final int ANDROID_IS_AUTO_PLAYLST_DISABLED = 16;
    public static final int IOS_IS_AUTO_PLAYLST_DISABLED = 14;
    
    public static final int ANDROID_ADHM_BUILD_NUMBER = 39;
    public static final int IOS_ADHM_BUILD_NUMBER = 35;
    
    public static final int ANDROID_ONDEVICE_BUILD_NUMBER = 41;
    
    public static final int IOS_NAVIGATION_SUPPORT_BUILD_NUMBER = 45;
    public static final int ANDROID_NAVIGATION_BUILD_NUMBER = 46;
    
    public static final int ANDROID_AD_TECH_BUILD_NUMBER = 58;
    
    public static final int ANDROID_PAID_BUG = 65;
    public static final int IOS_PAID_BUG = 76;

    public static final int IOS_AD_TECH_BUILD_NUMBER = 62;

    public static final int ANDROID_CLIENT_VERIFICATION_SUPPORT_BUILD = 29;
    public static final int IOS_CLIENT_VERIFICATION_SUPPORT_BUILD = 24;
    
    public static final int ANDROID_2G_OPTIMIZATION_SUPPORTED = 29;
    
    public static final List<Integer> oemBuildList = new ArrayList<>(Arrays.asList(34, 35));
    
    public static final String IOS_NEW_LANGS_SUPPORTED = "1.0.1";
	public static final String ANDROID_NEW_LANGS_SUPPORTED = "1.0.2";
	
	public static final int ANDROID_OFFERS_PACK_SUPPORTED = 12;
    public static final int IOS_OFFERS_PACK_SUPPORTED = 8;
    
	public static final int ANDROID_RADIO_SUPPORT_BUILD_NUMBER = 25;
	
	public static final int ANDROID_TRITON_SUPPORT_BUILD_NUMBER = 67;
	public static final int IOS_TRITON_SUPPORT_BUILD_NUMBER = 76;

	
    public static final int ANDROID_FEATURED_DISABLE_SUPPORT_BUILD_NUMBER = 26;
    public static final int IOS_FEATURED_DISABLE_SUPPORT_BUILD_NUMBER = 24;
    
    public static final int ANDROID_ACCOUNT_CONFIG_POPUP_SUPPORTED = 46;
    public static final int IOS_ACCOUNT_CONFIG_POPUP_SUPPORTED = 49;
    
    public static final int ANDROID_RICH_NOTIFICATION_BUILD_NUMBER = 24;
    public static final int ANDROID_SUPPORT_FOR_PACKAGE_DEEP_LINKING = 41;
    public static final int IOS_SUPPORT_FOR_PACKAGE_DEEP_LINKING = 44;
    public static final int IOS_SUPPORT_FOR_RADIO_NOTIFICATIONS = 14;
    
    public static final int IOS_SUPPORT_FOR_REGISTRATION_POPUP = 55;

    public static final int ANDROID_USER_SEPARATED_NUMBER = 54;
    public static final int IOS_USER_SEPARATED_NUMBER = 50;

    public static final int ANDROID_INTL_ROAMING = 69;
    public static final int IOS_INTL_ROAMING = 80;

    public static final int ANDROID_CLOUDFRONT_COOKIES_SUPPORT = 82;
    public static final int IOS_CLOUDFRONT_COOKIES_SUPPORT = 99;
    public static final int ANDROID_SUBSCRIPTION_INTENT = 81;
    public static final int IOS_SUBSCRIPTION_INTENT = 90;


    public static final int ANDROID_CHANGE_MOBILE_SUPPORT = 74;

    public static final int ANDROID_NATIVE_PAYMENT_SUPPORT = 92;
    
    public static final int PRE_CACHING_CRASH_BUILD_IOS = 100;
    public static final int ANDROID_NEW_SUBSCRIPTION_SUPPORT = 100;
    public static final int ANDROID_REDESIGN_SUBSCRIPTION_SUPPORT = 109;
    public static final int IOS_NEW_SUBSCRIPTION_SUPPORT = 106;
    public static final int IOS_REDESIGN_SUBSCRIPTION_SUPPORT = 107;

    public static final int ANDROID_HA_LANG_SUPPORT = 108;
    public static final int ANDROID_IS_APP_SHORTCUT_SUPPORTED = 117;

    public static final int SRILANKA_SUPPORTED_ANDROID_BUILD = 178;
    public static final int MUSIC_SRI_LANKA_BUILD=178;

    public static final int HELLOTUNE_ANDROID_BUILD = 204;
    public static final int HELLOTUNE_IOS_BUILD=153;

    public static final int ANDROID_ENCRYPTION_BY_DID_SUPPORTED=346;

    public static final int ANDROID_SUPPORTED_BUILD_NO_SINGAPORE_ONWARDS = 730;
    public static final int IOS_SUPPORTED_BUILD_NO_SINGAPORE_ONWARDS = 2220;
}
