package com.wynk.common;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

public class OperatorCircle {

	private Circle cirle;
	private String operator;
	
	public Circle getCirle() {
		return cirle;
	}
	public void setCirle(Circle cirle) {
		this.cirle = cirle;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}

	public void fromJSONObject(JSONObject json){
		if(json==null)
			return;
		if(json.get("operator")!=null){
			setOperator((String) json.get("operator"));
		}
		if(json.get("circle")!=null){
			String circleName = (String) json.get("circle");
			String circleId = Circle.getCircleShortName(circleName);
			if(StringUtils.isNotBlank(circleId))
				setCirle(Circle.getCircleById(circleId));
		}
	}
}
