package com.wynk.service;

import com.wynk.config.MusicConfig;
import com.wynk.constants.JsonKeyNames;
import com.wynk.constants.MusicBuildConstants;
import com.wynk.constants.MusicConstants;
import com.wynk.dto.IntlRoaming;
import com.wynk.notification.MusicNotificationConstants;
import com.wynk.server.ChannelContext;
import com.wynk.user.dto.User;
import com.wynk.user.dto.UserDevice;
import com.wynk.utils.*;
import com.wynk.wcf.WCFApisService;
import com.wynk.wcf.WCFApisUtils;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vivek on 12/04/17.
 */

@Service("IntlRoamingService")
public class IntlRoamingService {

    private static final Logger logger = LoggerFactory.getLogger(IntlRoamingService.class.getCanonicalName());

    // 1. Detect device on international IP
    // 2. Get blacklist of users disabled from availing this feature
    // 3. Define if user's international roaming has been processed ever
    // 4. Get current international roaming status - if enabled expiry days left
    // 5. Reset international roaming detected
    // 6. Reset international roaming
    // 7. Config if feature enabled
    // 8. Force upgrade required
    // 9. Sim checks
    // 10. Countries balcklisted for international roaming flow

    @Autowired
    GeoDBService geoDBService;

    @Autowired
    AccountService accountService;

    @Autowired
    MusicConfig musicConfig;

    @Autowired
    WCFUtils wcfUtils;

    @Autowired
    MusicService musicService;

    @Autowired
    WCFApisService wcfApisService;

    @Autowired
    UserEventService userEventService;

    @Autowired
    WCFApisUtils wcfApisUtils;

    public enum FirstTimeUserEligibility {
        INTL_ROAMING_FTUE_REGISTRATION,
        INTL_ROAMING_FTUE_ELIGIBLE,
        INTL_ROAMING_FTUE_INELIGIBLE,
        INTL_ROAMING_FTUE_DEVICE_INELIGIBLE,
        INTL_ROAMING_FTUE_BLACKLISTED
    }
    public enum IPSource {
        INTL_ROAMING_IP_UNKNOWN,
        INTL_ROAMING_IP_ALLOWED_COUNTRY,
        INTL_ROAMING_IP_INTERNATIONAL
    }

    public static Set<String> indianMCCList = new HashSet<>() ;
    static {
        indianMCCList.add("404");
        indianMCCList.add("405");
        indianMCCList.add("413");
    }

    public static Set<String> intlRoamingBlacklist = UserConfigService.INTL_ROAMING_CONFIG.getIntlRoamingBlacklist();
    public static Set<String> indianCarriersList = UserConfigService.INTL_ROAMING_CONFIG.getIndianCarriersList();
//    public static Set<String> indianMCCList = UserConfigService.INTL_ROAMING_CONFIG.getIndianMCCList();

    private static long oneDayTimeLengthInMillis = 86400000L;

    /**
     * @return if the device is on international roaming already
     */
    public boolean isDeviceOnIntlRoaming() {
        UserDevice userDevice = MusicDeviceUtils.getUserDevice();
        return userDevice != null && userDevice.getIntlRoaming() != null && userDevice.getIntlRoaming().isIntlRoamingEnabled();
    }

    /**
     * @return if geodb check to be allowed in renting
     */
    public boolean downloadGeoDbCheckAllow(){
        UserDevice userDevice = MusicDeviceUtils.getUserDevice();
        if (userDevice == null){
            return true;
        }
        boolean isExpired = true;
        if (userDevice.getIntlRoaming() != null){
            if (isDeviceOnIntlRoaming()) {
                long expiryTimestamp = getExpiryTimestamp(userDevice);
                long currentTimestamp = System.currentTimeMillis();
                if (expiryTimestamp > currentTimestamp) {
                    isExpired = false;
                }
            }
        }
        return !musicConfig.isIntlRoamingEnabled() || isExpired;
    }

