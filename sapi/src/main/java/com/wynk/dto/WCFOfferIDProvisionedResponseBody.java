package com.wynk.dto;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by Aakash on 01/08/17.
 */
public class WCFOfferIDProvisionedResponseBody {

    private Integer offerId;
    private String status;
    private String tid;
    private Long validTillTimestamp;
    private Integer noOfTimesAvailed;

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }

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

    public Long getValidTillTimestamp() {
        return validTillTimestamp;
    }

    public void setValidTillTimestamp(Long validTillTimestamp) {
        this.validTillTimestamp = validTillTimestamp;
    }

    public Integer getNoOfTimesAvailed() {
        return noOfTimesAvailed;
    }

    public void setNoOfTimesAvailed(Integer noOfTimesAvailed) {
        this.noOfTimesAvailed = noOfTimesAvailed;
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
        if(jsonObj.get("noOfTimesAvailed") != null){
            setNoOfTimesAvailed(Integer.parseInt(jsonObj.get("noOfTimesAvailed").toString()));
        }
    }

    @Override
    public String toString() {
        return "WCFOfferIDProvisionedResponseBody{" +
                "offerId=" + offerId +
                ", status='" + status + '\'' +
                ", tid='" + tid + '\'' +
                ", validTillTimestamp=" + validTillTimestamp +
                ", noOfTimesAvailed=" + noOfTimesAvailed +
                '}';
    }
}
