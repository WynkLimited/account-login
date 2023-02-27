package com.wynk.dto;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class LapuPackDto {
    int productId;
    String successMsg;
    String alreadySubsribedMsg;
    String activationMsg;
    String downloadMsg;
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public String getSuccessMsg() {
        return successMsg;
    }
    
    public void setSuccessMsg(String successMsg) {
        this.successMsg = successMsg;
    }
    
    public String getAlreadySubsribedMsg() {
        return alreadySubsribedMsg;
    }
    
    public void setAlreadySubsribedMsg(String alreadySubsribedMsg) {
        this.alreadySubsribedMsg = alreadySubsribedMsg;
    }
    
    public String getActivationMsg() {
        return activationMsg;
    }
    
    public void setActivationMsg(String activationMsg) {
        this.activationMsg = activationMsg;
    }
    
    public String getDownloadMsg() {
        return downloadMsg;
    }
    
    public void setDownloadMsg(String downloadMsg) {
        this.downloadMsg = downloadMsg;
    }
    
    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();

        jsonObj.put("productId", getProductId());
        jsonObj.put("downloadMsg", getDownloadMsg());
        jsonObj.put("activationMsg", getActivationMsg());
        jsonObj.put("alreadySubsribedMsg", getAlreadySubsribedMsg());
        jsonObj.put("successMsg", getSuccessMsg());
        return jsonObj;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }
    
    public void fromJsonObject(JSONObject jsonObj) {
        if (jsonObj.get("productId") != null) {
            setProductId(((Number) jsonObj.get("productId")).intValue());
        }
        if (jsonObj.get("successMsg") != null) {
            setSuccessMsg((String) jsonObj.get("successMsg"));
        }
        if (jsonObj.get("alreadySubsribedMsg") != null) {
            setAlreadySubsribedMsg((String) jsonObj.get("alreadySubsribedMsg"));
        }
        if (jsonObj.get("activationMsg") != null) {
            setActivationMsg((String) jsonObj.get("activationMsg"));
        }
        if (jsonObj.get("downloadMsg") != null) {
            setDownloadMsg((String) jsonObj.get("downloadMsg"));
        }
    }

}
