package com.wynk.dto;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ContentForKafkaQueue {
	String id;
	String type;
	String state;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	 public String toJson() {
	        JSONObject jsonObject = toJsonObject();
	        return jsonObject.toString();
	    }
	    
	    public JSONObject toJsonObject() {
	        JSONObject jsonObj = new JSONObject();
	        jsonObj.put("id", getId());
	        jsonObj.put("type", getType());
	        jsonObj.put("state", getState());
	        return jsonObj;
	    }
	
	    public void fromJson(String json) throws Exception {
	        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
	        fromJsonObject(jsonObj);
	    }
	    
	public void fromJsonObject(JSONObject jsonObj) {
		if (jsonObj.get("id") != null) {
			setId((String) jsonObj.get("id"));
		}
		if (jsonObj.get("type") != null) {
			setType((String) jsonObj.get("type"));
		}
		if (jsonObj.get("state") != null) {
			setState((String) jsonObj.get("state"));
		}

	}
	    
	

}
