package com.wynk.dto;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by Aakash on 03/03/17.
 */
public class WCFCurrentSubscriptionStatus {

    private String service;
    private Integer productId;
    private Long validTillDate;
    private Boolean autoRenewalOff;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Long getValidTillDate() {
        return validTillDate;
    }

    public void setValidTillDate(Long validTillDate) {
        this.validTillDate = validTillDate;
    }

    public Boolean getAutoRenewalOff() {
        return autoRenewalOff;
    }

    public void setAutoRenewalOff(Boolean autoRenewalOff) {
        this.autoRenewalOff = autoRenewalOff;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }

    public void fromJsonObject(JSONObject jsonObj) {
        if(jsonObj.get("service") != null){
            setService(jsonObj.get("service").toString());
        }
        if(jsonObj.get("productId") != null){
            setProductId(Integer.parseInt(jsonObj.get("productId").toString()));
        }
        if(jsonObj.get("validTillDate")!=null){
            setValidTillDate(Long.parseLong(jsonObj.get("validTillDate").toString()));
        }
        if(jsonObj.get("autoRenewalOff")!=null){
            setAutoRenewalOff(Boolean.parseBoolean(jsonObj.get("autoRenewalOff").toString()));
        }
    }
}
