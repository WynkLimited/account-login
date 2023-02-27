package com.wynk.utils;

import com.wynk.constants.MusicBuildConstants;
import com.wynk.music.dto.MusicPlatformType;
import com.wynk.server.ChannelContext;
import com.wynk.user.dto.User;
import com.wynk.user.dto.UserDevice;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wynk.utils.UserDeviceUtils.isRequestFromWAP;
import static com.wynk.utils.UserDeviceUtils.isWAPUser;

/**
 * Created by Ankit Srivastava on 28/05/16.
 */

public class MusicBuildUtils {

	private static final Logger logger = LoggerFactory.getLogger(MusicBuildUtils.class.getCanonicalName());

	/**
	 * return true if client supports config popup on account creation response
	 */
	public static boolean isConfigPopupOnAccountCreationSupported() {
		return isSupported(MusicBuildConstants.IOS_ACCOUNT_CONFIG_POPUP_SUPPORTED,
				MusicBuildConstants.ANDROID_ACCOUNT_CONFIG_POPUP_SUPPORTED);
	}

	/**
	 * return true if client supports disabling featured section on Home page
	 */
	public static boolean isDisablingFeaturedSupported() {
		return isSupported(MusicBuildConstants.IOS_FEATURED_DISABLE_SUPPORT_BUILD_NUMBER,
				MusicBuildConstants.ANDROID_FEATURED_DISABLE_SUPPORT_BUILD_NUMBER);
	}

	public static boolean isSrilankaSupported() {

		if(ChannelContext.getPlatform() != null && MusicPlatformType.isThirdPartyPlatformId(ChannelContext.getPlatform()))
			return false;
		if (ChannelContext.getOS() != null && ChannelContext.getOS().toLowerCase().contains("android"))

			return isSupportedByBuildNumber(ChannelContext.getBuildnumber(), MusicBuildConstants.SRILANKA_SUPPORTED_ANDROID_BUILD);
		else
			return false;

	}

	/**
	 * return true if client supports Radio packages on Home page
	 */
	public static boolean isRadioSupportedOnFeatured() {
		return isSupported(MusicBuildConstants.NOT_SUPPORTED_ON_OS,
				MusicBuildConstants.ANDROID_RADIO_SUPPORT_BUILD_NUMBER);
	}

	
	public static boolean isTritonSupported() {
		return isSupported(MusicBuildConstants.IOS_TRITON_SUPPORT_BUILD_NUMBER, 
				MusicBuildConstants.ANDROID_TRITON_SUPPORT_BUILD_NUMBER);
	}
	
	/**
	 * return true if client is an OEM build
	 */
	public static boolean isOemBuild() {

		if (UserDeviceUtils.isThirdPartyPlatformRequest() || isMotorolaPreinstall() || isMicromaxPreinstall() || isSamsungDevice())
			return true;
		else
			return false;
	}

	public static boolean isMotorolaPreinstall() {
		UserDevice device = MusicDeviceUtils.getUserDevice();

		if (device != null && StringUtils.isNotBlank(device.getPreInstallOem())
				&& device.getPreInstallOem().equals("moto"))
			return true;

		return false;
	}

	public static boolean isMicromaxPreinstall() {
		UserDevice device = MusicDeviceUtils.getUserDevice();

		if (device != null && StringUtils.isNotBlank(device.getPreInstallOem())
				&& device.getPreInstallOem().equals("mmx"))
			return true;

		return false;
	}

	/**
	 * return true if client build number should be updated
	 */
	public static Boolean doesVersionSupportNewLangs() {

		String userId = ChannelContext.getUid();

		if (UserDeviceUtils.isThirdPartyPlatformRequest())
			return true;

		if (MusicDeviceUtils.isAndroidDevice())
			return isNewerVersion(ChannelContext.getAppVersion(), MusicBuildConstants.ANDROID_NEW_LANGS_SUPPORTED);
		else if (MusicDeviceUtils.isIOSDevice())
			return isNewerVersion(ChannelContext.getAppVersion(), MusicBuildConstants.IOS_NEW_LANGS_SUPPORTED);
		else if (MusicDeviceUtils.isWindowsDeviceFromOS())
			return true;
		else if (isWAPUser(userId) || isRequestFromWAP())
			return true;
		else
			return false;
	}

