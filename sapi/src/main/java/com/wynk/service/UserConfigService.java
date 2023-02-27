package com.wynk.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import com.wynk.config.MusicConfig;
import com.wynk.service.dto.IntlRoamingConfigMetaData;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.wynk.config.MongoDBConfig;
import com.wynk.constants.JsonKeyNames;
import com.wynk.constants.MusicConstants;
import com.wynk.db.MongoDBManager;

import javax.annotation.PostConstruct;

@Service
public class UserConfigService {
	
	private static final Logger logger               = LoggerFactory.getLogger(UserConfigService.class.getCanonicalName());
	
	 @Autowired
	 private MongoDBManager          mongoMusicDBManager;

	 @Autowired
	MusicConfig musicConfig;
	
	 private final ConcurrentMap<Object, Object> hooksConfig = new ConcurrentHashMap<Object, Object>();
    
    private final ConcurrentMap<Object, Object> OQSConfig = new ConcurrentHashMap<Object, Object>();
    
    public static JSONObject OQS_CONFIG = new JSONObject();
    
    public static JSONObject HOOKS_CONFIG = new JSONObject();

	public static IntlRoamingConfigMetaData INTL_ROAMING_CONFIG = new IntlRoamingConfigMetaData();

	@PostConstruct
    public void init(){
		try {
			refreshConfigInfo();
		} catch (ParseException e) {
			logger.error("Error in initialization of UserConfigService");
			e.printStackTrace();
		}
	}
    
    private final ConcurrentMap<Object, Object> thirdPartyAppConf = new ConcurrentHashMap<Object, Object>();
    
    public static JSONObject THIRD_PARTY_CONF = new JSONObject();

    
    public JSONObject getHooksUserConfig(){
    	
		return HOOKS_CONFIG;
    	
    }
    
    public JSONObject getThirdPartyAppConfig() {
    	return THIRD_PARTY_CONF;
    }
    
    public void setHooksUserConfig(){
    	
    	JSONObject hooksHomePageHeader = new JSONObject();
		hooksHomePageHeader.put(JsonKeyNames.URL, getHooksConfigKey(JsonKeyNames.HOOKS_HOMEPAGE_HEADER));
		hooksHomePageHeader.put(JsonKeyNames.HEIGHT, 40);
		HOOKS_CONFIG.put(JsonKeyNames.HOOKS_HOMEPAGE_HEADER, hooksHomePageHeader);
		
		JSONObject hooksRailIcon = new JSONObject();
		hooksRailIcon.put(JsonKeyNames.URL, getHooksConfigKey(JsonKeyNames.HOOKS_RAIL_ICON));
		HOOKS_CONFIG.put(JsonKeyNames.HOOKS_RAIL_ICON, hooksRailIcon);
		
		JSONObject hooksPlayIcon = new JSONObject();
		hooksPlayIcon.put(JsonKeyNames.URL, getHooksConfigKey(JsonKeyNames.HOOKS_PLAY_ICON));
		HOOKS_CONFIG.put(JsonKeyNames.HOOKS_PLAY_ICON, hooksPlayIcon);
		
		JSONObject hooksNotifSound = new JSONObject();
		hooksNotifSound.put(JsonKeyNames.URL, getHooksConfigKey(JsonKeyNames.HOOKS_NOTIF_SOUND));
		HOOKS_CONFIG.put(JsonKeyNames.HOOKS_NOTIF_SOUND, hooksNotifSound);
		
		JSONObject hooksMpHeader = new JSONObject();
		hooksMpHeader.put(JsonKeyNames.URL, getHooksConfigKey(JsonKeyNames.HOOKS_MP_HEADER));
		HOOKS_CONFIG.put(JsonKeyNames.HOOKS_MP_HEADER, hooksMpHeader);
		
		JSONObject hooksNavDrawer = new JSONObject();
		hooksNavDrawer.put(JsonKeyNames.URL, getHooksConfigKey(JsonKeyNames.HOOKS_NAV_DRAWER));
		hooksNavDrawer.put(JsonKeyNames.HOOKS_NAV_DRAWER_ACTICE_COLOR, getHooksConfigKey(JsonKeyNames.HOOKS_NAV_DRAWER_ACTICE_COLOR));
		hooksNavDrawer.put(JsonKeyNames.HOOKS_NAV_DRAWER_INACTICE_COLOR,getHooksConfigKey(JsonKeyNames.HOOKS_NAV_DRAWER_INACTICE_COLOR));
		HOOKS_CONFIG.put(JsonKeyNames.HOOKS_NAV_DRAWER, hooksNavDrawer);
		
		HOOKS_CONFIG.put(JsonKeyNames.HOOKS_PACKAGE,getHooksConfigKey(JsonKeyNames.HOOKS_PACKAGE));
		JSONObject hooksPlayerBG = new JSONObject();
		hooksPlayerBG.put(JsonKeyNames.URL, getHooksConfigKey(JsonKeyNames.HOOKS_PLAYER_BG));
		HOOKS_CONFIG.put(JsonKeyNames.HOOKS_PLAYER_BG, hooksPlayerBG);
    	
    }
    
