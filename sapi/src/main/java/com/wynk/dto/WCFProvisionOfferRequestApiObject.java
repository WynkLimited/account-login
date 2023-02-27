package com.wynk.dto;

/**
 * Created by aakashkumar on 18/11/16.
 */
public class WCFProvisionOfferRequestApiObject {

    private String headerMsisdn;
    private String msisdn;
    private String clientIps;
    private String os;
    private String deviceId;
    private String appVersion;

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getHeaderMsisdn() {
        return headerMsisdn;
    }

    public void setHeaderMsisdn(String headerMsisdn) {
        this.headerMsisdn = headerMsisdn;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getClientIps() {
        return clientIps;
    }

    public void setClientIps(String clientIps) {
        this.clientIps = clientIps;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "WCFProvisionOfferRequestApiObject{" +
                "headerMsisdn='" + headerMsisdn + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", clientIps='" + clientIps + '\'' +
                ", os='" + os + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