	/**
	 * return true if client build number should be updated
	 */
	public static Boolean doesBuildSupportHrLang() {

		if (UserDeviceUtils.isThirdPartyPlatformRequest())
			return false;

		if (MusicDeviceUtils.isAndroidDevice())
			return isGreaterBuildNumber(ChannelContext.getBuildnumber(), MusicBuildConstants.ANDROID_HA_LANG_SUPPORT);
		else if (MusicDeviceUtils.isIOSDevice())
			return false;
		else if (MusicDeviceUtils.isWindowsDeviceFromOS())
			return false;
		else
			return false;
	}

	/**
	 * return true if client build number should be updated
	 */
	public static Boolean updateBuildNumber(int buildNumberToUpdate) {
		return isNewerBuildNumber(buildNumberToUpdate, ChannelContext.getBuildnumber());
	}

	/**
	 * return true if client build is an OEM build
	 */
	/*public static boolean isOEMBuild() {
		if (UserDeviceUtils.isThirdPartyPlatformRequest())
			return false;
		
		return MusicDeviceUtils.isAndroidDevice()
				&& MusicBuildConstants.oemBuildList.contains(ChannelContext.getBuildnumber());
	}*/

	/**
	 * return true if client supports 2G optimization changes
	 */
	public static boolean is2GOptimizationSupported() {
		return isSupported(MusicBuildConstants.NOT_SUPPORTED_ON_OS,
				MusicBuildConstants.ANDROID_2G_OPTIMIZATION_SUPPORTED);
	}

	/**
	 * return true if client supports Client verification for authorizing
	 * requests
	 */
	public static boolean isClientVerificationSupported() {
		return isSupported(MusicBuildConstants.IOS_CLIENT_VERIFICATION_SUPPORT_BUILD,
				MusicBuildConstants.ANDROID_CLIENT_VERIFICATION_SUPPORT_BUILD);
	}

	/**
	 * return true if client supports package of packages and navigation items
	 */
	public static boolean isNavigationItemSupported() {
		return isSupported(MusicBuildConstants.IOS_NAVIGATION_SUPPORT_BUILD_NUMBER,
				MusicBuildConstants.ANDROID_NAVIGATION_BUILD_NUMBER);
	}

	/**
	 * return true if client supports On Device/MP3 songs
	 */
	public static boolean isOnDeviceSupported() {
		if (UserDeviceUtils.isThirdPartyPlatformRequest())
			return false;
		
		return isSupported(MusicBuildConstants.NOT_SUPPORTED_ON_OS, MusicBuildConstants.ANDROID_ONDEVICE_BUILD_NUMBER);
	}
	
	public static boolean isAdSupported() {
		return isSupported(MusicBuildConstants.IOS_AD_TECH_BUILD_NUMBER, MusicBuildConstants.ANDROID_AD_TECH_BUILD_NUMBER);
	}

	public static boolean isAdPaidBugFix() {
		return isSupported(MusicBuildConstants.IOS_PAID_BUG, MusicBuildConstants.ANDROID_PAID_BUG);
	}
	/**
	 * return true if client supports ADHM
	 */
	public static boolean isADHMEnabled() {
		return isSupported(MusicBuildConstants.IOS_ADHM_BUILD_NUMBER, MusicBuildConstants.ANDROID_ADHM_BUILD_NUMBER);
	}

	/**
	 * return true if client supports HLS for renting.
	 */
	public static Boolean isHLSRentingSupported() {
		return isSupported(MusicBuildConstants.IOS_BUILD_NUMBER_WITH_HLS_SUPPORT,
				MusicBuildConstants.ANDROID_BUILD_NUMBER_WITH_HLS_SUPPORT_RENTING);
	}

	/**
	 * return true if client supports HLS for renting.
	 */
	public static Boolean isHLSStreamingSupported() {
		return isSupported(MusicBuildConstants.IOS_BUILD_NUMBER_WITH_HLS_SUPPORT,
				MusicBuildConstants.ANDROID_BUILD_NUMBER_WITH_HLS_SUPPORT);
	}

	/**
	 * return true if client supports BE configurable adaptive streaming.
	 */
	public static boolean isAdaptiveBEControlSupported() {
		return isSupported(MusicBuildConstants.IOS_ADAPTIVE_HLS_BE_CONTROL_SUPPORTED,
				MusicBuildConstants.ANDROID_ADAPTIVE_HLS_BE_CONTROL_SUPPORTED);
	}