    public JSONObject getOfflineQueueSortingConfig(){
    	
		return OQS_CONFIG;
    }
    
    public void setThirdPartyAppConf() {
    	THIRD_PARTY_CONF.put("iosPkgList", thirdPartyAppConf.get("iosPkgList"));
    	THIRD_PARTY_CONF.put("androidPkgList", thirdPartyAppConf.get("androidPkgList"));
    }
    
    public void setOfflineQueueSortingConfig(){
    	boolean featureAvailable = (boolean) getOQSConfigKey(JsonKeyNames.FEATURE_AVAILABLE);
		OQS_CONFIG.put(JsonKeyNames.FEATURE_AVAILABLE, featureAvailable);
		
		logger.info("feature available is "+featureAvailable);
		
		int start_time = (int) getOQSConfigKey(JsonKeyNames.START_WAIT_TIME);
		OQS_CONFIG.put(JsonKeyNames.START_WAIT_TIME	,start_time) ;
		
		int long_playback_time = (int) getOQSConfigKey(JsonKeyNames.SONG_PLAYBACK_TIME);
		OQS_CONFIG.put(JsonKeyNames.SONG_PLAYBACK_TIME	,long_playback_time) ;
		
		int offline_threshold = (int) getOQSConfigKey(JsonKeyNames.OFFLINE_SONG_THRESHOLD);
		OQS_CONFIG.put(JsonKeyNames.OFFLINE_SONG_THRESHOLD	,offline_threshold) ;
    }

    public void setIntlRoamingConfig(IntlRoamingConfigMetaData intlRoamingConfig){
    	if (CollectionUtils.isNotEmpty(intlRoamingConfig.getIndianCarriersList())) {
			INTL_ROAMING_CONFIG.setIndianCarriersList(intlRoamingConfig.getIndianCarriersList());
		}
		if (CollectionUtils.isNotEmpty(intlRoamingConfig.getIndianMCCList())){
    		INTL_ROAMING_CONFIG.setIndianMCCList(intlRoamingConfig.getIndianMCCList());
		}
		if (CollectionUtils.isNotEmpty(intlRoamingConfig.getIntlRoamingBlacklist())){
			INTL_ROAMING_CONFIG.setIntlRoamingBlacklist(intlRoamingConfig.getIntlRoamingBlacklist());
		}
		if (intlRoamingConfig.getIntlRoamingQuota() != null){
			INTL_ROAMING_CONFIG.setIntlRoamingQuota(intlRoamingConfig.getIntlRoamingQuota());
		}else {
			INTL_ROAMING_CONFIG.setIntlRoamingQuota(musicConfig.getIntlRoamingQuota());
		}
		if (intlRoamingConfig.getIntlRoamingCycle() != null){
			INTL_ROAMING_CONFIG.setIntlRoamingCycle(intlRoamingConfig.getIntlRoamingCycle());
		}else {
			INTL_ROAMING_CONFIG.setIntlRoamingCycle(musicConfig.getIntlRoamingCycle());
		}
		if (intlRoamingConfig.getIntlRoamingExpiryNotif() != null){
			INTL_ROAMING_CONFIG.setIntlRoamingExpiryNotif(intlRoamingConfig.getIntlRoamingExpiryNotif());
		}else {
			INTL_ROAMING_CONFIG.setIntlRoamingExpiryNotif(musicConfig.getIntlRoamingExpiryNotif());
		}
		if (intlRoamingConfig.getIntlRoamingAndroidVersionsToUpgrade() != null){
			INTL_ROAMING_CONFIG.setIntlRoamingAndroidVersionsToUpgrade(
					intlRoamingConfig.getIntlRoamingAndroidVersionsToUpgrade());
		}else {
			INTL_ROAMING_CONFIG.setIntlRoamingAndroidVersionsToUpgrade(
					musicConfig.getIntlRoamingAndroidVersionsToUpgrade());
		}
		if (intlRoamingConfig.getIntlRoamingIosVersionsToUpgrade() != null){
			INTL_ROAMING_CONFIG.setIntlRoamingIosVersionsToUpgrade(
					intlRoamingConfig.getIntlRoamingIosVersionsToUpgrade());
		}else {
			INTL_ROAMING_CONFIG.setIntlRoamingIosVersionsToUpgrade(musicConfig.getIntlRoamingIosVersionsToUpgrade());
		}
		if (intlRoamingConfig.getIntlRoamingEnabled() != null){
			INTL_ROAMING_CONFIG.setIntlRoamingEnabled(intlRoamingConfig.getIntlRoamingEnabled());
		}else {
			INTL_ROAMING_CONFIG.setIntlRoamingEnabled(musicConfig.isIntlRoamingEnabled());
		}
		if (intlRoamingConfig.getIntlRoamingFreeTierCheck() != null){
			INTL_ROAMING_CONFIG.setIntlRoamingFreeTierCheck(intlRoamingConfig.getIntlRoamingFreeTierCheck());
		}else {
			INTL_ROAMING_CONFIG.setIntlRoamingFreeTierCheck(musicConfig.isIntlRoamingFreeTierCheck());
		}
	}


