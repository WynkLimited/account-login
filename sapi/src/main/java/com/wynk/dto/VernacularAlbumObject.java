package com.wynk.dto;


 import org.apache.commons.lang.StringUtils;
 import org.json.simple.JSONObject;

public class VernacularAlbumObject extends VernacularBaseObject {
    private String publisher;

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public JSONObject toJson(){
        JSONObject jsonObj = super.toJson();

        if(!StringUtils.isEmpty(getPublisher()))
            jsonObj.put("publisher", getPublisher());
        return jsonObj;
    }

    public void fromJsonObject(JSONObject jsonObj) {
        super.fromJsonObject(jsonObj);
        if(jsonObj.get("publisher") instanceof String) {
            setPublisher((String) jsonObj.get("publisher"));
        }
    }
}
