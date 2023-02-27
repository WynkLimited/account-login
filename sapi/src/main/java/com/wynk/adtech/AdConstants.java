package com.wynk.adtech;

import com.wynk.constants.MusicConstants;

public class AdConstants {

	public static final String NATIVE_REFRESH_KEY = "native_refresh";
	public static final String CACHE_PURGE_KEY = "cachePurgeInterval";
	public static final String PREROLL_REFRESH_KEY = "preroll_refresh";
	public static final String PLAY_THRESH_KEY = "play_threshold";
	public static final String PREFETCH_COUNT_KEY = "prefetchCountThreshold";
	public static final String DEFAULT_LOGO_KEY = "defaultLogoImageUrl";
	public static final String DEFAULT_CROUSAL_KEY = "defaultCarouselImageUrl";
	public static final String DEFAULT_AD_TITLE_KEY = "defaultAdTitle";
	public static final String DEFAULT_AD_MESS_KEY = "defaultAdMessage";
	public static final String TARGETING_REFRESH_KEY = "targeting_refresh";
	public static final String PREROLL_PLAY_THRES_KEY = "preroll_play_threshold";
	public static final String REMOVE_ADS_URL_KEY = "removeAdsUrl";
	public static final String SHOW_BANNER_ON_LIST = "showBannerOnList";
	public static final String INTERSITIAL_RETRY_INT = "interstitialRetryInterval";
	public static final String INTERSITIAL_AUTOCLOSE_INT = "interstitialAutoCloseInterval";

    public static final String INTERSTITIAL_ANDROID_BUILD = "interstitial_android_build";
	public static final String INTERSTITIAL_IOS_BUILD = "interstitial_ios_build";

	public static final String PREROLL_AD_SLOTS = "preroll_ad_slots";
	public static final String PREROLL_AD_SLOTS_ANDROID_BUILD = "preroll_ad_slots_android_build";
	public static final String PREROLL_AD_SLOTS_IOS_BUILD = "preroll_ad_slots_ios_build";

	public static final String BANNER_DIMENSIONS_ANDROID_BUILD = "banner_dimensions_android_build";
	public static final String BANNER_DIMENSIONS_IOS_BUILD = "banner_dimensions_ios_build";

	public static final int PREROLL_REFRESH_INTERVAL 			= 5;
	public static final int PREROLL_REFRESH_INTERVAL_PAID_BUG 	=  Integer.MAX_VALUE;

	public static final String PREROLL_REFRESH_INTERVAL_UNIT 	= "songs";
	public static final int NATIVE_REFRESH_INTERVAL 			= 1 * 60 * 1000;
	public static final String NATIVE_REFRESH_INTERVAL_UNIT 	= "minutes";

	public static final int SONG_PLAY_THRESHOLD 	= 10000;
	public static final int PREFETCH_COUNT_THRESHOLD = 2;

	public static final String WHY_ADS 	= "http://whyads.com";
	public static final int TARGETING_REFRESH 	= 15 * 60 * 1000;
	public static final int IOS_TARGETING_REFRESH 	= 5 * 60 * 1000;

	public static final int PREROLL_PLAY_THRESHOLD 	= 10000;

	public static final String LOGO_IMG_URL = "https://d2n2xdxvkri1jk.cloudfront.net/adtech/prod/default_image/logo.png";
	public static final String BANNER_IMG_URL =  "https://d2n2xdxvkri1jk.cloudfront.net/adtech/prod/default_image/ad_default_on_player.png";

	public static final int CACHE_PURGE_INTERVAL = 3 * MusicConstants.DAY;

	public static final String FREQUENCY = "frequency";
	public static final String ad_notify_timer = "ad_notify_timer";
	public static final String min_streamline_threshold= "min_streamline_threshold";
	public static final String play_trigger_delay = "play_trigger_delay";
	public static final String delay_after_triggers = "delay_after_triggers";
	public static final String triggers = "triggers";
	public static final String interstitial = "interstitial";
}