    /**
     * @return force upgrade version
     */
    public int forceUpgradeCheck(boolean isAndroidDevice) {
        UserDevice userDevice = MusicDeviceUtils.getUserDevice();
        if (userDevice == null){
            return 0;
        }
        IPSource ipSource = getIPSource();
        if ((IPSource.INTL_ROAMING_IP_INTERNATIONAL.equals(ipSource)) && musicConfig.isIntlRoamingEnabled()){
            if (isAndroidDevice){
                return MusicBuildConstants.ANDROID_INTL_ROAMING;
            } else{
                return MusicBuildConstants.IOS_INTL_ROAMING;
            }
        }
        return 0;
    }


    /**
     * @return if international roaming flow is enabled for build and is enabled in config
     */
    public boolean isIntlRoamingEnabled(){
        boolean isVersionSupported = MusicBuildUtils.isIntlRoamingSupported();
        return isVersionSupported && musicConfig.isIntlRoamingEnabled();
    }

    /**
     * @param userDevice user device that is set in channel context
     * @param simInfo
     * @param fromRegistrationFlow
     * @return notification code
     * check first time eligibility or ineligible
     * if first time eligible check if getting first time activation or has been reset previously or quota is expired in present cycle or cycle has renewed
     */
    private Integer getIntlRoamingNotificationCode(UserDevice userDevice, JSONObject simInfo, boolean fromRegistrationFlow) {

        Integer code;

        // First time user eligibility flow returns 3 codes - eligible, ineligible, registration, ineligibleDevice, blacklisted
        // If ineligible return code = MusicConstants.INTL_ROAMING_NOTIF_INELIGIBLE;
        // If registration return code = MusicConstants.INTL_ROAMING_NOTIF_INVOKE_REGISTRATION;
        // If ineligibleDevice return code = String.valueOf(MusicConstants.INTL_ROAMING_NOTIF_DEVICE_INELIGIBLE);

        FirstTimeUserEligibility firstTimeUserEligibilityEligibility = firstTimeUserEligibilityIntlRoaming(simInfo, fromRegistrationFlow);
        logger.info("International roaming : first time user eligibility " + firstTimeUserEligibilityEligibility.name());
        if (FirstTimeUserEligibility.INTL_ROAMING_FTUE_INELIGIBLE.equals(firstTimeUserEligibilityEligibility) || FirstTimeUserEligibility.INTL_ROAMING_FTUE_BLACKLISTED.equals(firstTimeUserEligibilityEligibility)){
            return MusicConstants.INTL_ROAMING_NOTIF_INELIGIBLE;
        }else if (FirstTimeUserEligibility.INTL_ROAMING_FTUE_REGISTRATION.equals(firstTimeUserEligibilityEligibility)){
            return MusicConstants.INTL_ROAMING_NOTIF_INVOKE_REGISTRATION;
        } else if (FirstTimeUserEligibility.INTL_ROAMING_FTUE_DEVICE_INELIGIBLE.equals(firstTimeUserEligibilityEligibility)){
            return MusicConstants.INTL_ROAMING_NOTIF_DEVICE_INELIGIBLE;
        }

        if (userDevice.getIntlRoaming() == null) {
            userDevice.setIntlRoaming(new IntlRoaming());
        }

        long intlRoamingQuota = musicConfig.getIntlRoamingQuota(); // 28 days
        long intlRoamingCycle = musicConfig.getIntlRoamingCycle(); // 90 days

        if (userDevice.getIntlRoaming().getCountConsumedTime() == 0L){
            // international roaming first time activated in quarter
            firstTimeIntlRoamingActivated(userDevice);
            code = MusicConstants.INTL_ROAMING_NOTIF_ACTIVATED;
        }else {
            // international roaming flag is false but the device has been reset at least once
            long timeLeftInCycle = getTimeLeftInCycle(userDevice);

            if (timeLeftInCycle <= intlRoamingCycle){
                code = (userDevice.getIntlRoaming().getCountConsumedTime() <= intlRoamingQuota) ?
                        MusicConstants.INTL_ROAMING_NOTIF_ACTIVATED : MusicConstants.INTL_ROAMING_NOTIF_INELIGIBLE;
                if (code == MusicConstants.INTL_ROAMING_NOTIF_INELIGIBLE){
                    return code;
                }
                roamingActivated(userDevice);
            }else {
                firstTimeIntlRoamingActivated(userDevice);
                code = MusicConstants.INTL_ROAMING_NOTIF_ACTIVATED;
            }
        }
        // set in databases
        accountService.updateDeviceInUserDevicesAndDb(userDevice);
        return code;
    }

