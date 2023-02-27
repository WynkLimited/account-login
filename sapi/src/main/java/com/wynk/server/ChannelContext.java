package com.wynk.server;
import com.bsb.portal.core.common.AccessMsnInf;
import com.bsb.portal.core.common.CircleCatalog;
import com.wynk.common.Language;
import com.wynk.constants.MusicConstants;
import com.wynk.music.constants.MusicContentLanguage;
import com.wynk.common.RequestContext;
import com.wynk.user.dto.*;
import com.wynk.utils.MusicDeviceUtils;
import com.wynk.utils.UserDeviceUtils;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.AttributeKey;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by anurag on 12/9/16.
 */
public class ChannelContext {

	private static final Logger logger               = LoggerFactory.getLogger(ChannelContext.class.getCanonicalName());

	private static AttributeKey<HttpRequest> httpRequest   	= AttributeKey.valueOf("httpRequest");
	private static AttributeKey<String> uidContext   	    = AttributeKey.valueOf("uidContext");
	private static AttributeKey<String> circleContext       = AttributeKey.valueOf("circleContext");
	private static AttributeKey<String> msisdnContext       = AttributeKey.valueOf("msisdnContext");
	private static AttributeKey<String> deviceIdContext     = AttributeKey.valueOf("deviceIdContext");
	private static AttributeKey<String> etag   	 	 		= AttributeKey.valueOf("etag");

	private static AttributeKey<List<MusicContentLanguage>> contentLangContext   = AttributeKey.valueOf("contentLangContext");
	private static AttributeKey<AccessMsnInf> userOperatorInfo             = AttributeKey.valueOf("userOperatorInfo");
	private static AttributeKey<CircleCatalog> circleCatalog 		       = AttributeKey.valueOf("circleCatalog");
	private static AttributeKey<String> langContext   	 	               = AttributeKey.valueOf("langContext");

	private static AttributeKey<LinkedHashSet<String>> downloadedSongIdsSet   = AttributeKey.valueOf("downloadedSongIdsSet");
	private static AttributeKey<LinkedHashSet<String>> rentedSongIdsSet     	= AttributeKey.valueOf("rentedSongIdsSet");
	private static AttributeKey<LinkedHashSet<String>> likedContentIdsSet     = AttributeKey.valueOf("likedContentIdsSet");

	private static AttributeKey<Map<String,Long>> downloadedSongTimestamps   = AttributeKey.valueOf("downloadedSongTimestamps");
	private static AttributeKey<Map<String,Long>> rentedSongTimestamps     	 = AttributeKey.valueOf("rentedSongTimestamps");
	private static AttributeKey<Map<String,Long>> likedSongTimestamps        = AttributeKey.valueOf("likedSongTimestamps");


	private static AttributeKey<Long> timeStamp   			= AttributeKey.valueOf("timeStamp");
	private static AttributeKey<User> userContext     		= AttributeKey.valueOf("userContext");

	private static AttributeKey<RequestContext> requestContext = AttributeKey.valueOf("requestContext");
	private static AttributeKey<Boolean> isSOSOnlyBatch        = AttributeKey.valueOf("isSOSOnlyBatch");

	private static AttributeKey<String> osContext   				= AttributeKey.valueOf("osContext");
	private static AttributeKey<Integer> buildNumberContext     	= AttributeKey.valueOf("buildNumberContext");
	private static AttributeKey<String> appVersionContext 		    = AttributeKey.valueOf("appVersionContext");
	private static AttributeKey<Integer> platformContext 			= AttributeKey.valueOf("platformContext");
	private static AttributeKey<String> utknContext 			= AttributeKey.valueOf("utkn");

	private static AttributeKey<String> userDeletedVersionContext = AttributeKey.valueOf("userDeletedVersion");

	private static AttributeKey<String> userCoaContext    		= AttributeKey.valueOf("userCountryAccess");
	private static AttributeKey<String> userCooContext     		= AttributeKey.valueOf("userCountryOnboard");