    public Object getOQSConfigKey(String key) {
    	return OQSConfig.get(key);
    }
    
    
    public   Object getHooksConfigKey(String key) {
   	 return hooksConfig.get(key);
	}
    
    @Scheduled(fixedDelay = 1000 * 60 * 2)
	public void refreshConfigInfo() throws ParseException {
		logger.info("Refreshing  config Information");
			Map<String, Object> query = new HashMap<>();
			query.put("confId", 01);
			DBObject Config = mongoMusicDBManager.getObject(MusicConstants.MUSICAPP_CONFIG_COLLECTION, query);
			
			logger.info("map update with id "+ Config.get("confId"));

			hooksConfig.clear();
//			hooksConfig.put(JsonKeyNames.HOOKS_ENABLED, (boolean) Config.get("hooksenabled"));
			hooksConfig.put(JsonKeyNames.HOOKS_ENABLED, false); //TODO: Only for preprod
			hooksConfig.put(JsonKeyNames.HOOKS_HOMEPAGE_HEADER, (String) Config.get("homepageHeader"));
			//hooksConfig.put(JsonKeyNames.HOOKS_HOMEPAGE_HEADER_HEIGHT, (String) Config.get("homepageHeaderHeight"));
			hooksConfig.put(JsonKeyNames.HOOKS_MP_HEADER, (String) Config.get("mpHeader"));
			hooksConfig.put(JsonKeyNames.HOOKS_NAV_DRAWER_ACTICE_COLOR, (String) Config.get("activeColor"));
			hooksConfig.put(JsonKeyNames.HOOKS_NAV_DRAWER_INACTICE_COLOR, (String) Config.get("inactiveColor"));
			hooksConfig.put(JsonKeyNames.HOOKS_NAV_DRAWER, (String) Config.get("navDrawer"));
			hooksConfig.put(JsonKeyNames.HOOKS_NOTIF_SOUND, (String) Config.get("notificationTune"));
			hooksConfig.put(JsonKeyNames.HOOKS_PLAY_ICON, (String) Config.get("playIcon"));
			hooksConfig.put(JsonKeyNames.HOOKS_RAIL_ICON, (String) Config.get("railIcon"));
			hooksConfig.put(JsonKeyNames.HOOKS_PACKAGE, (String) Config.get("package"));
			hooksConfig.put(JsonKeyNames.HOOKS_PLAYER_BG,"http://wynk-music-cms.s3.amazonaws.com/Card_Mask%20Copy.jpg");

			setHooksUserConfig();
			
			Map<String, Object> query1 = new HashMap<>();
			query1.put("confId", 02);
			DBObject ConfigOSQ = mongoMusicDBManager.getObject(MusicConstants.MUSICAPP_CONFIG_COLLECTION, query1);
			
			logger.info("map update with id "+ ConfigOSQ.get("confId"));

			OQSConfig.clear();
			boolean featAvai = false;
			if(ConfigOSQ.get("featureAvailable").equals("true"))
				featAvai = true;
			 
			
			OQSConfig.put(JsonKeyNames.FEATURE_AVAILABLE, featAvai);
			int songPlayedCount = Integer.parseInt((String) ConfigOSQ.get("songPlayedCount"));
			OQSConfig.put(JsonKeyNames.START_WAIT_TIME, songPlayedCount);
			
			int songQualityThreshold = Integer.parseInt((String) ConfigOSQ.get("songQualityThreshold"));
			OQSConfig.put(JsonKeyNames.SONG_PLAYBACK_TIME, songQualityThreshold);
			
			int offlineSongThreshold = Integer.parseInt((String) ConfigOSQ.get("offlineSongThreshold"));
			OQSConfig.put(JsonKeyNames.OFFLINE_SONG_THRESHOLD, offlineSongThreshold);
			setOfflineQueueSortingConfig();
			
			try {
				Map<String, Object> query3 = new HashMap<>();
				query3.put("confId", 3);
				DBObject configIntlRoaming = mongoMusicDBManager.getObject(MusicConstants.MUSICAPP_CONFIG_COLLECTION, query3);

				if (configIntlRoaming != null) {
					logger.info("map update with id "+ configIntlRoaming.get("confId"));
					IntlRoamingConfigMetaData intlRoamingConfigMetaData = new IntlRoamingConfigMetaData();
					intlRoamingConfigMetaData.fromJson(configIntlRoaming.toString());
					setIntlRoamingConfig(intlRoamingConfigMetaData);
				}else {
					IntlRoamingConfigMetaData intlRoamingConfigMetaData = new IntlRoamingConfigMetaData();
					setIntlRoamingConfig(intlRoamingConfigMetaData);
				}
			}catch (Exception e){
				logger.error("Error in updating international roaming config from mongo db " + e.getMessage(), e);
			}
			
			refreshThirdPartyAppConf();
	}
    
