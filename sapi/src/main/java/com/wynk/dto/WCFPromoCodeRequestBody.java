package com.wynk.dto;

/**
 * Created by Aakash on 11/08/17.
 */
public class WCFPromoCodeRequestBody {

    private String coupon;
    private String deviceId;
    private String uid;

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "WCFPromoCodeRequestBody{" +
                "coupon='" + coupon + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
