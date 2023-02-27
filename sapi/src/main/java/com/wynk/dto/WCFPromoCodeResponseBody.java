package com.wynk.dto;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by Aakash on 11/08/17.
 */
public class WCFPromoCodeResponseBody {

    private String status;
    private String tid;
    private String reason;
    private Integer productId;
    private Long validTillDate;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public void fromJson(String json) {
        try {
            JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
            fromJsonObject(jsonObj);
        }catch (Throwable th){
            th.printStackTrace();
        }

    }

    public void fromJsonObject(JSONObject jsonObj) {
        if(jsonObj.get("status") != null){
            setStatus(jsonObj.get("status").toString());
        }
        if(jsonObj.get("tid") != null){
            setTid(jsonObj.get("tid").toString());
        }
        if(jsonObj.get("productId") != null){
            setProductId(Integer.parseInt(jsonObj.get("productId").toString()));
        }
        if(jsonObj.get("reason") != null){
            setReason(jsonObj.get("reason").toString());
        }
        if(jsonObj.get("validTillDate") != null){
            setValidTillDate(Long.parseLong(jsonObj.get("validTillDate").toString()));
        }
    }

    @Override
    public String toString() {
        return "WCFPromoCodeResponseBody{" +
                "status='" + status + '\'' +
                ", tid='" + tid + '\'' +
                ", reason='" + reason + '\'' +
                ", productId=" + productId +
                ", validTillDate=" + validTillDate +
                '}';
    }
}
