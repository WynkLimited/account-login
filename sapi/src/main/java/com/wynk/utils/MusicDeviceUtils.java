package com.wynk.utils;

import com.wynk.constants.MusicConstants;
import com.wynk.common.NetworkQuality;
import com.wynk.user.dto.User;
import com.wynk.user.dto.UserDevice;
import com.wynk.server.ChannelContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class MusicDeviceUtils {

	public static UserDevice getUserDevice() {
		return getUserDeviceFromDid(ChannelContext.getUser(), ChannelContext.getDeviceId());
	}

	public static UserDevice getUserDeviceFromDid(User user, String deviceId) {
		if (user == null)
			return null;

		if (StringUtils.isBlank(deviceId)) {
			return null;
		}
		
		List<UserDevice> devices = user.getDevices();
		if (CollectionUtils.isEmpty(devices))
			return null;

		for (UserDevice device : devices) {
			
			if (deviceId.equalsIgnoreCase(device.getDeviceId()))
				return device;
		}
		return null;
	}

	public static final String getOSVersion(String did) {
		Map<String, String> parseMusicHeaderDID = parseMusicHeaderDID(did);
		return parseMusicHeaderDID.get(MusicConstants.OS_VERSION);
	}

	public static final String getOSVersion() {
		Map<String, String> parseMusicHeaderDID = parseMusicHeaderDID();
		return parseMusicHeaderDID.get(MusicConstants.OS_VERSION);
	}
	
	public static boolean isWindowsDeviceFromOS(String os) {
		if (StringUtils.isBlank(os))
			return false;

		if (os.toLowerCase().contains("windows") || os.toLowerCase().contains("windowsphone"))
			return true;

		return false;
	}

	public static boolean isWindowsDeviceFromOS() {
		return isWindowsDeviceFromOS(ChannelContext.getOS());
	}

	public static boolean isIOSDeviceFromOS(String os) {
		if (StringUtils.isBlank(os))
			return false;

		if (MusicDeviceUtils.isAndroidDeviceFromOS(os) || isWindowsDeviceFromOS(os))
			return false;

		return true;
	}

	public static boolean isIOSDevice() {
		return isIOSDeviceFromOS(ChannelContext.getOS());
	}

	public static boolean isAndroidDeviceFromOS(String os) {
		if (StringUtils.isBlank(os))
			return false;

		if (os.toLowerCase().contains("android"))
			return true;

		return false;
	}

	public static boolean isAndroidDevice() {
		return isAndroidDeviceFromOS(ChannelContext.getOS());
	}

	public static final Map<String, String> parseMusicHeaderDID() {
		return parseMusicHeaderDID(null);
	}

	public static final Map<String, String> parseMusicHeaderDID(String did) {

		if (StringUtils.isBlank(did)) {
			HttpRequest request = ChannelContext.getRequest();
			if (request == null)
				return null;
			did = request.headers().get(MusicConstants.MUSIC_HEADER_DID);
		}

		Map<String, String> result = new LinkedHashMap<>();

		if (did != null) {
			String res[] = did.split("\\s*/\\s*");
			if (res.length == 5) {
				result.put(MusicConstants.DEVICE_ID, res[0]); // DeviceId
				result.put(MusicConstants.OS, res[1]); // OS
				result.put(MusicConstants.OS_VERSION, res[2]); // OS-Version
				result.put(MusicConstants.APP_BUILD_NO, res[3]); // App Build
																	// Number
				result.put(MusicConstants.APP_VERSION_NO, res[4]); // App
																	// Version
																	// Number
			} else if (res.length == 1) {
				result.put(MusicConstants.DEVICE_ID, res[0]); // Old builds only
																// DeviceId
			}
		}
		return result;
	}

	public static final String getOS() {
		Map<String, String> parseMusicHeaderDID = parseMusicHeaderDID();
		return parseMusicHeaderDID.get(MusicConstants.OS);
	}

	public static final String getAppVersion() {
		Map<String, String> parseMusicHeaderDID = parseMusicHeaderDID();
		return parseMusicHeaderDID.get(MusicConstants.APP_VERSION_NO);
	}

	public static final int getBuildNumber() {
		Map<String, String> parseMusicHeaderDID = parseMusicHeaderDID();
		String buildNumber = parseMusicHeaderDID.get(MusicConstants.APP_BUILD_NO);
		return NumberUtils.toInt(buildNumber,0);
	}

	public static final String getDeviceId() {
		return ChannelContext.getDeviceId();
	}

	public static final String getDeviceId(String did) {
		Map<String, String> parseMusicHeaderDID = parseMusicHeaderDID(did);
		return parseMusicHeaderDID.get(MusicConstants.DEVICE_ID);
	}

	public static boolean isAndroid(String userAgent) {
		if (StringUtils.isBlank(userAgent)) {
			return false;
		}
		// True if matches.
		if (userAgent.toLowerCase().contains("android")) {
			return true;
		}
		return false;
	}

	public static NetworkQuality getNetworkQuality(HttpRequest request) {
		HttpHeaders headers = request.headers();
		String networkQuality = null;
		Map<String, String> networkHeaderMap = UserDeviceUtils
				.parseNetworkHeader(headers.get(MusicConstants.MUSIC_HEADER_NET));

		if (networkHeaderMap != null && networkHeaderMap.size() > 0)
			networkQuality = networkHeaderMap.get(MusicConstants.NETWORK_QUALITY);

		if (networkQuality == null)
			return NetworkQuality.UNKNOWN;

		String os = ChannelContext.getOS();
		Integer appBuildNo = ChannelContext.getBuildnumber();
		Integer netQuality = NumberUtils.toInt(networkQuality, -1);

		if (os == null)
			return NetworkQuality.UNKNOWN;

		if (appBuildNo == -1)
			return NetworkQuality.UNKNOWN;

		switch (netQuality) {
		case -1:
			return NetworkQuality.UNKNOWN;

		case 0:
			if (StringUtils.containsIgnoreCase(os, "android")) {
				if (appBuildNo >= 37)
					return NetworkQuality.AWFUL;
				else
					return NetworkQuality.POOR;
			} else
				return NetworkQuality.POOR;
		case 1:
			if (StringUtils.containsIgnoreCase(os, "android")) {
				if (appBuildNo >= 46)
					return NetworkQuality.INDIAN_POOR;
				else if (appBuildNo < 46 && appBuildNo >= 37)
					return NetworkQuality.POOR;
				else
					return NetworkQuality.MODERATE;
			} else
				return NetworkQuality.MODERATE;

		case 2:
			if (StringUtils.containsIgnoreCase(os, "android")) {
				if (appBuildNo >= 46)
					return NetworkQuality.POOR;
				else if (appBuildNo < 46 && appBuildNo >= 37)
					return NetworkQuality.MODERATE;
				else
					return NetworkQuality.GOOD;
			} else
				return NetworkQuality.GOOD;

		case 3:
			if (StringUtils.containsIgnoreCase(os, "android")) {
				if (appBuildNo >= 46)
					return NetworkQuality.MODERATE;
				else if (appBuildNo < 46 && appBuildNo >= 37)
					return NetworkQuality.GOOD;
				else
					return NetworkQuality.EXCELLENT;
			} else
				return NetworkQuality.EXCELLENT;

		case 4:
			if (StringUtils.containsIgnoreCase(os, "android")) {
				if (appBuildNo >= 46)
					return NetworkQuality.GOOD;
				else
					return NetworkQuality.EXCELLENT;
			} else
				return NetworkQuality.EXCELLENT;

		case 5:
			return NetworkQuality.EXCELLENT;

		default:
			return NetworkQuality.UNKNOWN;
		}
	}

	public static Map<String,String> getOSAndBuildNo() {
		String did = ChannelContext.getRequest().headers().get(MusicConstants.MUSIC_HEADER_DID);
		//String did = "575B18E7-CC14-4F0E-9CEC-987E52020C1E/Android/21/25/1.1.6";
		Map<String, String> result = parseMusicHeaderDID();
		String os = null;
		int appBuildNo = 0;
		Map<String, String> osBuildNo = new HashMap<>();
		if (result.size() == 5) {
			os = result.get(MusicConstants.OS);
			String abn = result.get(MusicConstants.APP_BUILD_NO);
			if (abn != null)
				try {
					appBuildNo = Integer.parseInt(abn);
				} catch (NumberFormatException e) {
//					e.printStackTrace();
				}
		}

		if(os == null  || appBuildNo == 0) {
			UserDevice userDevice = getUserDeviceFromDid(ChannelContext.getUser(), did);
			if(userDevice != null) {
				if (os == null)
					os = userDevice.getOs();
				if (appBuildNo == 0)
					appBuildNo = userDevice.getBuildNumber();
			}
		}

		osBuildNo.put(MusicConstants.OS,os);
		if(appBuildNo == 45)
			appBuildNo = 40;

		osBuildNo.put(MusicConstants.APP_BUILD_NO, String.valueOf(appBuildNo));

		return  osBuildNo;
	}

	public static final Map<Object, Object> parseMusicHeaders(String did) {

		if (StringUtils.isBlank(did)) {
			HttpRequest request = ChannelContext.getRequest();
			if (request == null)
				return Collections.emptyMap();
			did = request.headers().get(MusicConstants.MUSIC_HEADER_DID);
		}

		Map<Object, Object> map = new LinkedHashMap<>();

		if (did != null) {
			List<String> list = Arrays.asList(did.split("\\s*/\\s*"));
			if (list.size() == 5) {
				map.put("deviceId", list.get(0));
				map.put("os", list.get(1));
				map.put("appId", list.get(2));
				map.put("buildNo", list.get(3));
				map.put("appVersion", list.get(4));
			}
		}
		return map;
	}


}
