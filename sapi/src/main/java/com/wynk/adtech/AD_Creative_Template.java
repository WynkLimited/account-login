package com.wynk.adtech;

import com.wynk.utils.ObjectUtils;
import org.json.simple.JSONObject;

import com.wynk.constants.MusicBuildConstants;
import com.wynk.utils.MusicBuildUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AD_Creative_Template  {

	private String name;
	private String templateId;
	private boolean showToPaid;
	private boolean buildCheckReq;
	private int iosBuildNos;
	private int androidBuildNos;
	private AD_Type type;
	
	AD_Creative_Template(String name, String templateId,boolean showToPaid, boolean buildCheckReq, int iosBuildNos, int androidBuildNos, AD_Type type) {
		this.name = name;
		this.templateId = templateId;
		this.showToPaid=showToPaid;
		this.buildCheckReq = buildCheckReq;
		this.iosBuildNos = iosBuildNos;
		this.androidBuildNos = androidBuildNos;
		this.type = type;
	}

	public String getTemplateId() {
		return templateId;
	}
	
	public AD_Type getType() {
		return type;
	}

	public boolean showToPaidUser(){
		return showToPaid;
	}

	public String toJSONString() {
		JSONObject obj = toJSONObject();
		return obj.toJSONString();
	}

	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("templateId", templateId);
		return obj;
	}

	public int getIosBuildNos() {
		return iosBuildNos;
	}

	public boolean isBuildCheckReq() {
		return buildCheckReq;
	}

	public int getAndroidBuildNos() {
		return androidBuildNos;
	}
	
	public static List<AD_Creative_Template> getTemplateFromAdType(List<AD_Creative_Template> templates, AD_Type type) {
		List<AD_Creative_Template> filteredTemplates = new ArrayList<>();
		for (AD_Creative_Template template : templates) {
			if(!ObjectUtils.isEmpty(template)) {
				if (type.equals(template.getType()))
					filteredTemplates.add(template);
			}
		}
		return filteredTemplates;
	}
	
	public static AD_Creative_Template fromDbConfig(String templateStr) {
		String[] values = templateStr.split(",");
		AD_Creative_Template template = new AD_Creative_Template(values[0],values[1],Boolean.valueOf(values[2]),Boolean.valueOf(values[3]),Integer.valueOf(values[4]),Integer.valueOf(values[5]), AD_Type.fromName(values[6]));
		return template;
	}

}