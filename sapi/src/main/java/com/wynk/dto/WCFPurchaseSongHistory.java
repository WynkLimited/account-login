package com.wynk.dto;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Aakash on 14/03/17.
 */
public class WCFPurchaseSongHistory {

    private static final Logger logger = LoggerFactory.getLogger(WCFPurchaseSongHistory.class.getCanonicalName());

    private String transactionId;
    private String orderId;
    private String price;
    private String contentId;
    private String contentType;
    private String contentTitle;
    private Boolean isSamsung;
    private Boolean isWapUser;
    private String userId;
    private Integer src;
    private Long creationDate;
    private Integer retryCount;
    private String serviceType;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public Boolean getSamsung() {
        return isSamsung;
    }

    public void setSamsung(Boolean samsung) {
        isSamsung = samsung;
    }

    public Boolean getWapUser() {
        return isWapUser;
    }

    public void setWapUser(Boolean wapUser) {
        isWapUser = wapUser;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getSrc() {
        return src;
    }

    public void setSrc(Integer src) {
        this.src = src;
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

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String toJson() throws Exception {
        JSONObject jsonObject = toJsonObject();
        return jsonObject.toString();
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();

        jsonObj.put("transactionId",getTransactionId());
        jsonObj.put("orderId",getOrderId());
        jsonObj.put("price",getPrice());
        jsonObj.put("contentId",getContentId());
        jsonObj.put("contentType",getContentType());
        jsonObj.put("contentTitle",getContentTitle());
        jsonObj.put("isSamsung",getSamsung());
        jsonObj.put("isWapUser",getWapUser());
        jsonObj.put("userId",getUserId());
        jsonObj.put("src",getSrc());
        jsonObj.put("creationDate",getCreationDate());
        jsonObj.put("retryCount",getRetryCount());
        jsonObj.put("serviceType",getServiceType());
        return jsonObj;
    }
}
