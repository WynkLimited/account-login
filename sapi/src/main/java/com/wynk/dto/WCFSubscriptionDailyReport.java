package com.wynk.dto;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by Aakash on 23/01/17.
 */
public class WCFSubscriptionDailyReport {

    private String uid;
    private Long validTillDate;
    private Integer productId;
    private Boolean autoRenewalOff;
    private Long updatedTimestamp;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getValidTillDate() {
        return validTillDate;
    }

    public void setValidTillDate(Long validTillDate) {
        this.validTillDate = validTillDate;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Boolean getAutoRenewalOff() {
        return autoRenewalOff;
    }

    public void setAutoRenewalOff(Boolean autoRenewalOff) {
        this.autoRenewalOff = autoRenewalOff;
    }

    public Long getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(Long updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public void fromJson(String json) {
        try {
            JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
            fromJsonObject(jsonObj);
        }catch (Throwable th){
            th.printStackTrace();
        }

    }

    public void fromJsonObject(JSONObject jsonObj) {
        if(jsonObj.get("uid") != null){
            setUid((String) jsonObj.get("uid"));
        }
        if(jsonObj.get("validTillDate") != null){
            setValidTillDate(Long.parseLong(jsonObj.get("validTillDate").toString()));
        }
        if(jsonObj.get("productId") != null){
            setProductId(Integer.parseInt(jsonObj.get("productId").toString()));
        }
        if(jsonObj.get("autoRenewalOff") != null){
            setAutoRenewalOff(Boolean.parseBoolean(jsonObj.get("autoRenewalOff").toString()));
        }
        if(jsonObj.get("updatedTimestamp") != null){
            setUpdatedTimestamp(Long.parseLong(jsonObj.get("updatedTimestamp").toString()));
        }
    }


    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();

        jsonObj.put("uid", getUid());
        jsonObj.put("validTillDate", getValidTillDate());
        jsonObj.put("productId", getProductId());
        jsonObj.put("autoRenemwalOff",getAutoRenewalOff());
        jsonObj.put("updatedTimestamp",getUpdatedTimestamp());
        return jsonObj;
    }

    @Override
    public String toString() {
        return "WCFSubscriptionDailyReport{" +
                "uid='" + uid + '\'' +
                ", validTillDate=" + validTillDate +
                ", productId=" + productId +
                ", autoRenewalOff=" + autoRenewalOff +
                ", updatedTimestamp=" + updatedTimestamp +
                '}';
    }
}
