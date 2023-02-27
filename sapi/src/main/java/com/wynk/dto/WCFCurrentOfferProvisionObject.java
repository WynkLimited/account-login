package com.wynk.dto;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aakash on 03/03/17.
 */
public class WCFCurrentOfferProvisionObject {

    private List<WCFOfferProvisionResponseObject> offerStatus;
    private List<WCFCurrentSubscriptionStatus> subscriptionStatus;

    public List<WCFOfferProvisionResponseObject> getOfferStatus() {
        return offerStatus;
    }

    public void setOfferStatus(List<WCFOfferProvisionResponseObject> offerStatus) {
        this.offerStatus = offerStatus;
    }

    public List<WCFCurrentSubscriptionStatus> getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(List<WCFCurrentSubscriptionStatus> subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }

    public void fromJsonObject(JSONObject jsonObj) throws Exception{
        if(jsonObj.get("offerStatus") != null){
            JSONArray offerStatusArray = (JSONArray) jsonObj.get("offerStatus");
            List<WCFOfferProvisionResponseObject> wcfOfferProvisionResponseObjectList = new ArrayList<>();
            if(offerStatusArray != null) {
                for(int k = 0; k < offerStatusArray.size(); k++) {
                    JSONObject offerObject = (JSONObject) offerStatusArray.get(k);
                    WCFOfferProvisionResponseObject wcfOfferProvisionResponseObject = new WCFOfferProvisionResponseObject();
                    wcfOfferProvisionResponseObject.fromJson(offerObject.toString());
                    wcfOfferProvisionResponseObjectList.add(wcfOfferProvisionResponseObject);
                }
                setOfferStatus(wcfOfferProvisionResponseObjectList);
            }
        }

        if(jsonObj.get("subscriptionStatus") != null){
            JSONArray subscriptionStatusArray = (JSONArray) jsonObj.get("subscriptionStatus");
            List<WCFCurrentSubscriptionStatus> wcfCurrentSubscriptionStatusList = new ArrayList<>();
            if(subscriptionStatusArray != null) {
                for(int k = 0; k < subscriptionStatusArray.size(); k++) {
                    JSONObject subscriptionObject = (JSONObject) subscriptionStatusArray.get(k);
                    WCFCurrentSubscriptionStatus wcfCurrentSubscriptionStatus = new WCFCurrentSubscriptionStatus();
                    wcfCurrentSubscriptionStatus.fromJson(subscriptionObject.toString());
                    wcfCurrentSubscriptionStatusList.add(wcfCurrentSubscriptionStatus);
                }
                setSubscriptionStatus(wcfCurrentSubscriptionStatusList);
            }
        }
    }
}