    /**
     * @param userDevice
     * @return
     */
    private long getTimeLeftInCycle(UserDevice userDevice) {
        long initialProvisionedDate = userDevice.getIntlRoaming().getInitialProvisionedDate();
        return System.currentTimeMillis() - initialProvisionedDate;
    }

    /**
     * @param userDevice user device that is set in channel context
     * if roaming has been activated for the first time
     */
    private void firstTimeIntlRoamingActivated(UserDevice userDevice) {
        long provisioningTimestamp = System.currentTimeMillis();
        userDevice.getIntlRoaming().setInitialProvisionedDate(provisioningTimestamp);
        userDevice.getIntlRoaming().setProvisionedDate(provisioningTimestamp);
        userDevice.getIntlRoaming().setIntlRoamingEnabled(true);
        userDevice.getIntlRoaming().setCountConsumedTime(0L);
        userDevice.getIntlRoaming().setLastConsumptionUpdateTime(provisioningTimestamp);
    }

    /**
     * @param userDevice user device that is set in channel context
     * if roaming has been activated after a previous reset
     */
    private void roamingActivated(UserDevice userDevice){
        userDevice.getIntlRoaming().setProvisionedDate(System.currentTimeMillis());
        userDevice.getIntlRoaming().setIntlRoamingEnabled(true);
//        updateConsumedTime(userDevice);
    }

    /**
     * @return enum if IP is unknown or from india/allowed country
     */
    private IPSource getIPSource() {
        HttpRequest request = ChannelContext.getRequest();
//        if (MusicUtils.checkAllowedCountryOnBSYIP(geoDBService, request)) {
//            return IPSource.INTL_ROAMING_IP_ALLOWED_COUNTRY;
//        }

        List<String> ipAddressList = MusicUtils.getIPsListFromXForwardedFor(request);

        if (CollectionUtils.isEmpty(ipAddressList)) {
            return IPSource.INTL_ROAMING_IP_UNKNOWN;
        }

        String sourceIP = ipAddressList.get(0);
        String countryCode = geoDBService.getCountry(sourceIP);
        if (StringUtils.isBlank(countryCode)){
            return IPSource.INTL_ROAMING_IP_UNKNOWN;
        }
        else if (MusicConstants.ALLOWED_COUNTRY_LIST.contains(countryCode)){
            return IPSource.INTL_ROAMING_IP_ALLOWED_COUNTRY;
        }
        return IPSource.INTL_ROAMING_IP_INTERNATIONAL;
    }

    /**
     * @param userDevice user device that is set in channel context
     * @param simInfo carrier information
     * @param fromRegistrationFlow
     * @return international roaming notif code
     * if international roaming has expired or getting activated
     */
    private Integer processIntlRoaming(UserDevice userDevice, JSONObject simInfo, boolean fromRegistrationFlow){
        Integer code = MusicConstants.INTL_ROAMING_NOTIF_DEFAULT_CODE;

        if (userDevice == null){
            return code;
        }
        long intlRoamingQuota = musicConfig.getIntlRoamingQuota(); // 28 days
        long intlRoamingExpiryNotif = musicConfig.getIntlRoamingExpiryNotif(); // 7 days

        if (userDevice.getIntlRoaming() == null || !userDevice.getIntlRoaming().isIntlRoamingEnabled()){
            code = getIntlRoamingNotificationCode(userDevice, simInfo, fromRegistrationFlow);
        }else {
            // look for international roaming expiry
            long provisionedDate = userDevice.getIntlRoaming().getProvisionedDate();
            long currentTime = System.currentTimeMillis();
            long intlRoamingLeft = intlRoamingQuota - (getCountConsumedTime(currentTime - provisionedDate));

            // subtract days that have already been consumed
            if (userDevice.getIntlRoaming().getCountConsumedTime() > 0) {
                intlRoamingLeft = intlRoamingLeft - userDevice.getIntlRoaming().getCountConsumedTime();
            }

            // If there is no international roaming time left
            if (intlRoamingLeft <= 0L){
                // check if cycle is to be reset or not
                if (musicConfig.getIntlRoamingCycle() >= getTimeLeftInCycle(userDevice)){
                    code = MusicConstants.INTL_ROAMING_NOTIF_EXPIRED;
                }else {
                    code = getIntlRoamingNotificationCode(userDevice, simInfo, fromRegistrationFlow);
                }
            }else if (intlRoamingLeft > 0L && intlRoamingLeft <= intlRoamingExpiryNotif){
                code = MusicConstants.INTL_ROAMING_NOTIF_EXPIRY;
            }else {
                // check if cycle is to be reset or not
                if (musicConfig.getIntlRoamingCycle() < getTimeLeftInCycle(userDevice)){
                    code = getIntlRoamingNotificationCode(userDevice, simInfo, fromRegistrationFlow);
                }
            }
        }
        return code;
    }