	/**
	 * return true if client supports BE configurable adaptive streaming.
	 */
	public static boolean shouldServeOnlyAdaptiveHLS() {
		return isSupported(MusicBuildConstants.IOS_SERVE_ONLY_ADAPTIVE_BITRATES,
				MusicBuildConstants.ANDROID_SERVE_ONLY_ADAPTIVE_BITRATES, MusicBuildConstants.NOT_SUPPORTED_ON_OS);

	}

	/**
	 * return true if client supports handling of dup stats events
	 */
	public static boolean isDuplicateStatsHandlingSupported() {
		return isSupported(MusicBuildConstants.IOS_BUILD_NUMBER_WITH_DUP_STATS_SUPPORT,
				MusicBuildConstants.ANDROID_BUILD_NUMBER_WITH_DUP_STATS_SUPPORT);
	}

	/**
	 * return true if auto playlists are created at client
	 */
	public static boolean isAutoPlaylistsDisabled() {
		return isSupported(MusicBuildConstants.IOS_IS_AUTO_PLAYLST_DISABLED,
				MusicBuildConstants.ANDROID_IS_AUTO_PLAYLST_DISABLED);
	}

	/**
	 * return true if client supports handling for geo restriction response
	 */
	public static boolean isGeoRestrictionSupported() {
		return isSupported(MusicBuildConstants.IOS_BUILD_NUMBER_WITH_GEO_RESTRICTION_SUPPORT,
				MusicBuildConstants.ANDROID_BUILD_NUMBER_WITH_GEO_RESTRICTION_SUPPORT);
	}

	/**
	 * return true if client supports external webview notifications
	 */
	public static boolean isExternalWebViewNotificationSupported() {
		return isSupported(MusicBuildConstants.IOS_SUPPORT_FOR_EXTERNAL_WEBVIEW,
				MusicBuildConstants.ANDROID_SUPPORT_FOR_EXTERNAL_WEBVIEW);
	}

	/**
	 * return true if client supports internal webview notifications
	 */
	public static boolean isInternalWebViewNotificationSupported() {
		return isSupported(MusicBuildConstants.IOS_SUPPORT_FOR_EXTERNAL_WEBVIEW,
				MusicBuildConstants.ANDROID_INITIAL_BUILD_NUMBER);
	}

	/**
	 * return true if we UserSeparatedBuilds
	 */
	public static boolean userSeparatedBuilds() {
		return isSupported(MusicBuildConstants.IOS_USER_SEPARATED_NUMBER,
				MusicBuildConstants.ANDROID_USER_SEPARATED_NUMBER);
	}

	public static Boolean isSupported(int iOSBuildNumber, int androidBuildNumber) {
		return isSupported(iOSBuildNumber, androidBuildNumber, MusicBuildConstants.WINDOWS_INITIAL_BUILD_NUMBER);
	}
	
	public static Boolean isSupported(int iOSBuildNumber, int androidBuildNumber, int windowsBuildNumber, int samsungSdkBuildNos) {
		if (UserDeviceUtils.isThirdPartyPlatformRequest())
			return isSupportedByBuildNumber(ChannelContext.getBuildnumber(), samsungSdkBuildNos);

		return isSupported(iOSBuildNumber, androidBuildNumber, MusicBuildConstants.WINDOWS_INITIAL_BUILD_NUMBER);
	}

	public static Boolean isSupported(int iOSBuildNumber, int androidBuildNumber, int windowsBuildNumber) {
		
		if (UserDeviceUtils.isThirdPartyPlatformRequest())
			return true;
		
		if (StringUtils.isBlank(ChannelContext.getOS()))
			return false;

		if (ChannelContext.getOS().toLowerCase().contains("android"))
			return isSupportedByBuildNumber(ChannelContext.getBuildnumber(), androidBuildNumber);
		else if (MusicDeviceUtils.isWindowsDeviceFromOS(ChannelContext.getOS()))
			return isSupportedByBuildNumber(ChannelContext.getBuildnumber(), windowsBuildNumber);
		else
			return isSupportedByBuildNumber(ChannelContext.getBuildnumber(), iOSBuildNumber);
	}

