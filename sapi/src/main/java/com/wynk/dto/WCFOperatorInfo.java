package com.wynk.dto;

import com.wynk.common.Circle;
import com.wynk.common.Country;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Aakash on 27/03/17.
 */
@Deprecated
public class WCFOperatorInfo {

    private static final Logger logger = LoggerFactory.getLogger(WCFOperatorInfo.class.getCanonicalName());

    private String operator;
    private String circle;
    private String userType;
    private String circleShortName;
    private boolean isCircleDetectedFromMccMnc;
    private String countryCodeMcc;
    private String countryCodeIP;

    public String getCountryCodeIP() {
        return countryCodeIP;
    }

    public void setCountryCodeIP(String countryCodeIP) {
        this.countryCodeIP = countryCodeIP;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getCircle() {
        return circle;
    }

    public String getCountryCodeMcc() {
        return countryCodeMcc;
    }

    public void setCountryCodeMcc(String countryCodeMcc) {
        this.countryCodeMcc = countryCodeMcc;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCircleShortName() {
        return circleShortName;
    }

    public void setCircleShortName(String circleShortName) {
        this.circleShortName = circleShortName;
    }

    public Boolean getCircleDetectedFromMccMnc() {
        return isCircleDetectedFromMccMnc;
    }

    public void setCircleDetectedFromMccMnc(Boolean circleDetectedFromMccMnc) {
        isCircleDetectedFromMccMnc = circleDetectedFromMccMnc;
    }
    public void fromJson(String json) {
        try {
            JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
            fromJsonObject(jsonObj);
        }catch (Throwable th){
            logger.error("exception occurred in parsing Operator Info for {}",json);
        }
    }

    public void fromJsonObject(JSONObject jsonObj) {
        if (jsonObj.get("operator") != null && !jsonObj.get("operator").toString().equalsIgnoreCase("unknown")) {
            setOperator(jsonObj.get("operator").toString());
        }
        if (jsonObj.get("circle") != null && !jsonObj.get("circle").toString().equalsIgnoreCase("unknown")) {
            setCircle(jsonObj.get("circle").toString());
            setCircleShortName(Circle.getCircleShortName(getCircle()));
        }
        if (jsonObj.get("userType") != null) {
            setUserType(jsonObj.get("userType").toString());
        }
    }

    @Override
    public String toString() {
        return "WCFOperatorInfo{" +
                "operator='" + operator + '\'' +
                ", circle='" + circle + '\'' +
                ", userType='" + userType + '\'' +
                ", circleShortName='" + circleShortName + '\'' +
                ", isCircleDetectedFromMccMnc=" + isCircleDetectedFromMccMnc +
                '}';
    }
}
