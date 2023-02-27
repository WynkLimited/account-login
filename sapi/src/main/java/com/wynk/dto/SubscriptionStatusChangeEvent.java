package com.wynk.dto;

import com.wynk.utils.ObjectUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * DTO for creating user state change event journey and add tracking accordingly.
 *
 */
public class SubscriptionStatusChangeEvent {
    long createTimestamp;
    long expireTimestamp;
    boolean changePlan;
    boolean isUnsubscribed;
    boolean autoRenewalOn;
    int productId;
    
    public long getCreateTimestamp() {
        return createTimestamp;
    }
    
    public void setCreateTimestamp(long createTime) {
        this.createTimestamp = createTime;
    }
    
    public boolean isUnsubscribed() {
        return isUnsubscribed;
    }
    
    public void setUnsubscribed(boolean isUnsubscribed) {
        this.isUnsubscribed = isUnsubscribed;
    }
    
    public boolean isAutoRenewalOn() {
        return autoRenewalOn;
    }
    
    public void setAutoRenewalOn(boolean autoRenewalOn) {
        this.autoRenewalOn = autoRenewalOn;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    public String toJson() {
        JSONObject jsonObject = toJsonObject();
        return jsonObject.toString();
    }
    
    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("createTimestamp", getCreateTimestamp());
        jsonObj.put("expireTimestamp", getExpireTimestamp());
        jsonObj.put("changePlan", isChangePlan());
        jsonObj.put("autoRenewal", isAutoRenewalOn());
        jsonObj.put("productId", getProductId());
        jsonObj.put("isUnsubscribed", isUnsubscribed());
        return jsonObj;
    }
    
    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }
    
    public void fromJsonObject(JSONObject jsonObj) {
        if(jsonObj.get("isUnsubscribed") != null) {
            setUnsubscribed((Boolean)jsonObj.get("isUnsubscribed"));
        }
        if(jsonObj.get("autoRenewal") != null) {
            setAutoRenewalOn((Boolean)jsonObj.get("autoRenewal"));
        }
        if(jsonObj.get("productId") != null) {
            setProductId(ObjectUtils.getNumber(jsonObj.get("productId"), 0).intValue());
        }
        if(jsonObj.get("createTimestamp") != null) {
            setCreateTimestamp(ObjectUtils.getNumber(jsonObj.get("createTimestamp"), 0).longValue());
        }
        if(jsonObj.get("expireTimestamp") != null) {
            setExpireTimestamp(ObjectUtils.getNumber(jsonObj.get("expireTimestamp"), 0).longValue());
        }
        if(jsonObj.get("changePlan") != null) {
            setChangePlan((Boolean)jsonObj.get("changePlan"));
        }
    }

    public long getExpireTimestamp() {
        return expireTimestamp;
    }
    
    public void setExpireTimestamp(long expireTimestamp) {
        this.expireTimestamp = expireTimestamp;
    }
    
    public boolean isChangePlan() {
        return changePlan;
    }

    public void setChangePlan(boolean changePlan) {
        this.changePlan = changePlan;
    }

}
