package com.wynk.dto;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class SpecialOfferDay {

	String offerDay;
	String offerDate;

	public JSONObject toJsonObject() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("offerDay", getOfferDay());
		jsonObj.put("offerDate", getOfferDate());
		return jsonObj;
	}

	public void fromJson(String json) throws Exception {
		Object obj = JSONValue.parseWithException(json);
		JSONObject valueMap = (JSONObject) obj;
		fromJsonObject(valueMap);
	}

	public void fromJsonObject(JSONObject jsonObj) {

		if (jsonObj.get("offerDay") != null) {
			setOfferDay((String) jsonObj.get("offerDay"));
		}
		if (jsonObj.get("offerDate") != null) {
			setOfferDate((String) jsonObj.get("offerDate"));
		}
	}

	public String getOfferDay() {
		return offerDay;
	}

	public void setOfferDay(String offerDay) {
		this.offerDay = offerDay;
	}

	public String getOfferDate() {
		return offerDate;
	}

	public void setOfferDate(String offerDate) {
		this.offerDate = offerDate;
	}

}