    /**
     * @param userDevice user device that is set in channel context
     * check if device on international roaming.
     * @param simInfo
     */
    private void checkIfResetForIntlRoamingApplicable(UserDevice userDevice, JSONObject simInfo) {
        if (isDeviceOnIntlRoaming()) {
            if (isIndianMCC(simInfo, JsonKeyNames.MCC)) {
                resetDeviceForIntRoaming(userDevice, simInfo);
            }
        }
    }

    /**
     * @param userDevice user device that is set in channel context
     * reset device for international roaming in cache and DB
     */
    private void resetDeviceForIntRoaming(UserDevice userDevice, JSONObject simInfo) {
    	String uid = ChannelContext.getUid();
        if (userDevice != null && userDevice.getIntlRoaming() != null && userDevice.getIntlRoaming().isIntlRoamingEnabled()) {
            long currentTime = System.currentTimeMillis();
            long provisionedTime = userDevice.getIntlRoaming().getProvisionedDate();
            long timeDiff = currentTime-provisionedTime;
            if (userDevice.getIntlRoaming().getLastConsumptionUpdateTime() == provisionedTime || currentTime - userDevice.getIntlRoaming().getLastConsumptionUpdateTime() > oneDayTimeLengthInMillis) {
                userDevice.getIntlRoaming().setCountConsumedTime(getCountConsumedTime(timeDiff));
                userDevice.getIntlRoaming().setLastConsumptionUpdateTime(currentTime);
            }
            userDevice.getIntlRoaming().setIntlRoamingEnabled(false);
            JSONArray simInfoArray = getSimInfoArray(simInfo);
            logger.info("International roaming : reset device" + userDevice.toJsonObject().toJSONString());

            // set in databases
            accountService.updateDeviceInUserDevicesAndDb(userDevice);

            userEventService.addUserDeviceUpdateEvent(uid, userDevice, JsonKeyNames.INTERNATIONAL_ROAMING, "false");
            musicService.createIntlRoamingLog(ChannelContext.getRequest(),ChannelContext.getUid(), userDevice, userDevice.getIntlRoaming().getProvisionedDate(), System.currentTimeMillis(), "reset", simInfoArray);
        }
    }

    private long getCountConsumedTime(long timeDiff) {
        return (((timeDiff)/oneDayTimeLengthInMillis)+1)*oneDayTimeLengthInMillis;
    }

    /**
     * @param appResponseCode app level code for streaming call
     * @param intlRoamingNotifJson notification json
     * @return streaming call response
     */
    private JSONObject getIntlRoamingNotif(Integer appResponseCode, JSONObject intlRoamingNotifJson) {
        JSONObject json = new JSONObject();
        json.put("code", appResponseCode);
        if (appResponseCode == MusicConstants.INTL_ROAMING_NOTIF_DEFAULT_CODE){
            return json;
        }
        if (intlRoamingNotifJson!=null && !intlRoamingNotifJson.isEmpty()) {
            json.put(JsonKeyNames.POPUP_PAYLOAD, intlRoamingNotifJson);
        }
        return json;
    }

