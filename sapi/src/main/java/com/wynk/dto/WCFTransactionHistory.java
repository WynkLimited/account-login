package com.wynk.dto;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by aakashkumar on 22/11/16.
 */
public class WCFTransactionHistory {

    private static final Logger logger = LoggerFactory.getLogger(WCFTransactionHistory.class.getCanonicalName());

    private Integer productId;
    private String uid;
    private String msisdn;
    private String transactionId;
    private Integer retryCount;
    private Long creationDate;
    private String payload;
    private String serviceType;
    private Boolean isOtpRequired;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Boolean getOtpRequired() {
        return isOtpRequired;
    }

    public void setOtpRequired(Boolean otpRequired) {
        isOtpRequired = otpRequired;
    }

    public String toJson() throws Exception {
        JSONObject jsonObject = toJsonObject();
        return jsonObject.toString();
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("productId", getProductId());
        jsonObj.put("uid", getUid());
        jsonObj.put("msisdn", getMsisdn());
        jsonObj.put("transactionId", getTransactionId());
        jsonObj.put("retryCount", getRetryCount());
        jsonObj.put("creationDate", getCreationDate());
        jsonObj.put("payload",getPayload());
        jsonObj.put("serviceType",getServiceType());
        if(getOtpRequired() != null){
            jsonObj.put("isOtpRequired",getOtpRequired());
        }
        return jsonObj;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }

    public void fromJsonObject(JSONObject jsonObj) {
        if(jsonObj.get("productId") != null) {
            setProductId(Integer.parseInt(jsonObj.get("productId").toString()));
        }

        if(jsonObj.get("uid") != null) {
            setUid(jsonObj.get("uid").toString());
        }

        if(jsonObj.get("msisdn") != null) {
            setMsisdn(jsonObj.get("msisdn").toString());
        }

        if(jsonObj.get("transactionId") != null) {
            setTransactionId(jsonObj.get("transactionId").toString());
        }

        if(jsonObj.get("retryCount") != null) {
            setRetryCount(Integer.parseInt(jsonObj.get("retryCount").toString()));
        }

        if(jsonObj.get("creationDate") != null) {
            setCreationDate(Long.parseLong(jsonObj.get("creationDate").toString()));
        }

        if(jsonObj.get("payload") != null) {
            setPayload(jsonObj.get("payload").toString());
        }

        if(jsonObj.get("serviceType") != null) {
            setServiceType(jsonObj.get("serviceType").toString());
        }

        setOtpRequired(Boolean.FALSE);
        if(jsonObj.get("isOtpRequired") != null) {
            setOtpRequired(Boolean.parseBoolean(jsonObj.get("isOtpRequired").toString()));
        }
    }
}