	public static Boolean isSupported(User user, String deviceId, int iOSBuildNumber, int androidBuildNumber) {
		return isSupported(user, deviceId, iOSBuildNumber, androidBuildNumber,
				MusicBuildConstants.WINDOWS_INITIAL_BUILD_NUMBER);
	}

	public static Boolean isSupported(User user, String deviceId, int iOSBuildNumber, int androidBuildNumber,
			int windowsBuildNumber) {
		
		if (UserDeviceUtils.isThirdPartyPlatformRequest())
			return true;
		
		if (user == null)
			return false;

		UserDevice userDevice = MusicDeviceUtils.getUserDeviceFromDid(user, deviceId);

		String os = null;
		int appBuildNo = -1;

		if (userDevice != null) {
			os = userDevice.getOs();
			appBuildNo = userDevice.getBuildNumber();
		}

		if (StringUtils.isBlank(os))
			return false;

		if (os.toLowerCase().contains("android"))
			return isSupportedByBuildNumber(appBuildNo, androidBuildNumber);
		else if (MusicDeviceUtils.isWindowsDeviceFromOS(os))
			return isSupportedByBuildNumber(appBuildNo, windowsBuildNumber);
		else
			return isSupportedByBuildNumber(appBuildNo, iOSBuildNumber);
	}

	public static Boolean isSupportedByBuildNumber(int currentAppBuildNumber, int supportedAppBuildNumber) {
		if (supportedAppBuildNumber == -1)
			return false;

		return isNewerBuildNumber(currentAppBuildNumber, supportedAppBuildNumber);
	}

	public static Boolean isNewerBuildNumber(int currentBuildNumber, int requiredBuildNumber) {
		return currentBuildNumber >= requiredBuildNumber;
	}

	public static Boolean isGreaterBuildNumber(int currentBuildNumber, int requiredBuildNumber) {
		return currentBuildNumber > requiredBuildNumber;
	}

	public static Boolean isNewerVersion(String newVersion, String existingVersion) {
		if (StringUtils.isBlank(newVersion))
			return false;

		int newVersionInt = getVersionAsInt(get3DigitVersion(newVersion));
		int existingVersionInt = getVersionAsInt(get3DigitVersion(existingVersion));
		return newVersionInt > existingVersionInt;
	}

	public static String get3DigitVersion(String version) {
		int countMatches = StringUtils.countMatches(version, ".");
		if (countMatches > 2) {
			int secondOccurance = version.indexOf(".", version.indexOf(".") + 1);
			int thirdOccurance = version.indexOf(".", secondOccurance + 1);
			try {
				version = version.substring(0, thirdOccurance);
			} catch (Exception e) {
				logger.error("Error getting version substring for iOS : " + e.getMessage(), e);
			}
		} else if (countMatches == 1) {
			version = version + ".0";
		}
		return version;
	}
	
	public static Boolean isNewerVersionAsInt(int newVersion, int existingVersion) {

		return newVersion > existingVersion;
	}

	private static int getVersionAsInt(String version) {
		if (StringUtils.isBlank(version))
			return 0;
		String versionAsString = version.replace(".", "");
		return ObjectUtils.integerValue(versionAsString, 0);
	}

	public static boolean isRegistrationPopupSupported() {
        if (isSupported(MusicBuildConstants.IOS_SUPPORT_FOR_REGISTRATION_POPUP,
				MusicBuildConstants.WINDOWS_INITIAL_BUILD_NUMBER, MusicBuildConstants.WINDOWS_INITIAL_BUILD_NUMBER))
        	return true;
        
        return false;
    }

	public static boolean isIntlRoamingSupported() {
		return isSupported(MusicBuildConstants.IOS_INTL_ROAMING,
				MusicBuildConstants.ANDROID_INTL_ROAMING, MusicBuildConstants.WINDOWS_INITIAL_BUILD_NUMBER);
	}


	public static boolean isNotificationETagSrpported() {
		return isSupported(MusicBuildConstants.IOS_ETAG_SUPPORTED,
				MusicBuildConstants.NOT_SUPPORTED_ON_OS);
	}

	public static boolean isSamsungDevice() {
		HttpRequest request = ChannelContext.getRequest();
		if(request!= null && request.headers().get("x-bsy-medium") !=null && request.headers().get("x-bsy-medium").equals("samsung")){
			return true;
		}
		return false;
	}