    /**
     * @param code notification code
     * @param userDevice user device
     * @return notification json or empty json
     */
    private JSONObject getNotifAccordingToCode(Integer code, UserDevice userDevice, JSONObject simInfo){
        Integer appResponseCode = MusicConstants.INTL_ROAMING_NOTIF_DEFAULT_CODE;
        JSONObject intlRoamingNotifJson = new JSONObject();
        String uid = ChannelContext.getUid();
        JSONArray simInfoArray = getSimInfoArray(simInfo);

        switch (code){
            case MusicConstants.INTL_ROAMING_NOTIF_EXPIRY:
                appResponseCode = MusicConstants.INTL_ROAMING_INFO;
                intlRoamingNotifJson = MusicNotificationConstants.INTL_ROAMING_EXPIRY_NOTIFICATION;
                // hack for setting date in notification
                // intlRoamingNotifJson.put("msg", String.format(MusicConstants.INTL_ROAMING_NOTIF_DATE_INFO, getExpiryDate(userDevice)));
                intlRoamingNotifJson.put("id", getPrefixedId(MusicConstants.INTL_ROAMING_EXPIRY_PREFIX, userDevice.getIntlRoaming().getProvisionedDate()));
                break;
            case MusicConstants.INTL_ROAMING_NOTIF_EXPIRED:
                appResponseCode = MusicConstants.INTL_ROAMING_EXPIRED;
                intlRoamingNotifJson = MusicNotificationConstants.INTL_ROAMING_EXPIRED_NOTIFICATION;
                // hack for setting date in notification
                // intlRoamingNotifJson.put("msg", String.format(MusicConstants.INTL_ROAMING_NOTIF_EXPIRED_DATE_INFO, getExpiryDate(userDevice)));
                intlRoamingNotifJson.put("id", getPrefixedId(MusicConstants.INTL_ROAMING_GENERIC_PREFIX, System.currentTimeMillis()));

                musicService.createIntlRoamingLog(ChannelContext.getRequest(),ChannelContext.getUid(), userDevice, userDevice.getIntlRoaming().getProvisionedDate(), getExpiryTimestamp(userDevice), "expired", simInfoArray);
                break;
            case MusicConstants.INTL_ROAMING_NOTIF_ACTIVATED:
                appResponseCode = MusicConstants.INTL_ROAMING_INFO;
                intlRoamingNotifJson = MusicNotificationConstants.INTL_ROAMING_ACTIVATED_NOTIFICATION;
                // hack for setting date in notification
                // intlRoamingNotifJson.put("msg", String.format(MusicConstants.INTL_ROAMING_NOTIF_DATE_INFO, getExpiryDate(userDevice)));
                intlRoamingNotifJson.put("id", getPrefixedId(MusicConstants.INTL_ROAMING_ACTIVATE_PREFIX, userDevice.getIntlRoaming().getProvisionedDate()));

                userEventService.addUserDeviceUpdateEvent(uid, userDevice, JsonKeyNames.INTERNATIONAL_ROAMING, "true");
                musicService.createIntlRoamingLog(ChannelContext.getRequest(),ChannelContext.getUid(), userDevice, userDevice.getIntlRoaming().getProvisionedDate(), getExpiryTimestamp(userDevice), "activated", simInfoArray);
                break;
            case MusicConstants.INTL_ROAMING_NOTIF_INELIGIBLE:
            case MusicConstants.INTL_ROAMING_NOTIF_DEVICE_INELIGIBLE:
                appResponseCode = MusicConstants.INTL_ROAMING_INELIGIBLE;
                intlRoamingNotifJson = MusicNotificationConstants.INTL_ROAMING_INELEIGIBLE_NOTIFICATION;
                intlRoamingNotifJson.put("id", getPrefixedId(MusicConstants.INTL_ROAMING_GENERIC_PREFIX, System.currentTimeMillis()));
                musicService.createIntlRoamingLog(ChannelContext.getRequest(),ChannelContext.getUid(), userDevice, userDevice.getIntlRoaming().getProvisionedDate(), getExpiryTimestamp(userDevice), "ineligible", simInfoArray);
                break;
            case MusicConstants.INTL_ROAMING_NOTIF_INVOKE_REGISTRATION:
                appResponseCode = MusicConstants.INTL_ROAMING_INVOKE_REGISTRATION;
                break;
            default:
                break;
        }
        return getIntlRoamingNotif(appResponseCode, intlRoamingNotifJson);
    }

