package com.wynk.adtech;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.wynk.constants.MusicConstants;
import com.wynk.db.MongoDBManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Service
public class AdConfigService {

	private static final Logger logger = LoggerFactory.getLogger(AdConfigService.class.getCanonicalName());

	@Autowired
	private MongoDBManager mongoMusicDBManager;

	private static final int AD_CONFIG_ID = 4;

	private static Set<String> blacklistedAdUnitsForIos;

	static {
		blacklistedAdUnitsForIos = new HashSet<>();
		blacklistedAdUnitsForIos.add("WYNK_PREROLL");
		blacklistedAdUnitsForIos.add("WYNK_PREROLL_PREMIUM");
	}

	public static JSONObject AD_CONFIG = new JSONObject();

	private final ConcurrentMap<Object, Object> adConfig = new ConcurrentHashMap<Object, Object>();
	private final ConcurrentMap<Object, Object> adSettings = new ConcurrentHashMap<Object, Object>();
	private final ConcurrentMap<Object, Object> interstitialAdSettings = new ConcurrentHashMap<>();
	private final ConcurrentMap<Object, Object> interstitialMeta = new ConcurrentHashMap<>();

	private final ConcurrentMap<Object, Object> prerollAdSlots = new ConcurrentHashMap<>();
	private final ConcurrentMap<Object, Object> prerollAdSlotsBuild = new ConcurrentHashMap<>();

	private static JSONObject ADCONFIG = new JSONObject();

	private static JSONObject ADSETTINGS = new JSONObject();
	private static JSONObject ADSETTINGS_ANDROID = new JSONObject();
	private static JSONObject ADSETTINGS_IOS = new JSONObject();
	private static JSONObject ADSETTINGS_IOS_FROM_BUILD_1069 = new JSONObject();

	private static JSONObject INTERSTITIALSETTING = new JSONObject();

	private static JSONObject INTERSTITIALMETA = new JSONObject();

	private static JSONObject PREROLLADSLOTS = new JSONObject();

	private static JSONObject PREROLLADSLOTSBUILD = new JSONObject();

	@PostConstruct
	public void init() throws ParseException {
		refreshConfigInfo();
	}