    public void refreshThirdPartyAppConf() throws ParseException {

		Map<String, Object> thirdPartyAppConfQuery = new HashMap<>();
		thirdPartyAppConfQuery.put("confId", 5);
		DBObject thirdPartyAppDbObj = mongoMusicDBManager.getObject(MusicConstants.MUSICAPP_CONFIG_COLLECTION, thirdPartyAppConfQuery);
		String configStr = JSON.serialize(thirdPartyAppDbObj);
		JSONObject config = (JSONObject) JSONValue.parseWithException(configStr);
		
		thirdPartyAppConf.clear();
		if (config.containsKey("androidPkgList")) {
			JSONArray androidPkgList = new JSONArray();
			JSONArray pkgs = (JSONArray) config.get("androidPkgList");
			List<JSONObject> pkgsUnique = new ArrayList<>(pkgs);
			androidPkgList.addAll(pkgsUnique.stream().map(pkg -> (String)pkg.get("pkgName")).distinct()
					.collect(Collectors.toList()));
			thirdPartyAppConf.put("androidPkgList", androidPkgList);
		}
        if (config.containsKey("iosPkgList")) {
        	JSONArray iosPkgList = new JSONArray();
        	JSONArray pkgs = (JSONArray) config.get("iosPkgList");
        	for (Object pkg: pkgs) {
        		JSONObject pkgJson = (JSONObject)pkg;
        		iosPkgList.add(pkgJson.get("pkgName"));
        	}
            thirdPartyAppConf.put("iosPkgList", iosPkgList);
        }
        logger.info("thirdPartyAppConf" + thirdPartyAppConf);
        
		setThirdPartyAppConf();
    }
    
    public static void main(String[] args) {
    	
    	MongoDBConfig config = new MongoDBConfig();
    	config.setMongodbHost("10.1.1.225");
    	config.setMongodbPort(27017);
    	config.setMongoDBName("musicdb");
    	MongoDBManager mongoMusicDBManager = new MongoDBManager(config);
    	UserConfigService s = new UserConfigService();
    	s.mongoMusicDBManager = mongoMusicDBManager;
    	try {
			s.refreshThirdPartyAppConf();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(s.thirdPartyAppConf);
	}

}
