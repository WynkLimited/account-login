package com.wynk.dto;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by a1vlqlyy on 22/02/17.
 */
public class SubscriptionStatusCheck {

    private Integer productId;
    private Long vtd;
    private Boolean active;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Long getVtd() {
        return vtd;
    }

    public void setVtd(Long vtd) {
        this.vtd = vtd;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }

    public void fromJsonObject(JSONObject jsonObj) throws Exception {
        if(jsonObj.get("productId") != null) {
            setProductId(Integer.parseInt(jsonObj.get("productId").toString()));
        }

        if(jsonObj.get("vtd") != null) {
            setVtd(Long.parseLong(jsonObj.get("vtd").toString()));
        }

        if(jsonObj.get("active") != null){
            setActive(Boolean.parseBoolean(jsonObj.get("active").toString()));
        }
    }
}
