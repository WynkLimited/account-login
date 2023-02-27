package com.wynk.dto;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Aakash on 18/04/17.
 */
public class ThirdPartyNotifyDTO {

    private static final Logger logger = LoggerFactory.getLogger(ThirdPartyNotifyDTO.class.getCanonicalName());

    private String service;
    private String uid;
    private String email;
    private String msisdn;
    private Boolean isIOSDevice;
    private Long expireTimestamp;
    private Boolean paid;
    private Boolean autoRenewal;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Boolean getIOSDevice() {
        return isIOSDevice;
    }

    public void setIOSDevice(Boolean IOSDevice) {
        isIOSDevice = IOSDevice;
    }

    public Long getExpireTimestamp() {
        return expireTimestamp;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Boolean getAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }

    public void setExpireTimestamp(Long expireTimestamp) {
        this.expireTimestamp = expireTimestamp;
    }

    public void fromJson(String json){
        try {
            JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
            fromJsonObject(jsonObj);
        }catch (Throwable th){
            logger.error("Exception in parsing the json",th);
        }
    }

    public void fromJsonObject(JSONObject jsonObj) {
        if(jsonObj.get("service") != null){
            setService(jsonObj.get("service").toString());
        }

        if(jsonObj.get("uid") != null){
            setUid(jsonObj.get("uid").toString());
        }

        if(jsonObj.get("email") != null){
            setEmail(jsonObj.get("email").toString());
        }

        if(jsonObj.get("msisdn") != null){
            setMsisdn(jsonObj.get("msisdn").toString());
        }

        if(jsonObj.get("isIOSDevice") != null){
            setIOSDevice(Boolean.parseBoolean(jsonObj.get("isIOSDevice").toString()));
        }

        if(jsonObj.get("expireTimestamp") != null){
            setExpireTimestamp(Long.parseLong(jsonObj.get("expireTimestamp").toString()));
        }
        if(jsonObj.get("paid") != null){
            setPaid(Boolean.parseBoolean(jsonObj.get("paid").toString()));
        }
        if(jsonObj.get("autoRenewal") != null){
            setAutoRenewal(Boolean.parseBoolean(jsonObj.get("autoRenewal").toString()));
        }
    }

    public JSONObject toJsonObject(){
        JSONObject jsonObject = new JSONObject();

        if(StringUtils.isNotBlank(getService())){
            jsonObject.put("service", getService());
        }
        if(StringUtils.isNotBlank(getUid())){
            jsonObject.put("uid", getUid());
        }
        if(StringUtils.isNotBlank(getMsisdn())){
            jsonObject.put("msisdn", getMsisdn());
        }
        if(StringUtils.isNotBlank(getEmail())){
            jsonObject.put("email", getEmail());
        }
        if(getIOSDevice() != null){
            jsonObject.put("isIOSDevice",getIOSDevice());
        }
        if(getExpireTimestamp() != null){
            jsonObject.put("expireTimestamp",getExpireTimestamp());
        }
        if(getAutoRenewal() != null){
            jsonObject.put("autoRenewal",getAutoRenewal());
        }
        if(getPaid() != null){
            jsonObject.put("paid",getPaid());
        }
        return jsonObject;
    }
}
