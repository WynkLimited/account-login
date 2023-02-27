package com.wynk.dto;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by Aakash on 20/09/17.
 */
public class WCFCarrierBillingResponseBody {

    private Integer partnerProductId;
    private Long vtd;
    private String status;

    public Integer getPartnerProductId() {
        return partnerProductId;
    }

    public void setPartnerProductId(Integer partnerProductId) {
        this.partnerProductId = partnerProductId;
    }

    public Long getVtd() {
        return vtd;
    }

    public void setVtd(Long vtd) {
        this.vtd = vtd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }

    public void fromJsonObject(JSONObject jsonObj) throws Exception{
        if(jsonObj.get("partnerProductId") != null) {
            setPartnerProductId(Integer.parseInt(jsonObj.get("partnerProductId").toString()));
        }

        if(jsonObj.get("vtd") != null) {
            setVtd(Long.parseLong(jsonObj.get("vtd").toString()));
        }

        if(jsonObj.get("status") != null) {
            setStatus(jsonObj.get("status").toString());
        }
    }
}