	@Scheduled(fixedDelay = 1000 * 60 * 60)
	public void refreshConfigInfo() throws ParseException {

		logger.info("Refreshing  config Information");
		Map<String, Object> query = new HashMap<>();
		query.put("confId", AD_CONFIG_ID);
		DBObject dbConf = mongoMusicDBManager.getObject(MusicConstants.MUSICAPP_CONFIG_COLLECTION, query);
		String configStr = JSON.serialize(dbConf);
        JSONObject config = (JSONObject) JSONValue.parseWithException(configStr);
		logger.info("map update with id " + config.get("confId"));

		adConfig.clear();
		adSettings.clear();
		interstitialAdSettings.clear();
		interstitialMeta.clear();
		prerollAdSlots.clear();
		prerollAdSlotsBuild.clear();

		adConfig.put(AdConstants.PLAY_THRESH_KEY, (double) config.get(AdConstants.PLAY_THRESH_KEY));
		adConfig.put(AdConstants.PREFETCH_COUNT_KEY, (double) config.get(AdConstants.PREFETCH_COUNT_KEY));
		adConfig.put(AdConstants.DEFAULT_LOGO_KEY, (String) config.get(AdConstants.DEFAULT_LOGO_KEY));
		adConfig.put(AdConstants.DEFAULT_CROUSAL_KEY, (String) config.get(AdConstants.DEFAULT_CROUSAL_KEY));
		adConfig.put(AdConstants.DEFAULT_AD_TITLE_KEY, (String) config.get(AdConstants.DEFAULT_AD_TITLE_KEY));
		adConfig.put(AdConstants.DEFAULT_AD_MESS_KEY, (String) config.get(AdConstants.DEFAULT_AD_MESS_KEY));
		adConfig.put(AdConstants.TARGETING_REFRESH_KEY, (double) config.get(AdConstants.TARGETING_REFRESH_KEY));
		adConfig.put(AdConstants.PREROLL_PLAY_THRES_KEY, (double) config.get(AdConstants.PREROLL_PLAY_THRES_KEY));
		adConfig.put(AdConstants.PREROLL_REFRESH_KEY, (double) config.get(AdConstants.PREROLL_REFRESH_KEY));
		adConfig.put(AdConstants.NATIVE_REFRESH_KEY, (double) config.get(AdConstants.NATIVE_REFRESH_KEY));
		adConfig.put(AdConstants.CACHE_PURGE_KEY, (double) config.get(AdConstants.CACHE_PURGE_KEY));
		adConfig.put(AdConstants.SHOW_BANNER_ON_LIST, (boolean) config.get(AdConstants.SHOW_BANNER_ON_LIST));
		adConfig.put(AdConstants.INTERSITIAL_RETRY_INT, (double) config.get(AdConstants.INTERSITIAL_RETRY_INT));
		adConfig.put(AdConstants.INTERSITIAL_AUTOCLOSE_INT, (double) config.get(AdConstants.INTERSITIAL_AUTOCLOSE_INT));

		interstitialAdSettings.put(AdConstants.FREQUENCY,(double) config.get(AdConstants.FREQUENCY));
		interstitialAdSettings.put(AdConstants.triggers,(ArrayList) config.get(AdConstants.triggers));
		interstitialAdSettings.put(AdConstants.delay_after_triggers,(double) config.get(AdConstants.delay_after_triggers));
		interstitialAdSettings.put(AdConstants.play_trigger_delay,(double) config.get(AdConstants.play_trigger_delay));
		interstitialAdSettings.put(AdConstants.min_streamline_threshold,(double) config.get(AdConstants.min_streamline_threshold));
		interstitialAdSettings.put(AdConstants.ad_notify_timer,(double) config.get(AdConstants.ad_notify_timer));

		interstitialMeta.put(AdConstants.INTERSTITIAL_ANDROID_BUILD, (double)config.get(AdConstants.INTERSTITIAL_ANDROID_BUILD));
		interstitialMeta.put(AdConstants.INTERSTITIAL_IOS_BUILD, (double)config.get(AdConstants.INTERSTITIAL_IOS_BUILD));

		prerollAdSlots.put(AdConstants.PREROLL_AD_SLOTS, (ArrayList) config.get(AdConstants.PREROLL_AD_SLOTS));

		prerollAdSlotsBuild.put(AdConstants.PREROLL_AD_SLOTS_ANDROID_BUILD, (double)config.get(AdConstants.PREROLL_AD_SLOTS_ANDROID_BUILD));
		prerollAdSlotsBuild.put(AdConstants.PREROLL_AD_SLOTS_IOS_BUILD, (double)config.get(AdConstants.PREROLL_AD_SLOTS_IOS_BUILD));

		setConfig();
		setInterstitialConfig();
		setInterstitialMeta();

		setPrerollAdSlots();
		setPrerollAdSlotsBuild();

		adSettings.put("adunitids", config.get("adunitids"));
		adSettings.put("adtemplateids", config.get("adtemplateids"));
		setSettings();
	}

