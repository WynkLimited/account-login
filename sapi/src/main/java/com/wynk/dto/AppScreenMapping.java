package com.wynk.dto;

import com.wynk.music.constants.MusicContentType;
import com.wynk.common.ScreenCode;
import org.json.simple.JSONObject;

public class AppScreenMapping {
	
	private ScreenCode screenCode;
	private MusicContentType targetContentType;
	private String targetContentId;

	public MusicContentType getTargetContentType() {
		return targetContentType;
	}

	public void setTargetContentType(MusicContentType targetContentType) {
		this.targetContentType = targetContentType;
	}

	public String getTargetContentId() {
		return targetContentId;
	}

	public void setTargetContentId(String targetContentId) {
		this.targetContentId = targetContentId;
	}

	public ScreenCode getScreenCode() {
		return screenCode;
	}

	public void setScreenCode(ScreenCode screenCode) {
		this.screenCode = screenCode;
	}

	public JSONObject toJsonObject(){
		JSONObject obj = new JSONObject();
		
		if (screenCode != null)
			obj.put("screenCode", screenCode.getOpcode());
		if(targetContentType != null)
			obj.put("targetContentType", targetContentType.name());
		if(targetContentId != null)
			obj.put("targetContentId", targetContentId);
		
		return obj;
	}

	public void fromJsonObject(JSONObject jsonObject) {
		if(jsonObject.get("targetContentId") != null) {
			setTargetContentId((String) jsonObject.get("targetContentId"));
		}
		if(jsonObject.get("targetContentType") != null) {
			setTargetContentType(MusicContentType.getContentTypeId((String) jsonObject.get("targetContentType")));
		}
		if(jsonObject.get("targetScreen") != null) {
			setScreenCode(ScreenCode.getScreenCodeByName((String) jsonObject.get("targetScreen")));
		}
	}

	public void fromCMSJsonObject(JSONObject jsonObj) {
		if(jsonObj.get("target_content_id") != null) {
			setTargetContentId((String) jsonObj.get("target_content_id"));
		}
		if(jsonObj.get("target_screen") != null) {
			setScreenCode(ScreenCode.getScreenCodeByName((String) jsonObj.get("target_screen")));
		}
		if(jsonObj.get("target_content_type") != null) {
			setTargetContentType(MusicContentType.getContentTypeId((String) jsonObj.get("target_content_type")));
		}
	}

	public AppScreenMapping() {

	}

	public AppScreenMapping(ScreenCode screenCode) {
		this.screenCode = screenCode;
	}
}