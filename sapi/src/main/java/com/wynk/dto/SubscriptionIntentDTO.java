package com.wynk.dto;

import org.json.simple.JSONObject;

/**
 * Created by Aakash on 31/07/17.
 */
public class SubscriptionIntentDTO {

    private String header;
    private String subHeader;
    private String iconUrl;
    private Integer id;

    public SubscriptionIntentDTO(Integer id,String header, String subHeader, String iconUrl) {
        this.id = id;
        this.header = header;
        this.subHeader = subHeader;
        this.iconUrl = iconUrl;
    }

    public String getHeader() {
        return header;
    }

    public String getSubHeader() {
        return subHeader;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public Integer getId(){
        return id;
    }

    public String toJson(){
        JSONObject jsonObject = toJsonObject();
        return jsonObject.toString();
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        if(getHeader() != null){
            jsonObj.put("header",getHeader());
        }
        if(getSubHeader() != null){
            jsonObj.put("subHeader",getSubHeader());
        }
        if(getIconUrl() != null){
            jsonObj.put("iconUrl",getIconUrl());
        }
        return jsonObj;
    }
}