	private void setConfig() {
		double play_threshold = (double) adConfig.get(AdConstants.PLAY_THRESH_KEY);
		double prefetch_count = (double) adConfig.get(AdConstants.PREFETCH_COUNT_KEY);
		double targeting_refresh = (double)adConfig.get(AdConstants.TARGETING_REFRESH_KEY);
		double preroll_play_thres = (double)adConfig.get(AdConstants.PREROLL_PLAY_THRES_KEY);
		double preroll_refresh = (double)adConfig.get(AdConstants.PREROLL_REFRESH_KEY);
		double native_refresh = (double)adConfig.get(AdConstants.NATIVE_REFRESH_KEY);
		double cache_purge = (double)adConfig.get(AdConstants.CACHE_PURGE_KEY);
		double intersitialRetry = (double)adConfig.get(AdConstants.INTERSITIAL_RETRY_INT);
		double intersitialAutoClose = (double)adConfig.get(AdConstants.INTERSITIAL_AUTOCLOSE_INT);
		boolean show_banner_on_list = (boolean) adConfig.get(AdConstants.SHOW_BANNER_ON_LIST);

		ADCONFIG.put(AdConstants.PLAY_THRESH_KEY, (int)play_threshold);
		ADCONFIG.put(AdConstants.PREFETCH_COUNT_KEY, (int)prefetch_count);
		ADCONFIG.put(AdConstants.DEFAULT_LOGO_KEY, adConfig.get(AdConstants.DEFAULT_LOGO_KEY));
		ADCONFIG.put(AdConstants.DEFAULT_CROUSAL_KEY, adConfig.get(AdConstants.DEFAULT_CROUSAL_KEY));
		ADCONFIG.put(AdConstants.DEFAULT_AD_TITLE_KEY, adConfig.get(AdConstants.DEFAULT_AD_TITLE_KEY));
		ADCONFIG.put(AdConstants.DEFAULT_AD_MESS_KEY, adConfig.get(AdConstants.DEFAULT_AD_MESS_KEY));
		ADCONFIG.put(AdConstants.TARGETING_REFRESH_KEY,  (int)targeting_refresh);
		ADCONFIG.put(AdConstants.PREROLL_PLAY_THRES_KEY,  (int)preroll_play_thres);
		ADCONFIG.put(AdConstants.PREROLL_REFRESH_KEY,  (int)preroll_refresh);
		ADCONFIG.put(AdConstants.NATIVE_REFRESH_KEY, (int)native_refresh);
		ADCONFIG.put(AdConstants.CACHE_PURGE_KEY,  (int)cache_purge);
		ADCONFIG.put(AdConstants.INTERSITIAL_RETRY_INT,  (int)intersitialRetry);
		ADCONFIG.put(AdConstants.INTERSITIAL_AUTOCLOSE_INT,  (int)intersitialAutoClose);
		ADCONFIG.put(AdConstants.SHOW_BANNER_ON_LIST,show_banner_on_list);
	}

	private void setInterstitialConfig() {
		double delay_after_triggers = (double) interstitialAdSettings.get(AdConstants.delay_after_triggers);
		double play_trigger_delay = (double) interstitialAdSettings.get(AdConstants.play_trigger_delay);
		double min_streamline_threshold = (double)interstitialAdSettings.get(AdConstants.min_streamline_threshold);
		double ad_notify_timer = (double)interstitialAdSettings.get(AdConstants.ad_notify_timer);
		ArrayList triggers = (ArrayList)interstitialAdSettings.get(AdConstants.triggers);
		double frequency = (double)interstitialAdSettings.get(AdConstants.FREQUENCY);

		INTERSTITIALSETTING.put(AdConstants.delay_after_triggers, (int)delay_after_triggers);
		INTERSTITIALSETTING.put(AdConstants.play_trigger_delay, (int)play_trigger_delay);
		INTERSTITIALSETTING.put(AdConstants.min_streamline_threshold,(int) min_streamline_threshold);
		INTERSTITIALSETTING.put(AdConstants.ad_notify_timer, (int)ad_notify_timer);
		INTERSTITIALSETTING.put(AdConstants.FREQUENCY,(int) frequency);
		INTERSTITIALSETTING.put(AdConstants.triggers, triggers);
	}

	private void setInterstitialMeta() {
		double interstitialAndroidBuild = (double)interstitialMeta.get(AdConstants.INTERSTITIAL_ANDROID_BUILD);
		double interstitialIosBuild = (double)interstitialMeta.get(AdConstants.INTERSTITIAL_IOS_BUILD);
		INTERSTITIALMETA.put(AdConstants.INTERSTITIAL_ANDROID_BUILD, (int)interstitialAndroidBuild);
		INTERSTITIALMETA.put(AdConstants.INTERSTITIAL_IOS_BUILD, (int)interstitialIosBuild);
	}

