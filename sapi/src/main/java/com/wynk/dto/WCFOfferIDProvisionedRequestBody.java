package com.wynk.dto;

import java.util.List;

/**
 * Created by Aakash on 01/08/17.
 */
public class WCFOfferIDProvisionedRequestBody {

    private String uid;
    private String msisdn;
    private List<Integer> offerIds;
    private String deviceId;
    private String os;

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

    public List<Integer> getOfferIds() {
        return offerIds;
    }

    public void setOfferIds(List<Integer> offerIds) {
        this.offerIds = offerIds;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    @Override
    public String toString() {
        return "WCFOfferIDProvisionedRequestBody{" +
                "uid='" + uid + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", offerIds=" + offerIds +
                ", deviceId='" + deviceId + '\'' +
                ", os='" + os + '\'' +
                '}';
    }
}