	public static boolean isSubscriptionIntentSupported(){
		return isSupported(MusicBuildConstants.IOS_SUBSCRIPTION_INTENT,MusicBuildConstants.ANDROID_SUBSCRIPTION_INTENT);
	}

	public static boolean isCloudFrontCookiesSupported(){
		return isSupported(MusicBuildConstants.IOS_CLOUDFRONT_COOKIES_SUPPORT,
				MusicBuildConstants.ANDROID_CLOUDFRONT_COOKIES_SUPPORT,MusicBuildConstants.WINDOWS_INITIAL_BUILD_NUMBER, MusicBuildConstants.NOT_SUPPORTED_ON_OS);
	}

	public static boolean isChangeNumberBuildSupported(){
		return isSupported(MusicBuildConstants.NOT_SUPPORTED_ON_OS,
				MusicBuildConstants.ANDROID_CHANGE_MOBILE_SUPPORT,MusicBuildConstants.NOT_SUPPORTED_ON_OS);
	}

	public static boolean isNativePaymentBuildSupported(){
		return isSupported(MusicBuildConstants.NOT_SUPPORTED_ON_OS,
				MusicBuildConstants.ANDROID_NATIVE_PAYMENT_SUPPORT,MusicBuildConstants.NOT_SUPPORTED_ON_OS, MusicBuildConstants.NOT_SUPPORTED_ON_OS);
	}

	public static boolean isNewSubscriptionBuildSupported() {
		return isSupported(MusicBuildConstants.IOS_NEW_SUBSCRIPTION_SUPPORT, MusicBuildConstants.ANDROID_NEW_SUBSCRIPTION_SUPPORT);
	}

	public static boolean isRedesignSubscriptionBuildSupported() {
		return isSupported(MusicBuildConstants.IOS_REDESIGN_SUBSCRIPTION_SUPPORT, MusicBuildConstants.ANDROID_REDESIGN_SUBSCRIPTION_SUPPORT);
	}

	public static boolean isAppShortCutSupported() {
		return isSupported(MusicBuildConstants.NOT_SUPPORTED_ON_OS, MusicBuildConstants.ANDROID_IS_APP_SHORTCUT_SUPPORTED);
	}

	public static boolean isSriLankaSupported() {
		logger.info("Build Number supplied was {}, platform {}", ChannelContext.getPlatform(), ChannelContext.getBuildnumber());
		if (ChannelContext.getBuildnumber() != null && ChannelContext.getPlatform() != null && MusicBuildUtils.isGreaterBuildNumber(ChannelContext.getBuildnumber(), MusicBuildConstants.MUSIC_SRI_LANKA_BUILD) ) {
			return true;
		}
		return false;
	}

	public static boolean isHtSupported(){
		if((MusicDeviceUtils.isAndroidDevice() && ChannelContext.getBuildnumber() > MusicBuildConstants.HELLOTUNE_ANDROID_BUILD)
				|| (MusicDeviceUtils.isIOSDevice() && ChannelContext.getBuildnumber() > MusicBuildConstants.HELLOTUNE_IOS_BUILD)){
			return true;
		}
		return false;
	}

	public static boolean isInternationalSupportedBuild() {
		logger.info("Checking whether singapore build is supported");
		if (UserUtils.isRequestFromWeb() && StringUtils.isNotBlank(ChannelContext.getUserCoaContext())) {
			logger.info("Web request found via singapore build");
			return true;
		} else if (ChannelContext.getBuildnumber() != null && ChannelContext.getPlatform() != null && ChannelContext.getOS() != null) {
			if (StringUtils.equalsIgnoreCase(ChannelContext.getOS().toLowerCase(), "android") && MusicBuildUtils.isSupportedByBuildNumber(ChannelContext.getBuildnumber(), MusicBuildConstants.ANDROID_SUPPORTED_BUILD_NO_SINGAPORE_ONWARDS)) {
				logger.info("access via android for singapore build");
				return true;
			}
			if (StringUtils.equalsIgnoreCase(ChannelContext.getOS().toLowerCase(), "ios") && MusicBuildUtils.isSupportedByBuildNumber(ChannelContext.getBuildnumber(), MusicBuildConstants.IOS_SUPPORTED_BUILD_NO_SINGAPORE_ONWARDS)) {
				logger.info("access via ios for singapore build");
				return true;
			}
		}
		logger.info("Singapore build not supported by current device");
		return false;
	}

}
