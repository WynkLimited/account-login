package com.wynk.user.dto;

import static com.wynk.utils.MusicBuildUtils.isSupportedByBuildNumber;
import com.wynk.common.WynkAppType;
import com.wynk.constants.MusicBuildConstants;
import com.wynk.dto.IntlRoaming;
import com.wynk.dto.SimInfo;
import com.wynk.dto.WCFRegistrationChannel;
import com.wynk.server.ChannelContext;
import com.wynk.utils.EncryptUtils;
import com.wynk.utils.MusicDeviceUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: bhuvangupta
 * Date: 21/11/13
 * Time: 12:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserDevice {
    private static final Logger logger = LoggerFactory.getLogger(UserDevice.class.getCanonicalName());
    private String uid;
    private String deviceType = "";//model of device
    private String deviceId = "";
    private String deviceKey = "";   // used for push notification
    private String deviceVersion = "";
    private String os    = null;
    private String osVersion    = null;
    private String appVersion   = "";
    private String appId   = "";
    private long registrationDate = System.currentTimeMillis();
    private long lastActivityTime = System.currentTimeMillis();
    private String msisdn;
    private boolean isActive = true;
    private String token;
    private String resolution;
    private String operator;
    private String deviceSnsARN;
    private int buildNumber;
    private String preInstallOem;
    private long deviceKeyLastUpdateTime;
    private String advertisingId;
    private String fbUserId;
    private String imeiNumber;
    private Boolean isDualSim;
    private List<SimInfo> simInfo;
    private IntlRoaming intlRoaming;
    private WCFRegistrationChannel registrationChannel;
    private WynkAppType appType;
    private boolean is4GEnabled;
    private String customerCategory;
    private String totpDeviceId;
    private String totpDeviceKey;

    public String getTotpDeviceId() {
        return totpDeviceId;
    }

    public void setTotpDeviceId(String totpDeviceId) {
        this.totpDeviceId = totpDeviceId;
    }

    public String getTotpDeviceKey() {
        return totpDeviceKey;
    }

    public void setTotpDeviceKey(String totpDeviceKey) {
        this.totpDeviceKey = totpDeviceKey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Boolean getIsDualSim() {
		return isDualSim;
	}

	public void setIsDualSim(Boolean isDualSim) {
		this.isDualSim = isDualSim;
	}

	public List<SimInfo> getSimInfo() {
		return simInfo;
	}

	public void setSimInfo(List<SimInfo> simInfo) {
		this.simInfo = simInfo;
	}

	public String getAdvertisingId() {
        return advertisingId;
    }

    public void setAdvertisingId(String advertisingId) {
        this.advertisingId = advertisingId;
    }

    public String getFbUserId() {
        return fbUserId;
    }

    public void setFbUserId(String fbUserId) {
        this.fbUserId = fbUserId;
    }

    // string to contain the last batch processed id to avoid stats duplication.
    private String lastBatchProcessedId;



    public String getLastBatchProcessedId() {
        return lastBatchProcessedId;
    }

    public void setLastBatchProcessedId(String lastBatchProcessedId) {
        this.lastBatchProcessedId = lastBatchProcessedId;
    }

    public String getPreInstallOem() {
        return preInstallOem;
    }

    public void setPreInstallOem(String preInstallOem) {
        this.preInstallOem = preInstallOem;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceKey() {
        return deviceKey;
    }

    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public long getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(long registrationDate) {
        this.registrationDate = registrationDate;
    }

    public long getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDeviceSnsARN() {
        return deviceSnsARN;
    }

    public void setDeviceSnsARN(String deviceSnsARN) {
        this.deviceSnsARN = deviceSnsARN;
    }

    public long getDeviceKeyLastUpdateTime() {
        return deviceKeyLastUpdateTime;
    }

    public void setDeviceKeyLastUpdateTime(long deviceKeyLastUpdateTime) {
        this.deviceKeyLastUpdateTime = deviceKeyLastUpdateTime;
    }

    public String getImeiNumber() {
		return imeiNumber;
	}

	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}

    public IntlRoaming getIntlRoaming() {
        return intlRoaming;
    }

    public void setIntlRoaming(IntlRoaming intlRoaming) {
        this.intlRoaming = intlRoaming;
    }

    public WCFRegistrationChannel getRegistrationChannel() {
        return registrationChannel;
    }

    public void setRegistrationChannel(WCFRegistrationChannel registrationChannel) {
        this.registrationChannel = registrationChannel;
    }

    public WynkAppType getAppType() {
        return appType;
    }

    public void setAppType(WynkAppType appType) {
        this.appType = appType;
    }

    public boolean isIs4GEnabled() {
        return is4GEnabled;
    }

    public void setIs4GEnabled(boolean is4GEnabled) {
        this.is4GEnabled = is4GEnabled;
    }

    public String getCustomerCategory() {
        return customerCategory;
    }

    public void setCustomerCategory(String customerCategory) {
        this.customerCategory = customerCategory;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(DeviceEnityKey.deviceType, getDeviceType());
        jsonObj.put(DeviceEnityKey.deviceId, getDeviceId());
        jsonObj.put(DeviceEnityKey.deviceVersion, getDeviceVersion());
        jsonObj.put(DeviceEnityKey.appVersion, getAppVersion());
        jsonObj.put(DeviceEnityKey.appId, getAppId());
        jsonObj.put(DeviceEnityKey.os, getOs());
        jsonObj.put(DeviceEnityKey.osVersion, getOsVersion());
        jsonObj.put(DeviceEnityKey.deviceKey, getDeviceKey());
        jsonObj.put(DeviceEnityKey.resolution, getResolution());
        jsonObj.put(DeviceEnityKey.operator, getOperator());
        jsonObj.put(DeviceEnityKey.buildNumber, getBuildNumber());
        jsonObj.put(DeviceEnityKey.preInstallOem, getPreInstallOem());
        jsonObj.put(DeviceEnityKey.deviceKeyLastUpdateTime, getDeviceKeyLastUpdateTime());
        jsonObj.put(DeviceEnityKey.advertisingId, getAdvertisingId());
        jsonObj.put(DeviceEnityKey.fbUserId, getFbUserId());
        jsonObj.put(DeviceEnityKey.imeiNumber, getImeiNumber());
        jsonObj.put(DeviceEnityKey.registrationDate, getRegistrationDate());
        jsonObj.put(DeviceEnityKey.lastActivityTime, getLastActivityTime());
        jsonObj.put(DeviceEnityKey.is4gEnabled, isIs4GEnabled());
        if(totpDeviceKey!=null){
            jsonObj.put(DeviceEnityKey.totpDeviceKey, getTotpDeviceKey());
        }
        if(totpDeviceId!=null){
            jsonObj.put(DeviceEnityKey.totpDeviceId, getTotpDeviceId());
        }
        if(isDualSim!=null){
        	jsonObj.put(DeviceEnityKey.isDualSim, getIsDualSim());
        }
        if(simInfo != null){
        	jsonObj.put(DeviceEnityKey.simInfo, getSimInfoJson());
        }

        if (appType != null) {
            jsonObj.put(DeviceEnityKey.appType, getAppType().toString());
        }

        if (appType != null) {
            jsonObj.put(DeviceEnityKey.appType, getAppType().toString());
        }

        if (customerCategory != null) {
            jsonObj.put(DeviceEnityKey.customerCategory, getCustomerCategory());
        }

        if(intlRoaming == null) {
            IntlRoaming ir = new IntlRoaming();
            setIntlRoaming(ir);
        }
        jsonObj.put(DeviceEnityKey.intlRoaming, getIntlRoaming().toJsonObject());

        if(getRegistrationChannel() != null){
            jsonObj.put(DeviceEnityKey.registrationChannel,getRegistrationChannel().toJsonObject());
        }
        return jsonObj;
    }

	public JSONArray getSimInfoJson() {
		JSONArray simsArray = new JSONArray();
		   if(getSimInfo() != null) {
		       for(int i = 0; i < getSimInfo().size(); i++) {
		           SimInfo sim = getSimInfo().get(i);
		           simsArray.add(sim.toJsonObject());
		       }
		   }
		   return simsArray;
	}

    public void fromJson(String json,boolean isEnrcypted) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj,isEnrcypted);
    }

    public void fromAppJsonObject(JSONObject jsonObj,boolean isEncrypted){

        //        System.out.println("Device Info : "+jsonObj.toJSONString());
        Object idobj = jsonObj.get("uid");
        if(idobj instanceof String) {
            setUid((String) idobj);
        }
        if(jsonObj.get("active") != null) {
            setActive((Boolean) jsonObj.get("active"));
        }
        if(jsonObj.get("regdate") != null) {
            long time = (Long) jsonObj.get("regdate");
            setRegistrationDate(time);
        }
        if(jsonObj.get("lastActivity") != null) {
            long time = (Long) jsonObj.get("lastActivity");
            setLastActivityTime(time);
        }
        if(jsonObj.get("msisdn") != null)
            setMsisdn((String) jsonObj.get("msisdn"));
        if(jsonObj.get("devicetype") != null && jsonObj.get("devicetype") instanceof String)
            setDeviceType((String) jsonObj.get("devicetype"));

        if(jsonObj.get("devicekey") != null)
        {
            //todo: fix it. right now android and iOS are inconsistent
            //            setDeviceType((String) jsonObj.get("devicekey"));
            setDeviceKey((String) jsonObj.get("devicekey"));
        }

        if(jsonObj.get("deviceid") != null)
            setDeviceId((String) jsonObj.get("deviceid"));
        if(jsonObj.get("deviceversion") != null)
            setDeviceVersion((String) jsonObj.get("deviceversion"));
        if(jsonObj.get("appversion") != null)
            setAppVersion((String) jsonObj.get("appversion"));
        if(jsonObj.get("appId") != null)
            setAppId((String) jsonObj.get("appId"));
        if(jsonObj.get("osystem") != null)
            setOs((String) jsonObj.get("osystem"));
        if(jsonObj.get("osversion") != null)
            setOsVersion((String) jsonObj.get("osversion"));
        if(jsonObj.get("token") != null)
            setToken((String) jsonObj.get("token"));
        if(jsonObj.get("resolution") != null)      //iOS
            setResolution((String) jsonObj.get("resolution"));
        if(jsonObj.get("deviceresolution") != null)     //Android
            setResolution((String) jsonObj.get("deviceresolution"));
        if(jsonObj.get("carrier") != null)
            setOperator((String) jsonObj.get("carrier"));
        else if(jsonObj.get("operator") != null)
            setOperator((String) jsonObj.get("operator"));
        if(jsonObj.get("snsarn") != null)
            setDeviceSnsARN((String) jsonObj.get("snsarn"));
        if(jsonObj.get("buildNumber") instanceof String){
            setBuildNumber((Integer.parseInt((String) jsonObj.get("buildNumber"))));
        }
        if(jsonObj.get("buildNumber") instanceof Number){
            setBuildNumber(((Number) jsonObj.get("buildNumber")).intValue());
        }
        if(jsonObj.get("preinstallOem") != null){
            setPreInstallOem((String) jsonObj.get("preinstallOem"));
        }
        if(jsonObj.get("lastBatchProcessedId") != null){
            setLastBatchProcessedId((String) jsonObj.get("lastBatchProcessedId"));
        }
        if(jsonObj.get("deviceKeyLastUpdateTime") != null) {
            long time = (Long) jsonObj.get("deviceKeyLastUpdateTime");
            setDeviceKeyLastUpdateTime(time);
        }
        if(jsonObj.get("advertisingId") != null){
            setAdvertisingId((String) jsonObj.get("advertisingId"));
        }
        if(jsonObj.get("fbUserId") != null){
            setFbUserId((String) jsonObj.get("fbUserId"));
        }
        if(jsonObj.get("imeiNumber") != null){
            try {
                setImeiNumber(
                    isEncrypted? EncryptUtils
                        .decrypt_256((String)jsonObj.get("imeiNumber"),
                            EncryptUtils.getDeviceKey()):(String)jsonObj.get("imeiNumber"));
            }
            catch (Exception e) {
                logger.error("Error while decrypting sim ImeiNumber",e);
            }
        }
        if(jsonObj.get("isDualSim") != null){
            setIsDualSim((Boolean) jsonObj.get("isDualSim"));
        }
        if(jsonObj.get("simInfo") != null){
            JSONArray simArray = (JSONArray) jsonObj.get("simInfo");
            List<SimInfo> sims = new ArrayList<>();
            if (simArray != null) {
                for (int k = 0; k < simArray.size(); k++) {
                    JSONObject simObj = (JSONObject) simArray.get(k);
                    SimInfo simInfo = new SimInfo();
                    try {
                        simInfo.fromJsonObject(simObj,isEncrypted);
                    } catch (Exception e) {
                        logger.error("Error while decrypting msisdn",e);
                    }
                    sims.add(simInfo);
                }
                setSimInfo(sims);
            }
        }
        else if(jsonObj.get("networkInfo") != null){
            JSONObject networkInfoJsonObject = (JSONObject) jsonObj.get("networkInfo");

            if(networkInfoJsonObject.get("siminfo") != null){
                JSONArray simArray = (JSONArray) networkInfoJsonObject.get("siminfo");
                List<SimInfo> sims = new ArrayList<>();
                if (simArray != null) {
                    for (int k = 0; k < simArray.size(); k++) {
                        JSONObject simObj = (JSONObject) simArray.get(k);
                        SimInfo simInfo = new SimInfo();
                        try {
                            simInfo.fromJsonObject(simObj,isEncrypted);
                        } catch (Exception e) {
                            logger.error("Error while decrypting imsi/imei number",e);
                        }
                        sims.add(simInfo);
                    }
                    setSimInfo(sims);
                }
            }

        }
        IntlRoaming ir = new IntlRoaming();
        setIntlRoaming(ir);
        if(jsonObj.get("intlRoaming") != null){
            JSONObject irObj = (JSONObject) jsonObj.get("intlRoaming");
            ir.fromJsonObject(irObj);
            setIntlRoaming(ir);
        }

        if(jsonObj.get("registrationChannel") != null){
            try {
                String registrationChannelObject = jsonObj.get("registrationChannel").toString();
                WCFRegistrationChannel wcfRegistrationChannel = new WCFRegistrationChannel();
                wcfRegistrationChannel.fromJson(registrationChannelObject);
                setRegistrationChannel(wcfRegistrationChannel);
            }catch (Throwable th){

            }
        }
        //        System.out.println("After Device Info : " + this.toJsonObject().toJSONString());

    }

    public void fromJsonObject(JSONObject jsonObj,boolean isEnrcypted) {
        if(jsonObj.get(DeviceEnityKey.registrationDate) != null) {
            long time = (Long) jsonObj.get(DeviceEnityKey.registrationDate);
            setRegistrationDate(time);
        }
        if(jsonObj.get(DeviceEnityKey.lastActivityTime) != null) {
            long time = (Long) jsonObj.get(DeviceEnityKey.lastActivityTime);
            setLastActivityTime(time);
        }
        if(jsonObj.get(DeviceEnityKey.deviceType) != null && jsonObj.get(DeviceEnityKey.deviceType) instanceof String)
            setDeviceType((String) jsonObj.get(DeviceEnityKey.deviceType));

        if(jsonObj.get(DeviceEnityKey.deviceKey) != null)
        {
            setDeviceKey((String) jsonObj.get(DeviceEnityKey.deviceKey));
        }
        if(jsonObj.get(DeviceEnityKey.deviceId) != null)
            setDeviceId((String) jsonObj.get(DeviceEnityKey.deviceId));
        if(jsonObj.get(DeviceEnityKey.deviceVersion) != null)
            setDeviceVersion((String) jsonObj.get(DeviceEnityKey.deviceVersion));
        if(jsonObj.get(DeviceEnityKey.appVersion) != null)
            setAppVersion((String) jsonObj.get(DeviceEnityKey.appVersion));
        if(jsonObj.get(DeviceEnityKey.appId) != null)
            setAppId((String) jsonObj.get(DeviceEnityKey.appId));
        if(jsonObj.get(DeviceEnityKey.os) != null)
            setOs((String) jsonObj.get(DeviceEnityKey.os));
        if(jsonObj.get(DeviceEnityKey.osVersion) != null)
            setOsVersion((String) jsonObj.get(DeviceEnityKey.osVersion));
        if(jsonObj.get(DeviceEnityKey.resolution) != null)      //iOS
            setResolution((String) jsonObj.get(DeviceEnityKey.resolution));
        if(jsonObj.get("deviceresolution") != null)     //Android
            setResolution((String) jsonObj.get("deviceresolution"));
        if(jsonObj.get(DeviceEnityKey.totpDeviceKey) != null)
            setTotpDeviceKey((String) jsonObj.get(DeviceEnityKey.totpDeviceKey));
        if(jsonObj.get(DeviceEnityKey.totpDeviceId) != null)
            setTotpDeviceId((String) jsonObj.get(DeviceEnityKey.totpDeviceId));

        Object buildNumberObj = jsonObj.get(DeviceEnityKey.buildNumber);
        if(buildNumberObj instanceof String){
    		setBuildNumber(Integer.parseInt((String) buildNumberObj));
    	}
        if(buildNumberObj instanceof Number){
    	    if(buildNumberObj instanceof Long){
                setBuildNumber(Math.toIntExact((Long)buildNumberObj));
            }else {
                setBuildNumber((Integer)buildNumberObj);
            }
        }
    	if(jsonObj.get(DeviceEnityKey.preInstallOem) != null){
            setPreInstallOem((String) jsonObj.get(DeviceEnityKey.preInstallOem));
        }
        if(jsonObj.get(DeviceEnityKey.operator) != null)
            setOperator((String) jsonObj.get(DeviceEnityKey.operator));

        if(jsonObj.get(DeviceEnityKey.advertisingId) != null){
            setAdvertisingId((String) jsonObj.get(DeviceEnityKey.advertisingId));
        }
        if(jsonObj.get(DeviceEnityKey.fbUserId) != null){
            setFbUserId((String) jsonObj.get(DeviceEnityKey.fbUserId));
        }
        if(jsonObj.get(DeviceEnityKey.imeiNumber) != null){
            try {
                setImeiNumber(
                    isEnrcypted? EncryptUtils
                        .decrypt_256((String)jsonObj.get(DeviceEnityKey.imeiNumber),
                            EncryptUtils.getDeviceKey()):(String)jsonObj.get(DeviceEnityKey.imeiNumber));
            }
            catch (Exception e) {
                logger.error("Error while decrypting sim ImeiNumber",e);
            }
        }
        if(jsonObj.get(DeviceEnityKey.isDualSim) != null){
            setIsDualSim((Boolean) jsonObj.get(DeviceEnityKey.isDualSim));
        }
        if(jsonObj.get(DeviceEnityKey.appType) != null){
            WynkAppType type = WynkAppType.valueOf((String) jsonObj.get(DeviceEnityKey.appType));
            setAppType(type);
        }

        if(jsonObj.get(DeviceEnityKey.customerCategory) != null) {
            setCustomerCategory((String) jsonObj.get(DeviceEnityKey.customerCategory));
        }

        if(jsonObj.get(DeviceEnityKey.is4gEnabled) != null){
            setIs4GEnabled((Boolean) jsonObj.get(DeviceEnityKey.is4gEnabled));
        }
        if(jsonObj.get(DeviceEnityKey.simInfo) != null){
			JSONArray simArray = (JSONArray) jsonObj.get(DeviceEnityKey.simInfo);
			List<SimInfo> sims = new ArrayList<>();
			if (simArray != null) {
				for (int k = 0; k < simArray.size(); k++) {
					JSONObject simObj = (JSONObject) simArray.get(k);
					SimInfo simInfo = new SimInfo();
            try {
                simInfo.fromJsonObject(simObj,isEnrcypted);
            } catch (Exception e) {
                logger.error("Error while decrypting sim info",e);
            }
            sims.add(simInfo);
				}
				setSimInfo(sims);
			}
		}
		else if(jsonObj.get("networkInfo") != null){
            JSONObject networkInfoJsonObject = (JSONObject) jsonObj.get("networkInfo");

            if(networkInfoJsonObject.get("siminfo") != null){
                JSONArray simArray = (JSONArray) networkInfoJsonObject.get("siminfo");
                List<SimInfo> sims = new ArrayList<>();
                if (simArray != null) {
                    for (int k = 0; k < simArray.size(); k++) {
                        JSONObject simObj = (JSONObject) simArray.get(k);
                        SimInfo simInfo = new SimInfo();
                        try {
                            simInfo.fromJsonObject(simObj,isEnrcypted);
                        } catch (Exception e) {
                            logger.error("Error while decrypting sim info",e);
                        }
                        sims.add(simInfo);
                    }
                    setSimInfo(sims);
                }
            }

        }
        IntlRoaming ir = new IntlRoaming();
        setIntlRoaming(ir);
        if(jsonObj.get(DeviceEnityKey.intlRoaming) != null){
            JSONObject irObj = (JSONObject) jsonObj.get(DeviceEnityKey.intlRoaming);
            ir.fromJsonObject(irObj);
            setIntlRoaming(ir);
        }

        if(jsonObj.get(DeviceEnityKey.registrationChannel) != null){
            try {
                String registrationChannelObject = jsonObj.get(DeviceEnityKey.registrationChannel).toString();
                WCFRegistrationChannel wcfRegistrationChannel = new WCFRegistrationChannel();
                wcfRegistrationChannel.fromJson(registrationChannelObject);
                setRegistrationChannel(wcfRegistrationChannel);
            }catch (Throwable th){

            }
        }

    }
}
