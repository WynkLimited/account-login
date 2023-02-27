package com.wynk.dto;

/**
 * Created by aakashkumar on 10/11/16.
 */
public class WCFSubscribeInitializationObject {

    private Integer productId;
    private String returnUrl;
    private String uid;
    private String msisdn;
    private String msisdnFromHeader;
    private String email;
    private String name;
    private String appVersion;
    private String buildNumber;
    private String deviceId;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    private String platform;
    private boolean isVerifiedUser;

    public boolean isVerifiedUser() {
        return isVerifiedUser;
    }

    public void setVerifiedUser(boolean verifiedUser) {
        isVerifiedUser = verifiedUser;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getMsisdnFromHeader() {
        return msisdnFromHeader;
    }

    public void setMsisdnFromHeader(String msisdnFromHeader) {
        this.msisdnFromHeader = msisdnFromHeader;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    @Override
    public String toString() {
        return "WCFSubscribeInitializationObject{" +
                "productId=" + productId +
                ", returnUrl='" + returnUrl + '\'' +
                ", uid='" + uid + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", msisdnFromHeader='" + msisdnFromHeader + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", buildNumber='" + buildNumber + '\'' +
                '}';
    }
}