    public JSONArray getSimInfoArray(JSONObject simInfo) {
        JSONArray simInfoArray = new JSONArray();
        if (simInfo == null || simInfo.isEmpty()){
            return simInfoArray;
        }
        if (simInfo.containsKey(JsonKeyNames.NETWORK_INFO)) {
            try {
                JSONObject simInfoJson = (JSONObject)simInfo.get(JsonKeyNames.NETWORK_INFO);
                if (simInfoJson.containsKey(JsonKeyNames.SIMINFO)) {
                    try {
                        simInfoArray = (JSONArray) simInfoJson.get(JsonKeyNames.SIMINFO);
                    } catch (Exception e) {
                        logger.error("International roaming: Error in parsing siminfo list from siminfo payload " + e.getMessage(), e);
                    }
                }
            }catch (Exception e){
                logger.error("International roaming: Error in parsing network info siminfo payload " + e.getMessage(), e);
            }
        }
        return simInfoArray;
    }

    /**
     * @param prefix
     * @param timestamp
     * @return
     */
    private String getPrefixedId(String prefix, long timestamp) {
        return prefix + String.valueOf(timestamp);
    }

    /**
     * @param userDevice
     * @return
     */
    private long getExpiryTimestamp(UserDevice userDevice) {
        return userDevice.getIntlRoaming().getProvisionedDate() + getNewIntlRoamingQuota();
    }

    /**
     * @return new international roaming quota which is either international quota or less if already reset.
     */
    // if device has already been reset calculate new international roaming quota
    private long getNewIntlRoamingQuota(){
        UserDevice userDevice = MusicDeviceUtils.getUserDevice();
        long newIntlRoamingQuota = musicConfig.getIntlRoamingQuota();
        if (userDevice.getIntlRoaming().getCountConsumedTime() > 0) {
            newIntlRoamingQuota = newIntlRoamingQuota - userDevice.getIntlRoaming().getCountConsumedTime();
        }
        return newIntlRoamingQuota;
    }

    /**
     * @param simInfo
     * @param fromRegistrationFlow
     * @return if the user eligible for first time eligibility
     */
    private FirstTimeUserEligibility firstTimeUserEligibilityIntlRoaming(JSONObject simInfo, boolean fromRegistrationFlow){
        User user = ChannelContext.getUser();
        FirstTimeUserEligibility firstTimeUserEligibility = FirstTimeUserEligibility.INTL_ROAMING_FTUE_INELIGIBLE;
        if (user == null){
            return firstTimeUserEligibility;
        }
        // 1. check registered - msisdn
        // 2. check premium - wcfUtils get premium account
        if (user.getMsisdn() == null){
            firstTimeUserEligibility = FirstTimeUserEligibility.INTL_ROAMING_FTUE_REGISTRATION;
        }else if (CollectionUtils.isNotEmpty(intlRoamingBlacklist) && intlRoamingBlacklist.contains(Utils.getTenDigitMsisdnWithoutCountryCode(user.getMsisdn()))){
            firstTimeUserEligibility =  FirstTimeUserEligibility.INTL_ROAMING_FTUE_BLACKLISTED;
        }else if (fromRegistrationFlow){
            if (isIndianMCC(simInfo, JsonKeyNames.IMSI)) {
                firstTimeUserEligibility = FirstTimeUserEligibility.INTL_ROAMING_FTUE_ELIGIBLE;
            } else {
                firstTimeUserEligibility = FirstTimeUserEligibility.INTL_ROAMING_FTUE_DEVICE_INELIGIBLE;
            }
        }else {
            firstTimeUserEligibility = FirstTimeUserEligibility.INTL_ROAMING_FTUE_ELIGIBLE;
        }
        if (firstTimeUserEligibility == FirstTimeUserEligibility.INTL_ROAMING_FTUE_ELIGIBLE) {
            if (isIndianMCC(simInfo, JsonKeyNames.IMSI)) {
                if (musicConfig.isIntlRoamingFreeTierCheck()) {
                    if (!wcfApisUtils.isPaidUser(user.getUserSubscription())) {
                        firstTimeUserEligibility = FirstTimeUserEligibility.INTL_ROAMING_FTUE_INELIGIBLE;
                    }
                } else {
                    if (wcfUtils.isNotExpired(user)) {
                        firstTimeUserEligibility = FirstTimeUserEligibility.INTL_ROAMING_FTUE_ELIGIBLE;
                    } else {
                        firstTimeUserEligibility = FirstTimeUserEligibility.INTL_ROAMING_FTUE_INELIGIBLE;
                    }
                }
            }else {
                firstTimeUserEligibility = FirstTimeUserEligibility.INTL_ROAMING_FTUE_INELIGIBLE;
            }
        }
        return firstTimeUserEligibility;
    }

