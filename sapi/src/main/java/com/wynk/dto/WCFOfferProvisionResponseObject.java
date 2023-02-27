package com.wynk.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by aakashkumar on 21/11/16.
 */
public class WCFOfferProvisionResponseObject {

    private String action;
    private String offerId;
    private String offerProductId;
    private String offerPackId;
    private String title;
    private String description;
    private String type;
    private Boolean isAvailableOnRegisteredUser;
    private String src;
    private Long expireTimestamp;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getAvailableOnRegisteredUser() {
        return isAvailableOnRegisteredUser;
    }

    public void setAvailableOnRegisteredUser(Boolean availableOnRegisteredUser) {
        isAvailableOnRegisteredUser = availableOnRegisteredUser;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public Long getExpireTimestamp() {
        return expireTimestamp;
    }

    public void setExpireTimestamp(Long expireTimestamp) {
        this.expireTimestamp = expireTimestamp;
    }

    public String getOfferProductId() {
        return offerProductId;
    }

    public void setOfferProductId(String offerProductId) {
        this.offerProductId = offerProductId;
    }

    public String getOfferPackId() {
        return offerPackId;
    }

    public void setOfferPackId(String offerPackId) {
        this.offerPackId = offerPackId;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }

    public void fromJsonObject(JSONObject jsonObj) throws Exception {

        if(jsonObj.get("action") != null) {
            setAction(jsonObj.get("action").toString());
        }

        if(jsonObj.get("offerId") != null) {
            setOfferId(jsonObj.get("offerId").toString());
        }

        if(jsonObj.get("isAvailableOnRegisteredUser") != null){
            setAvailableOnRegisteredUser(Boolean.valueOf(jsonObj.get("isAvailableOnRegisteredUser").toString()));
        }

        if(jsonObj.get("title") != null){
            setTitle(jsonObj.get("title").toString());
        }

        if(jsonObj.get("src") != null){
            setSrc(jsonObj.get("src").toString());
        }

        if(jsonObj.get("type") != null) {
            setType(jsonObj.get("type").toString());
        }

        if(jsonObj.get("description") != null){
            setDescription(jsonObj.get("description").toString());
        }

        if(jsonObj.get("packs")!= null){
            JsonParser jsonParser = new JsonParser();
            JsonArray arrayObj =(JsonArray)jsonParser.parse(jsonObj.get("packs").toString());

            for(int i =0; i< arrayObj.size(); i++){
                JsonObject packJsonObj = arrayObj.get(i).getAsJsonObject();
                if(packJsonObj.get("id") != null) {
                    setOfferProductId(packJsonObj.get("id").getAsString());
                }
                if(packJsonObj.get("partnerId") != null) {
                    setOfferPackId(packJsonObj.get("partnerId").getAsString());
                }
                if(packJsonObj.get("title")!= null){
                    setTitle(packJsonObj.get("title").getAsString());
                }
                if(packJsonObj.get("validTillDate")!= null){
                    setExpireTimestamp(packJsonObj.get("validTillDate").getAsLong());
                }

            }
        }
    }


    @Override
    public String toString() {
        return "WCFOfferProvisionResponseObject{" +
                "action='" + action + '\'' +
                ", offerId='" + offerId + '\'' +
                ", offerProductId='" + offerProductId + '\'' +
                ", offerPackId='" + offerPackId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", isAvailableOnRegisteredUser=" + isAvailableOnRegisteredUser +
                ", src='" + src + '\'' +
                ", expireTimestamp=" + expireTimestamp +
                '}';
    }
}
