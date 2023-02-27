package com.wynk.dto;

import static com.wynk.constants.JsonKeyNames.THUMBNAIL;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.wynk.constants.JsonKeyNames;
import com.wynk.constants.MusicConstants;


public class GalaxyPlaylist {
	
	String title;
	String language;
	String deeplinkUrl;
	String thumbnailUrl;
	int count;
	long durationSeconds;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getDeeplinkUrl() {
		return deeplinkUrl;
	}
	public void setDeeplinkUrl(String deeplinkUrl) {
		this.deeplinkUrl = deeplinkUrl;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public long getDurationSeconds() {
		return durationSeconds;
	}
	public void setDurationSeconds(long durationSeconds) {
		this.durationSeconds = durationSeconds;
	}
	
	 public String toJson() {
	        JSONObject jsonObject = toJsonObject();
	        return jsonObject.toString();
	    }
	    
	    public JSONObject toJsonObject() {
	        JSONObject jsonObj = new JSONObject();
	        
	        jsonObj.put("title", getTitle());
	        jsonObj.put("durationSeconds",getDurationSeconds());
	        jsonObj.put("count", getCount());
	        jsonObj.put("deeplinkUrl", getDeeplinkUrl());
	        jsonObj.put("thumbnailUrl", MusicConstants.IMG_WYNK_IN+getThumbnailUrl());
	        return jsonObj;
	    }
	    
	    public void fromJson(String json) throws Exception {
	        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
	        fromJsonObject(jsonObj);
	    }
	    
	    public void fromJsonObject(JSONObject jsonObj) {
	        if(jsonObj.get("title") != null) {
	        	setTitle((String)jsonObj.get("title"));
	        }
	        if(jsonObj.get("durationSeconds") != null) {
	        	setDurationSeconds((long)jsonObj.get("durationSeconds"));
	        }
	        if(jsonObj.get("contentIds") != null) {
	        	 ArrayList contentIds =  (ArrayList) jsonObj.get("contentIds");
	             setCount(contentIds.size());
	        }
	        if(jsonObj.get("id") != null) {
	        	String id = (String) jsonObj.get("id");
	        	setDeeplinkUrl(JsonKeyNames.DEEPLINK_URL+id+".html");
	        }
	        
	        if(jsonObj.get("thumbnailUrl") != null) {
	        	setThumbnailUrl((String)jsonObj.get("thumbnailUrl"));
	        }
	        else
	        	setThumbnailUrl(MusicConstants.DEFAULT_WYNK_IMAGE);

	        if(jsonObj.get("language") != null) {
	        	setLanguage((String)jsonObj.get("language"));
	        }
	        
	    }

}