	public static void setRequest(HttpRequest request) {
		ChannelThreadLocal.get().attr(httpRequest).set(request);
		try {
			ChannelThreadLocal.get().attr(userOperatorInfo).set(UserDeviceUtils.getUserOperatorInfo(request));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static HttpRequest getRequest() {
		try {
			if (ChannelThreadLocal.get() == null)
				return null;
			return ChannelThreadLocal.get().attr(httpRequest).get();
		} catch( Exception e) {
			logger.info("getRequest is not set");
		}
		return null;
	}


	public static boolean isSOSOnlyBatch() {
		return ChannelThreadLocal.get().attr(isSOSOnlyBatch).get();
	}

	public static void setSOSOnlyBatch(boolean sosOnlyBatch) {
		ChannelThreadLocal.get().attr(isSOSOnlyBatch).set(sosOnlyBatch);
	}

	public static RequestContext getRequestContext() {
		Channel channel = ChannelThreadLocal.get();
		RequestContext context = channel.attr(requestContext).get();
		if (context != null) {
			return context;
		}
		context = new RequestContext();
		channel.attr(requestContext).set(context);
		return context;
	}

	/**
	 * set request context
	 *
	 * @param context
	 */
	public static void setRequestContext(RequestContext context) {
		ChannelThreadLocal.get().attr(requestContext).set(context);
	}

	public static String getLang() {
		String lang = ChannelThreadLocal.get().attr(langContext).get();

		if(lang == null || lang.equalsIgnoreCase("null"))
			lang = "en";
		return lang;
	}

	public static void setLang(String lang) {
		if(lang == null || lang.equalsIgnoreCase("null"))
			lang = "en";

		//set en if invalid lang code is received
		ChannelThreadLocal.get().attr(langContext).set(Language.getLanguageById(lang).getId());
	}

	public static void setEtag(String eTag) {
		ChannelThreadLocal.get().attr(etag).set(eTag);
	}

	public static String getEtag() {
		return ChannelThreadLocal.get().attr(etag).get();
	}


	public static String getUid() {

		// return null if no context is found.
		if (uidContext == null)
			return null;

		String uid = ChannelThreadLocal.get().attr(uidContext).get();

		if(StringUtils.isEmpty(uid) || uid.equalsIgnoreCase("null"))
			return null;
		return uid;
	}

	public static void setUid(String uid) {
		if(uid == null || uid.equalsIgnoreCase("null"))
			return;

		ChannelThreadLocal.get().attr(uidContext).set(uid);
	}

	public static String getDeviceId() {
		Map<String, String> parseMusicHeaderDID = MusicDeviceUtils.parseMusicHeaderDID();
		return parseMusicHeaderDID.get(MusicConstants.DEVICE_ID);
	}

	public static void setDeviceId(String deviceId) {
		if(deviceId == null || deviceId.equalsIgnoreCase("null"))
			return;

		ChannelThreadLocal.get().attr(deviceIdContext).set(deviceId);
	}

	//TODO-R265 check logic
	public static List<MusicContentLanguage> getContentLang() {
		List<MusicContentLanguage> lang = ChannelThreadLocal.get().attr(contentLangContext).get();
		if(lang == null){
			lang = new ArrayList<MusicContentLanguage>();
			lang.add(MusicContentLanguage.HINDI);
			lang.add(MusicContentLanguage.ENGLISH);
		}
		return lang;
	}

	public static void setContentLang(List<MusicContentLanguage> userLanguages) {
		if(userLanguages == null )
			return;

		//set en if invalid lang code is received
		ChannelThreadLocal.get().attr(contentLangContext).set(userLanguages);
	}

	public static AccessMsnInf getUserOperatorInfo() {
		return ChannelThreadLocal.get().attr(userOperatorInfo).get();
	}

	public static CircleCatalog getCircleCatalog() {
		return  ChannelThreadLocal.get().attr(circleCatalog).get();
	}

	public static LinkedHashSet<String> getDownloadedSongIdsSet() {
		return ChannelThreadLocal.get().attr(downloadedSongIdsSet).get();
	}

	public static LinkedHashSet<String> getRentedSongIdsSet() {
		return ChannelThreadLocal.get().attr(rentedSongIdsSet).get();
	}

	public static LinkedHashSet<String> getLikedContentIdsSet() {
		return ChannelThreadLocal.get().attr(likedContentIdsSet).get();
	}

	public static Map<String, Long> getDownloadedSongTimestamps() {
		return ChannelThreadLocal.get().attr(downloadedSongTimestamps).get();
	}

	public static Map<String, Long> getRentedSongTimestamps() {
		return ChannelThreadLocal.get().attr(rentedSongTimestamps).get();
	}

	public static Map<String, Long> getLikedSongTimestamps() {
		return ChannelThreadLocal.get().attr(likedSongTimestamps).get();
	}

	public static void setLikedSongTimestamps(Map<String, Long> likedSongs) {
		ChannelThreadLocal.get().attr(likedSongTimestamps).set(likedSongs);
	}

	public static void setDownloadedSongTimestamps(Map<String, Long> downloadedSongs) {
		ChannelThreadLocal.get().attr(downloadedSongTimestamps).set(downloadedSongs);
	}

	public static void setRentedSongTimestamps(Map<String, Long> rentedSongs) {
		ChannelThreadLocal.get().attr(rentedSongTimestamps).set(rentedSongs);
	}

	public static String getCircle() {
		String circle = ChannelThreadLocal.get().attr(circleContext).get();

		if(StringUtils.isEmpty(circle) || circle.equalsIgnoreCase("null"))
			return null;
		return circle;
	}

	public static void setCircle(String circle) {
		if(circle == null || circle.equalsIgnoreCase("null"))
			return;

		ChannelThreadLocal.get().attr(circleContext).set(circle);
	}

	public static String getMsisdn() {
		String msisdn = ChannelThreadLocal.get().attr(msisdnContext).get();

		if(StringUtils.isEmpty(msisdn) || msisdn.equalsIgnoreCase("null"))
			return null;
		return msisdn;
	}

	public static void setMsisdn(String msisdn) {
		if(msisdn == null || msisdn.equalsIgnoreCase("null"))
			return;

		ChannelThreadLocal.get().attr(msisdnContext).set(msisdn);
	}

	public static void setDownloadedSongIdsSet(LinkedHashSet<String> songIdsSet) {
		ChannelThreadLocal.get().attr(downloadedSongIdsSet).set(songIdsSet);
	}

	public static void setLikedContentIdsSet(LinkedHashSet<String> songIdsSet) {
		ChannelThreadLocal.get().attr(likedContentIdsSet).set(songIdsSet);
	}

	public static void setRentedSongIdsSet(LinkedHashSet<String> songIdsSet) {
		ChannelThreadLocal.get().attr(rentedSongIdsSet).set(songIdsSet);
	}

	public static User getUser() {
		return ChannelThreadLocal.get().attr(userContext).get();
	}

	public static void setUser(User user)
	{
		String userCircle = getCircle();
		if(user  != null)
		{
			if(logger.isDebugEnabled())
				logger.debug("Set Context for "+user.getUid()+","+ Thread.currentThread().getId()+"," + ChannelThreadLocal.get().id());

			long startTime = System.currentTimeMillis();
			if(StringUtils.isEmpty(ChannelContext.getMsisdn()) && !StringUtils.isEmpty(user.getMsisdn()))
				setMsisdn(user.getMsisdn());
			
			ChannelThreadLocal.get().attr(platformContext).set(user.getPlatform());
			ChannelThreadLocal.get().attr(userContext).set(user);
			
			if(StringUtils.isEmpty(getUid()))
				setUid(user.getUid());


			List<String> userLangs =  user.getContentLanguages();
			if(userLangs != null && userLangs.size() > 0)
			{
//				String nonEngHiLang = null;
//				for (int i = 0; i < userLangs.size(); i++) {
//					String lang = userLangs.get(i);
//					if(lang.equalsIgnoreCase(MusicContentLanguage.ENGLISH.getId()) ||
//							lang.equalsIgnoreCase(MusicContentLanguage.HINDI.getId()) )
//						continue;
//					nonEngHiLang = lang;
//					break;
//
//				}
//				MusicContentLanguage lang1 = MusicContentLanguage.getContentLanguageById(nonEngHiLang);
//				if(lang1 == null)
//					lang1 = MusicContentLanguage.HINDI;
//
//				ChannelThreadLocal.get().attr(contentLangContext).set(lang1);
				
				//TODO-R265 . Need proper review
				List<MusicContentLanguage> userLanguages = new ArrayList<MusicContentLanguage>();
				for (int i = 0; i < userLangs.size(); i++) {
					String lang = userLangs.get(i);
					MusicContentLanguage userContentLang = MusicContentLanguage.getContentLanguageById(lang);
					userLanguages.add(userContentLang);
				}
				if(CollectionUtils.isEmpty(userLanguages)){
					userLanguages.add(MusicContentLanguage.HINDI);
					userLanguages.add(MusicContentLanguage.ENGLISH);
				}
					
				if(!CollectionUtils.isEmpty(userLanguages))
					ChannelThreadLocal.get().attr(contentLangContext).set(userLanguages);
			}

			List<UserPurchase> userDownloads = user.getDownloads();
			if(userDownloads != null)
			{
				LinkedHashSet<String> downloadedSongIdsSet = new LinkedHashSet<>();
				Map<String,Long> downloadedSongTSs = new HashMap<>();
				for (int i = 0; i < userDownloads.size(); i++) {
					UserPurchase userPurchase = userDownloads.get(i);
					if(userPurchase.isDeleted())
						continue;

					downloadedSongIdsSet.add(userPurchase.getId());
					downloadedSongTSs.put(userPurchase.getId(),userPurchase.getTimestamp());
				}
				ChannelContext.setDownloadedSongIdsSet(downloadedSongIdsSet);
				ChannelContext.setDownloadedSongTimestamps(downloadedSongTSs);
			}

			List<UserRental> userRentals = user.getRentals();
			if(userRentals != null)
			{
				LinkedHashSet<String> rentedSongIdsSet = new LinkedHashSet<>();
				Map<String,Long> rentedSongTSs = new HashMap<>();
				for (int i = 0; i < userRentals.size(); i++) {
					UserRental userRental = userRentals.get(i);
					rentedSongIdsSet.add(userRental.getId());
					rentedSongTSs.put(userRental.getId(),userRental.getTimestamp());
				}
				ChannelContext.setRentedSongIdsSet(rentedSongIdsSet);
				ChannelContext.setRentedSongTimestamps(rentedSongTSs);
			}

			List<UserFavorite> userLikes = user.getFavorites();
			if(userLikes != null)
			{
				LinkedHashSet<String> likedSongIdsSet = new LinkedHashSet<>();
				Map<String,Long> likedSongTSs = new HashMap<>();
				for (int i = 0; i < userLikes.size(); i++) {
					UserFavorite userFav = userLikes.get(i);
					likedSongIdsSet.add(userFav.getId());
					likedSongTSs.put(userFav.getId(),userFav.getTimestamp());
				}
				ChannelContext.setLikedContentIdsSet(likedSongIdsSet);
				ChannelContext.setLikedSongTimestamps(likedSongTSs);
			}
			if(logger.isDebugEnabled())
				logger.debug("Context creation time for "+getUid()+" : "+(System.currentTimeMillis() - startTime));
		}
		else
		{
			setContentLangsByCircle(userCircle);
		}
	}

	//TODO-R265 check logic
	public static void setContentLangsByCircle(String userCircle)
	{
		List<MusicContentLanguage> defaultUserLanguages = new ArrayList<MusicContentLanguage>();
		defaultUserLanguages.add(MusicContentLanguage.HINDI);
		defaultUserLanguages.add(MusicContentLanguage.ENGLISH);
		ChannelThreadLocal.get().attr(contentLangContext).set(defaultUserLanguages);
	}

	public static UserDevice getUserDevice()
	{
		User user = ChannelContext.getUser();
		if(user == null)
			return null;
		return user.getActiveDevice();
	}

	public static UserDevice getUserCurrentDevice(String deviceId)
	{
		User user = ChannelContext.getUser();
		if(user == null)
			return null;
		return user.getDevice(deviceId);
	}


	//0 -> non-app
	//1 -> app - android
	//2 -> app - iOS
	//3 -> app - Windows
	public static int getClientType()
	{
		HttpRequest request =  ChannelContext.getRequest();
		if(request == null)
			return 0;
		HttpHeaders headers = request.headers();
		if(headers == null)
			return 0;

		if(headers.contains("x-bsy-utkn") || headers.contains("x-bsy-did") || headers.contains("x-bsy-net"))
		{
			if(headers.contains("x-bsy-snet"))
			{
				if(MusicDeviceUtils.isWindowsDeviceFromOS())
					return 3;
				return 1;
			}
			else
				return 2;
		}
		return 0;
	}


	public static int getClientTypeFromDeviceOs(String operatingSystem)
	{
		if(StringUtils.isBlank(operatingSystem))
			return 0;

		if ("android".contains(operatingSystem.toLowerCase())) {
			return 1;
		} else if ("ios".contains(operatingSystem.toLowerCase())) {
			return 2;
		} else if ("windowsphone".contains(operatingSystem.toLowerCase())) {
			return 3;
		}
		else {
			return 0;
		}
	}


	public static void unset()
	{
		Channel channel = ChannelThreadLocal.get();
		if(channel == null)
			return;

		/*String uuid = channel.attr(uidContext).get();
		if(!StringUtils.isBlank(uuid))
			logger.info("Unset Context for "+ uuid +","+ Thread.currentThread().getId()+","+ChannelThreadLocal.get().id());*/

		channel.attr(langContext).remove();
		channel.attr(userContext).remove();
		channel.attr(contentLangContext).remove();
		channel.attr(etag).remove();
		channel.attr(circleContext).remove();
		channel.attr(downloadedSongTimestamps).remove();
		channel.attr(downloadedSongIdsSet).remove();
		channel.attr(httpRequest).remove();

		channel.attr(likedContentIdsSet).remove();
		channel.attr(likedSongTimestamps).remove();
		channel.attr(msisdnContext).remove();

		channel.attr(rentedSongTimestamps).remove();
		channel.attr(rentedSongIdsSet).remove();

		channel.attr(uidContext).remove();
		channel.attr(userOperatorInfo).remove();
		channel.attr(timeStamp).remove();

		channel.attr(osContext).remove();
		channel.attr(appVersionContext).remove();
		channel.attr(buildNumberContext).remove();
		channel.attr(deviceIdContext).remove();
		channel.attr(isSOSOnlyBatch).remove();

		channel.attr(circleCatalog).remove();
		channel.attr(requestContext).remove();
		channel.attr(platformContext).remove();
		channel.attr(utknContext).remove();
		channel.attr(userDeletedVersionContext).remove();
		channel.attr(userCooContext).remove();
		channel.attr(userCoaContext).remove();
	}


	public static Long getTimestamp() {
		return ChannelThreadLocal.get().attr(timeStamp).get();
	}

	public static void setTimestamp(Long timestamp) {
		ChannelThreadLocal.get().attr(timeStamp).set(timestamp);
	}

	public static String getOS() {
		return ChannelThreadLocal.get().attr(osContext).get();
	}
	public static void setOscontext(String os) {
		if(StringUtils.isBlank(os))
			return;

		ChannelThreadLocal.get().attr(osContext).set(os);
	}


	public static Integer getBuildnumber() {
		return ChannelThreadLocal.get().attr(buildNumberContext).get();
	}

	public static void setBuildnumbercontext(Integer buildNumber) {
		ChannelThreadLocal.get().attr(buildNumberContext).set(buildNumber);
	}

	public static String getAppVersion() {
		return ChannelThreadLocal.get().attr(appVersionContext).get();
	}

	public static void setAppVersionContext(String appVersion) {
		if(StringUtils.isBlank(appVersion))
			return;

		ChannelThreadLocal.get().attr(appVersionContext).set(appVersion);
	}
	
	public static Integer getPlatform() {
		try {
			if (ChannelThreadLocal.get() == null)
				return null;
			return ChannelThreadLocal.get().attr(platformContext).get();
		} catch( Exception e) {
			logger.info("getPlatform is not set");
		}
		return null;
	}

	public static void setPlatformContext(Integer platform) {
		ChannelThreadLocal.get().attr(platformContext).set(platform);
	}

	public static void setUtknContext(String utkn) {
		ChannelThreadLocal.get().attr(utknContext).set(utkn);
	}

	public static String getUtknContext() {
		String utkn = ChannelThreadLocal.get().attr(utknContext).get();

		if(StringUtils.isEmpty(utkn) || utkn.equalsIgnoreCase("null"))
			return null;
		return utkn;
	}

	public static String getUserDeletedVersionContext() {

		if (userDeletedVersionContext == null)
			return null;

		String deletedVersionString = ChannelThreadLocal.get().attr(userDeletedVersionContext).get();

		if(StringUtils.isEmpty(deletedVersionString) || deletedVersionString.equalsIgnoreCase("null"))
			return null;

		return deletedVersionString;
	}
	public static void setUserDeletedVersionContext(String deletedVersion) {
		if (deletedVersion == null || deletedVersion.equalsIgnoreCase("null"))
			return;

		ChannelThreadLocal.get().attr(userDeletedVersionContext).set(deletedVersion);
	}

	public static String getUserCoaContext() {

		if (userCoaContext == null) {
			logger.info("In context it's not present so returning null COA");
			return StringUtils.EMPTY;
		}

		String countryOfAccess = ChannelThreadLocal.get().attr(userCoaContext).get();

		if (StringUtils.isEmpty(countryOfAccess) || countryOfAccess.equalsIgnoreCase("null"))
		{
			logger.info("After getting key still, it's not there COA");
			return StringUtils.EMPTY;
		}

		logger.info("Found something while getting in COA : {}", countryOfAccess);
		return countryOfAccess;
	}

	public static void setUserCoaContext(String countryOfAccess) {

		logger.info("Going to set key as COA : {}", countryOfAccess);

		if (countryOfAccess == null || countryOfAccess.equalsIgnoreCase("null"))
		{
			logger.info("Going to set keys as null, as nothing is there COA");
			ChannelThreadLocal.get().attr(userCoaContext).set(StringUtils.EMPTY);
			return;
		}
		ChannelThreadLocal.get().attr(userCoaContext).set(countryOfAccess);
	}

	public static String getUserCooContext() {

		if (userCooContext == null)
		{
			logger.info("In context it's not present so returning null COO");
			return StringUtils.EMPTY;
		}

		String countryOfOnbaording = ChannelThreadLocal.get().attr(userCooContext).get();

		if (StringUtils.isEmpty(countryOfOnbaording) || countryOfOnbaording.equalsIgnoreCase("null"))
		{
			logger.info("After getting key still, it's not there COO");
			return StringUtils.EMPTY;
		}

		logger.info("Found something while getting in COO : {}", countryOfOnbaording);
		return countryOfOnbaording;
	}

	public static void setUserCooContext(String countryOfOnbaording) {

		logger.info("Going to set key as COO : {}", countryOfOnbaording);

		if (countryOfOnbaording == null || countryOfOnbaording.equalsIgnoreCase("null"))
		{
			logger.info("Going to set keys as null, as nothing is there COO");
			ChannelThreadLocal.get().attr(userCooContext).set(StringUtils.EMPTY);
			return;
		}

		ChannelThreadLocal.get().attr(userCooContext).set(countryOfOnbaording);
	}

}