	private void setPrerollAdSlots() {
		ArrayList prerollAdSlot = (ArrayList)prerollAdSlots.get(AdConstants.PREROLL_AD_SLOTS);
		PREROLLADSLOTS.put(AdConstants.PREROLL_AD_SLOTS, prerollAdSlot);
	}

	private void setPrerollAdSlotsBuild() {
		double prerollAdSlotsAndroidBuild = (double)prerollAdSlotsBuild.get(AdConstants.PREROLL_AD_SLOTS_ANDROID_BUILD);
		double prerollAdSlotsIosBuild = (double)prerollAdSlotsBuild.get(AdConstants.PREROLL_AD_SLOTS_IOS_BUILD);
		PREROLLADSLOTSBUILD.put(AdConstants.PREROLL_AD_SLOTS_ANDROID_BUILD, (int)prerollAdSlotsAndroidBuild);
		PREROLLADSLOTSBUILD.put(AdConstants.PREROLL_AD_SLOTS_IOS_BUILD, (int)prerollAdSlotsIosBuild);
	}

	public static JSONObject getInterstitialMeta() {
		return INTERSTITIALMETA;
	}

	public static JSONObject getConfig() {
		return ADCONFIG;
	}

	public static JSONObject getinterstitialAdSettings(){
		return INTERSTITIALSETTING;
	}

	public static JSONObject getPrerollAdSlots(){
		return PREROLLADSLOTS;
	}

	public static JSONObject getPrerollAdSlotsBuild(){
		return PREROLLADSLOTSBUILD;
	}

	public static JSONObject cloneAdConfig() {
		JSONObject ads = AdConfigService.getConfig();
		JSONObject obj = new JSONObject();
		for (Object key : ads.keySet()) {
			obj.put((String)key, ads.get((String)key));
		}
		return obj;
	}

	public static JSONObject cloneInterstitialSettings() {
		JSONObject interstitialSetting = AdConfigService.getinterstitialAdSettings();
		JSONObject obj = new JSONObject();
		for (Object key : interstitialSetting.keySet()) {
			obj.put((String)key, interstitialSetting.get((String)key));
		}
		return obj;
	}

	private void setSettings() {
		ADSETTINGS.put("adunitids", adSettings.get("adunitids"));
		ADSETTINGS.put("adtemplateids", adSettings.get("adtemplateids"));

		ADSETTINGS_ANDROID.put("adunitids", adSettings.get("adunitids"));
		ADSETTINGS_ANDROID.put("adtemplateids", adSettings.get("adtemplateids"));

		ADSETTINGS_IOS_FROM_BUILD_1069.put("adunitids", adSettings.get("adunitids"));
		ADSETTINGS_IOS_FROM_BUILD_1069.put("adtemplateids", adSettings.get("adtemplateids"));

		JSONArray updatedAdUnitsIds = new JSONArray();
		for (Object obj : (JSONArray) adSettings.get("adunitids")) {
			String adUnit = (String)(obj);
			if (adUnit != null) {
				String adUnitName = adUnit.split(",")[0];
				if (!blacklistedAdUnitsForIos.contains(adUnitName))
					updatedAdUnitsIds.add(adUnit);
			}
		}
		ADSETTINGS_IOS.put("adunitids", updatedAdUnitsIds);
		ADSETTINGS_IOS.put("adtemplateids", adSettings.get("adtemplateids"));
	}

	public static JSONObject getSettings() {
		return ADSETTINGS;
	}

	public static JSONObject getAdSettingsAndroid() {
		return ADSETTINGS_ANDROID;
	}

	public static JSONObject getAdSettingsIos() {
		return ADSETTINGS_IOS;
	}

	public static JSONObject getAdsettingsIosFromBuild1069() {
		return ADSETTINGS_IOS_FROM_BUILD_1069;
	}

}