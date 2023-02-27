package com.wynk.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by aakashkumar on 22/11/16.
 */
public class WCFCallBackRequestObject {

    private String msisdn;
    private String event;
    private Integer productid;
    private String status;
    private Long validTillDate;
    private Boolean autoRenewal;
    private String service;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Integer getProductid() {
        return productid;
    }

    public void setProductid(Integer productid) {
        this.productid = productid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getValidTillDate() {
        return validTillDate;
    }

    public void setValidTillDate(Long validTillDate) {
        this.validTillDate = validTillDate;
    }

    public Boolean getAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void fromJson(String json) throws Exception {
        try {
            JsonElement jsonElement = new JsonParser().parse(json);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            fromJsonObject(jsonObject);
        }catch (Exception ex){

        }
    }

    public void fromJsonObject(JsonObject jsonObj) {
        if (!jsonObj.get("msisdn").isJsonNull()) {
            setMsisdn(jsonObj.get("msisdn").getAsString());
        }
        if (!jsonObj.get("event").isJsonNull()) {
            setEvent(jsonObj.get("event").getAsString());
        }
        if (!jsonObj.get("productid").isJsonNull()) {
            setProductid(jsonObj.get("productid").getAsInt());
        }
        if (!jsonObj.get("status").isJsonNull()) {
            setStatus(jsonObj.get("status").getAsString());
        }
        if (!jsonObj.get("validTillDate").isJsonNull()) {
            setValidTillDate(jsonObj.get("validTillDate").getAsLong());
        }
        if (!jsonObj.get("autoRenewal").isJsonNull()) {
            setAutoRenewal(jsonObj.get("autoRenewal").getAsBoolean());
        }
        if(!jsonObj.get("service").isJsonNull()){
            setService(jsonObj.get("service").getAsString());
        }
    }
}
