package com.wynk.dto;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

public class VernacularBaseObject {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public JSONObject toJson(){
        JSONObject jsonObj = new JSONObject();

        if(!StringUtils.isEmpty(getTitle()))
            jsonObj.put("title", getTitle());
        return jsonObj;
    }

    public void fromJsonObject(JSONObject jsonObj) {
        if(jsonObj.get("title") instanceof String) {
            setTitle((String) jsonObj.get("title"));
        }
    }
}
