package com.wynk.dto;

import java.util.List;

/**
 * Created by Aakash on 20/03/17.
 */
public class WCFMsisdnIdentificationRequestBody {

    private String ipAddress;
    private String appVersion;
    private String platform;
    private String imsi;
    private String xMsisdn;
    private String imei;
    private String deviceId;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getxMsisdn() {
        return xMsisdn;
    }

    public void setxMsisdn(String xMsisdn) {
        this.xMsisdn = xMsisdn;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
