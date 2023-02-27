package com.wynk.dto;

import org.json.simple.JSONObject;

public class PostpaidTempSubscription extends Subscription {
    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("msisdn", msisdn);
        jsonObj.put("productId", productId);
        jsonObj.put("lastSubscribedTimestamp", lastSubscribedTimestamp);
        jsonObj.put("isSuspended", isSuspended);
        jsonObj.put("isDeprovisioned", isDeprovisioned);
        jsonObj.put("deprovisioningTimestamp", deprovisioningTimestamp);
        jsonObj.put("autoRenewalOff", autoRenewalOff);

        return jsonObj;
    }

    public String toJson() {
        JSONObject jsonObj = toJsonObject();
        return jsonObj.toString();
    }
}