    /**
     * @param simInfo
     * @param fromRegistrationFlow
     * @return notification code for international roaming
     */
    public Integer getIntlRoamingCode(JSONObject simInfo, boolean fromRegistrationFlow) {
        UserDevice userDevice = MusicDeviceUtils.getUserDevice();
        Integer code = MusicConstants.INTL_ROAMING_NOTIF_DEFAULT_CODE;
        IPSource ipSource = getIPSource();
        if (IPSource.INTL_ROAMING_IP_ALLOWED_COUNTRY.equals(ipSource)) {
            checkIfResetForIntlRoamingApplicable(userDevice, simInfo);
            return code;
        } else if (IPSource.INTL_ROAMING_IP_UNKNOWN.equals(ipSource)){
            return code;
        }else {
            if (isIndianMCC(simInfo, JsonKeyNames.MCC)){
                return code;
            }
            return processIntlRoaming(userDevice, simInfo, fromRegistrationFlow);
        }
    }

    /**
     * @param simInfo sim info json object passed
     * @return if mcc in sim info is in list of indian MCCs
     */
    public boolean isIndianMCC(JSONObject simInfo, String field){
        boolean success = false;
        if (simInfo == null || simInfo.isEmpty() || CollectionUtils.isEmpty(indianMCCList)){
            return success;
        }
        try {
            if (MusicDeviceUtils.isIOSDevice() && field.equalsIgnoreCase(JsonKeyNames.IMSI)){
                logger.info("International roaming : IOS device skipping MCC field check");
                return true;
            }
            UserDevice userDevice = ChannelContext.getUserDevice();
            JSONArray simInfoArray = getSimInfoArray(simInfo);
            for (Object sim : simInfoArray){
                for (String allowedMcc : indianMCCList){
                    JSONObject simJson = (JSONObject)sim;
                    if (simJson.containsKey(field)) {
                        String mccString = simJson.get(field).toString();
                        if (mccString.length() >= 3) {
                            String mcc = mccString.substring(0, 3);
                            if (allowedMcc.contains(mcc)) {
                                success = true;
                                break;
                            }
                        }
                    }
                }
            }

        }catch (Exception e){
            logger.error("International roaming: Error in parsing sim info: " + simInfo + e.getMessage(), e);
        }
        return success;
    }

    /**
     * @param simInfo
     * @return streaming call json or empty json if international roaming is not applicable or reset happened.
     */
    // Entry point in international roaming flow
    public JSONObject intlRoamingFlow(JSONObject simInfo, boolean fromRegistrationFlow) {
        UserDevice userDevice = MusicDeviceUtils.getUserDevice();
        if (userDevice == null){
            return new JSONObject();
        }

        return getNotifAccordingToCode(getIntlRoamingCode(simInfo, fromRegistrationFlow), userDevice, simInfo);
    }
}
