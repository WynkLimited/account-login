package com.wynk.adtech;

import com.wynk.config.MusicConfig;
import com.wynk.constants.MusicConstants;
import com.wynk.dto.MusicSubscriptionStatus;
import com.wynk.server.ChannelContext;
import com.wynk.utils.*;
import com.wynk.wcf.WCFApisUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
public class AdService {

	private static final Logger logger = LoggerFactory.getLogger(AdService.class);

	private PoolingHttpClientConnectionManager connectionManager;
	private CloseableHttpClient httpClient;

	private CassandraOperations userAdTechCassandraTemplate;

	@Autowired
	private MusicConfig musicConfig;

	@Autowired
	private WCFApisUtils wcfApisUtils;

	private static final  int   MAX_CONNECTIONS        		   	   = 400;
	private static final  int   SONG_PLAY_COUNT_DAYS               = -20;

	public AdService() {
		try {
			connectionManager = new PoolingHttpClientConnectionManager();
			RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(60000).setConnectTimeout(10000).setSocketTimeout(30000).build();
			connectionManager.setMaxTotal(MAX_CONNECTIONS);
			connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS);
			ConnectionKeepAliveStrategy keepAliveStrategy = new ConnectionKeepAliveStrategy() {

				@Override
				public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
					// Honor 'keep-alive' header
					HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
					while(it.hasNext()) {
						HeaderElement he = it.nextElement();
						String param = he.getName();
						String value = he.getValue();
						if(value != null && param.equalsIgnoreCase("timeout")) {
							try {
								return Long.parseLong(value) * 1000;
							}
							catch (NumberFormatException ignore) {
							}
						}
					}
					// otherwise keep alive for 30 seconds
					return 30 * 1000;
				}
			};

			httpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(config).setKeepAliveStrategy(keepAliveStrategy)
					.evictIdleConnections(30L, TimeUnit.SECONDS).build();
		}
		catch (Throwable th) {
			throw new RuntimeException(th);
		}
	}

	public boolean toAddAdSlot(AD_Slot ad_slot){

		// todo - uncomment when ios check is required
		if ((ChannelContext.getOS()!=null && ChannelContext.getOS().toLowerCase().contains("ios")) && (ChannelContext.getBuildnumber() != null && (ChannelContext.getBuildnumber() > ad_slot.getIosBuildNo())) )
			return  true;
		if (((ChannelContext.getOS()!=null && ChannelContext.getOS().toLowerCase().contains("android"))) && (ChannelContext.getBuildnumber() != null && (ChannelContext.getBuildnumber() > ad_slot.getAndroidBuildNo())) )
		    return  true;

		return false;
		}

	@SuppressWarnings("unchecked")
	public Optional<AD_Slot> findByUnitName(String unitId) {
		if (StringUtils.isEmpty(unitId)) {
			return Optional.empty();
		}
		JSONObject setting = AdConfigService.getSettings();
		List<AD_Creative_Template> adTemplateIds = new ArrayList<>();
		JSONArray templateIds = (JSONArray) setting.get("adtemplateids");
		for (Object templateId : templateIds) {
			adTemplateIds.add(AD_Creative_Template.fromDbConfig((String) templateId));
		}
		JSONArray unitIds = (JSONArray) setting.get("adunitids");
		if (unitIds == null) {
			return Optional.empty();
		}
		Stream<String> stream = unitIds.stream().map(String::valueOf);
		return stream.map(id -> AD_Slot.fromDbConfig(id, adTemplateIds))
				.filter(slot -> unitId.equals(slot.getName())).findFirst();
	}

	public JSONObject getConfig(String service,MusicSubscriptionStatus statusDto,String msisdnStr) {

		Map<String, String> abHeader = ABUtils.getABHeader(ChannelContext.getRequest());
		Boolean disablePreroll = Boolean.FALSE;
		Boolean disableAll = Boolean.FALSE;
		if(abHeader != null && abHeader.containsKey(musicConfig.getAdsDisableExperimentId())){
			String variant = abHeader.get(musicConfig.getAdsDisableExperimentId());
			if("1".equalsIgnoreCase(variant))
				disablePreroll = Boolean.TRUE;

			if("2".equalsIgnoreCase(variant))
				disableAll = Boolean.TRUE;
		}

		JSONObject ads = AdConfigService.cloneAdConfig();
		JSONObject adSettings = AdConfigService.getAdSettingsAndroid();
		if (StringUtils.isNotEmpty(ChannelContext.getOS()) && ChannelContext.getOS().toLowerCase().contains("ios")) {
			adSettings = (ChannelContext.getBuildnumber() != null && ChannelContext.getBuildnumber() >= 1069)
					? AdConfigService.getAdsettingsIosFromBuild1069() : AdConfigService.getAdSettingsIos();
		}
		JSONObject interstitialSettings = AdConfigService.cloneInterstitialSettings();

		boolean isPremiumAccountUser = Boolean.FALSE;
		if(StringUtils.isNotBlank(ChannelContext.getUser().getMsisdn())){
			isPremiumAccountUser = wcfApisUtils.isPaidUser(ChannelContext.getUser().getUserSubscription());
		}

		if(StringUtils.isNotEmpty(ChannelContext.getOS()) && ChannelContext.getOS().toLowerCase().contains("android") && ChannelContext.getBuildnumber() != null &&
				MusicBuildUtils.isSupportedByBuildNumber(ChannelContext.getBuildnumber(), (Integer) AdConfigService.getInterstitialMeta().get(AdConstants.INTERSTITIAL_ANDROID_BUILD)))
			ads.put("interstitial",interstitialSettings);

		if(StringUtils.isNotEmpty(ChannelContext.getOS()) && ChannelContext.getOS().toLowerCase().contains("ios") && ChannelContext.getBuildnumber() != null &&
				MusicBuildUtils.isSupportedByBuildNumber(ChannelContext.getBuildnumber(), (Integer) AdConfigService.getInterstitialMeta().get(AdConstants.INTERSTITIAL_IOS_BUILD)))
			ads.put("interstitial",interstitialSettings);

		JSONObject prerollAdSlots = AdConfigService.getPrerollAdSlots();
		if(StringUtils.isNotEmpty(ChannelContext.getOS()) && ChannelContext.getOS().toLowerCase().contains("android") && ChannelContext.getBuildnumber() != null &&
				MusicBuildUtils.isSupportedByBuildNumber(ChannelContext.getBuildnumber(), (Integer) AdConfigService.getPrerollAdSlotsBuild().get(AdConstants.PREROLL_AD_SLOTS_ANDROID_BUILD)))
			ads.put(AdConstants.PREROLL_AD_SLOTS, prerollAdSlots.get(AdConstants.PREROLL_AD_SLOTS));

		if(StringUtils.isNotEmpty(ChannelContext.getOS()) && ChannelContext.getOS().toLowerCase().contains("ios") && ChannelContext.getBuildnumber() != null &&
				MusicBuildUtils.isSupportedByBuildNumber(ChannelContext.getBuildnumber(), (Integer) AdConfigService.getPrerollAdSlotsBuild().get(AdConstants.PREROLL_AD_SLOTS_IOS_BUILD)))
			ads.put(AdConstants.PREROLL_AD_SLOTS, prerollAdSlots.get(AdConstants.PREROLL_AD_SLOTS));

		// TODO - WHY DO WE DO THE 2 BELOW STEPS FOR EVERY CALL?

		List<AD_Creative_Template> adTemplateIds = new ArrayList<>();
		JSONArray adtemplateids =  (JSONArray) adSettings.get("adtemplateids");
		for (Object adtemplateid : adtemplateids) {
			adTemplateIds.add(AD_Creative_Template.fromDbConfig((String)adtemplateid));
		}
		
		List<AD_Slot> adSlots = new ArrayList<>();
		JSONArray adunitids =  (JSONArray) adSettings.get("adunitids");
		for (Object adunitid : adunitids) {
			adSlots.add(AD_Slot.fromDbConfig((String)adunitid, adTemplateIds));
		}

		for (AD_Slot adSlot : adSlots) {
			boolean toAdd = toAddAdSlot(adSlot);
			if(toAdd && !disableAll) {
				//Dont add preroll slot in case of paid user
				if ((adSlot.getAdType().equals(AD_Type.PREROLL) ||
						adSlot.getAdType().equals(AD_Type.FEATURED) ||
						adSlot.getAdType().equals(AD_Type.AD_EX) ||
						adSlot.getAdType().equals(AD_Type.APP_INSTALL_SLOT) ||
						adSlot.getAdType().equals(AD_Type.WYNK_PREROLL_PREMIUM) ||
						adSlot.getAdType().equals(AD_Type.NATIVE_SLOT_LAST_RAIL) ||
						adSlot.getAdType().equals(AD_Type.RAIL) ||
						adSlot.getAdType().equals(AD_Type.LYRICS_AD_SLOT) ||
						adSlot.getAdType().equals(AD_Type.NATIVE_INTERSTITIAL_AD) ||
						adSlot.getAdType().equals(AD_Type.NATIVE_GRID_SLOT)) &&
						musicConfig.getPrerollEnabled() && !isPremiumAccountUser){
					if(disablePreroll && (adSlot.getAdType().equals(AD_Type.PREROLL) || adSlot.getAdType().equals(AD_Type.WYNK_PREROLL_PREMIUM))){
						logger.info("Disabling Preroll based on A/B experiment ID");
					}
					else {
						if (adSlot.getAdType().equals(AD_Type.NATIVE_INTERSTITIAL_AD)){
							if(CollectionUtils.isNotEmpty(adSlot.getTemplates()))
								ads.put(adSlot.getName(), adSlot.toJSONObject());
						}
						else
							ads.put(adSlot.getName(), adSlot.toJSONObject());
					}
				}


				if ((adSlot.getAdType().equals(AD_Type.NATIVE) || adSlot.getAdType().equals(AD_Type.NATIVE_LIST_SLOT_2) || adSlot.getAdType().equals(AD_Type.NATIVE_LIST_SLOT_8)) && musicConfig.getNativeAdEnabled()) {
					if (isPremiumAccountUser) {
						ads.put(adSlot.getName(), adSlot.toPaidJSONObject());
					} else {
						ads.put(adSlot.getName(), adSlot.toJSONObject());
					}
				}
			}
		}

		// HACK to fix Paid user bug -- Right now in case of paid users they are shown cached ads in offline mode
		if (isPremiumAccountUser && !MusicBuildUtils.isAdPaidBugFix() )
			ads.put(AdConstants.PREROLL_REFRESH_KEY, AdConstants.PREROLL_REFRESH_INTERVAL_PAID_BUG);
		
		
		// Hack to fix ios Bug. Targeting call is not sent at app start by ios
		if (ChannelContext.getOS() != null && ChannelContext.getOS().toLowerCase().contains("ios")){
			ads.put(AdConstants.TARGETING_REFRESH_KEY, AdConstants.IOS_TARGETING_REFRESH);
    } else if (ChannelContext.getOS() != null
        && ChannelContext.getOS().toLowerCase().contains("android")) {
      ads.put(AdConstants.TARGETING_REFRESH_KEY, AdConstants.TARGETING_REFRESH);
      }

		if(!isPremiumAccountUser){
			String removeAdsUrl = null;
			try {
				if(MusicDeviceUtils.isIOSDevice()){
					// TODO : this will get from wcf
						 removeAdsUrl = musicConfig.getWcfBaseApiUrl() + "FEPAGE" + "?bsyext=" + musicConfig.getIosBsyExt() + "&pid=" ;
				}
				else{
					removeAdsUrl = musicConfig.getWcfBaseApiUrl()+ MusicConstants.SUBSCRIBE_PACK_URL+"?u="+ MusicUtils.encryptAndEncodeParam(musicConfig.getEncryptionKey(), ChannelContext.getUid())+"&isPremium="+true;
				}
			}catch (Throwable th){
				logger.error("Exception occured in calling Wcf Recommended API Call for the uid {} in getMusicpack Status", ChannelContext.getUid());
			}
			ads.put(AdConstants.REMOVE_ADS_URL_KEY, removeAdsUrl);
		}
		ads.put("first_ad_position",1);
		ads.put("ad_placement_gap",7);
		ads.put("first_native_ad_slot",3);
		ads.put("gap_between_native_slots",3);

		if((ChannelContext.getOS() != null && ChannelContext.getOS().toLowerCase().contains("ios")) || (ChannelContext.getOS() != null && ChannelContext.getOS().toLowerCase().contains("android")&& MusicBuildUtils.isGreaterBuildNumber(ChannelContext.getBuildnumber(),149)))
		{
			JSONObject remove_ads = new JSONObject();

			remove_ads.put("remove_ads_icon","");
			remove_ads.put("remove_ads_title","Go Ad free");
			remove_ads.put("remove_ads_subtitle","Upgrade to Wynk Premium now and remove ads!");
			remove_ads.put("remove_ads_action_text","Remove Ads");
			ads.put("remove_ads",remove_ads);

		}
		return ads;
	}

	public String getUserSegmentFromService(String day, String uid ,String did) {
		String responseStr = "";
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, SONG_PLAY_COUNT_DAYS);
			String  date = (new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime());

			String URL = musicConfig.getSegmentationURL() + "/userSegment?" + "uid=" + uid + "&did=" + did + "&day=" + date;
			HttpGet request = new HttpGet(URL);

			CloseableHttpResponse response = null;
			try {
				response = httpClient.execute(request);
			} catch (Exception e) {
				logger.error("Error fetching User segemntdata from micro-service" + e.getMessage() + e);
				return "";
			}
			HttpEntity entity = response.getEntity();
			responseStr = EntityUtils.toString(entity);
		}
		catch (Exception e) {
			logger.error("Error getting user segement data from service  for uid : " + uid + " deviceID : " + did  + " error message :  " + e.getMessage() + e);
		}

		return responseStr;
	}

	public JSONObject getUserSegments(String day) {
		JSONObject apiResult = new JSONObject();
		String uid = ChannelContext.getUid();
		String deviceId = ChannelContext.getDeviceId();
		AD_User user = readAdUserFromCassandra(uid);
		if(user != null)
			apiResult.put("userMetaLevelSegment",getUserMetaSegmentINFO(user));

		AD_UserDevice ad_userDevice = readAdUserDeviceFromCassandra(deviceId);
		if(ad_userDevice != null)
			apiResult.put("userDeviceLevelSegment",getUserDeviceSegmentINFO(ad_userDevice));

		List<UserSongPlayCount> userSongPlayCounts = getUserSongPlayCount(uid, day);
		if(!CollectionUtils.isEmpty(userSongPlayCounts))
			apiResult.put("userAnalyticsSegment",getUserSongPlaySegemntINFO(userSongPlayCounts));

		return apiResult;
	}

	private JSONObject getUserSongPlaySegemntINFO(List<UserSongPlayCount> userSongPlayCounts) {

		JSONObject jsonObject = new JSONObject();
		if(CollectionUtils.isEmpty(userSongPlayCounts))
			return jsonObject;

		int counter = 0;
		for(UserSongPlayCount playCount : userSongPlayCounts) {
			counter += playCount.getCounter();
		}
		jsonObject.put("playCount", counter);
		return jsonObject;
	}


	private JSONObject getUserMetaSegmentINFO(AD_User ad_user) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("simType", ad_user.getUsertype());
		jsonObject.put("registrationDate", ad_user.getRegistration_date());
		jsonObject.put("operator", ad_user.getOperator());
		jsonObject.put("circle", ad_user.getCircle());
		jsonObject.put("userSubscriptionType", ad_user.getUser_subscription_type());
		jsonObject.put("appsInstalled", Utils.convertToJSONArray(ad_user.getOther_apps_installed()));
		return jsonObject;
	}

	private JSONObject getUserDeviceSegmentINFO(AD_UserDevice ad_userDevice) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("deviceId", ad_userDevice.getDeviceid());
		jsonObject.put("os", ad_userDevice.getOs());

		return jsonObject;
	}

	private AD_User readAdUserFromCassandra(String uid) {
		if(uid == null)
			return null;

		AD_User ad_user = null;
		try {
			ad_user =  userAdTechCassandraTemplate.selectOne("select * from users where uid='" + uid + "'",
					AD_User.class);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return ad_user;
	}

	private AD_UserDevice readAdUserDeviceFromCassandra(String deviceid) {
		if(deviceid == null)
			return null;

		AD_UserDevice ad_userDevice = null;
		try {
			ad_userDevice =  userAdTechCassandraTemplate.selectOne("select * from devices where deviceid='" + deviceid + "'",
					AD_UserDevice.class);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return ad_userDevice;
	}

	private List<UserSongPlayCount> getUserSongPlayCount(String uid, String day) {
		if(uid == null || day == null )
			return null;

		List<UserSongPlayCount> userSongPlayCounts = null;
		try {
			userSongPlayCounts = userAdTechCassandraTemplate.select("select * from play_counts WHERE day >= '" + day + "' allow filtering",
					UserSongPlayCount.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userSongPlayCounts;
	}


}

