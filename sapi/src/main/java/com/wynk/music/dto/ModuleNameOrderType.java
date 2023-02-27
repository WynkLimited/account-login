package com.wynk.music.dto;

import com.wynk.music.constants.MusicPackageType;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import static com.wynk.constants.JsonKeyNames.*;

/**
 * Created by anurag on 8/7/15.
 */
public class ModuleNameOrderType {

	private MusicPackageType packageType;
	private String label;
	private int order;
	private boolean shuffle;

	public ModuleNameOrderType() {}

	public ModuleNameOrderType(MusicPackageType packageType, String label) {
		this.packageType = packageType;
		this.label = label;
	}

	public JSONObject toJsonObject() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("packageType", getPackageType().getName());
		jsonObj.put(LABEL, getLabel());
		jsonObj.put(ORDER, getOrder());
		//jsonObj.put(SHUFFLE,isShuffle());
		return jsonObj;
	}

	public void fromJson(String json) throws Exception {
		JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
		fromJsonObject(jsonObj);
	}

	public void fromJsonObject(JSONObject jsonObj) {

		if(jsonObj.get(ORDER) != null)
			setOrder(((Number) jsonObj.get(ORDER)).intValue());

		if(jsonObj.get(LABEL) != null)
			setLabel((String) jsonObj.get(LABEL));

		if(jsonObj.get("packageType") != null) {
			String packType = (String)jsonObj.get("packageType");
			setPackageType(MusicPackageType.getPackageType(packType));
		}

		if(jsonObj.get(SHUFFLE) != null)
			setShuffle((Boolean)jsonObj.get(SHUFFLE));
	}

	public MusicPackageType getPackageType() {
		return packageType;
	}

	public void setPackageType(MusicPackageType packageType) {
		this.packageType = packageType;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isShuffle() {
		return shuffle;
	}

	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}
}
