package com.wynk.service.dto;

import com.wynk.dto.BaseObject;
import com.wynk.utils.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by a1rapiur on 28/04/17.
 */
public class IntlRoamingConfigMetaData extends BaseObject {

    private double confId;
    private Set<String> intlRoamingBlacklist = new HashSet<>();
    private Set<String> indianCarriersList = new HashSet<>();
    private Set<String> indianMCCList = new HashSet<>();
    private Long intlRoamingCycle;
    private Long intlRoamingQuota;
    private Long intlRoamingExpiryNotif;
    private Boolean intlRoamingEnabled = false;
    private Boolean intlRoamingFreeTierCheck;
    private Integer intlRoamingAndroidVersionsToUpgrade;
    private Integer intlRoamingIosVersionsToUpgrade;

    public double getConfId() {
        return confId;
    }

    public void setConfId(double confId) {
        this.confId = confId;
    }

    public Set<String> getIntlRoamingBlacklist() {
        return intlRoamingBlacklist;
    }

    public void setIntlRoamingBlacklist(Set<String> intlRoamingBlacklist) {
        this.intlRoamingBlacklist = intlRoamingBlacklist;
    }

    public Set<String> getIndianCarriersList() {
        return indianCarriersList;
    }

    public void setIndianCarriersList(Set<String> indianCarriersList) {
        this.indianCarriersList = indianCarriersList;
    }

    public Set<String> getIndianMCCList() {
        return indianMCCList;
    }

    public void setIndianMCCList(Set<String> indianMCCList) {
        this.indianMCCList = indianMCCList;
    }

    public Long getIntlRoamingCycle() {
        return intlRoamingCycle;
    }

    public void setIntlRoamingCycle(Long intlRoamingCycle) {
        this.intlRoamingCycle = intlRoamingCycle;
    }

    public Long getIntlRoamingQuota() {
        return intlRoamingQuota;
    }

    public void setIntlRoamingQuota(Long intlRoamingQuota) {
        this.intlRoamingQuota = intlRoamingQuota;
    }

    public Long getIntlRoamingExpiryNotif() {
        return intlRoamingExpiryNotif;
    }

    public void setIntlRoamingExpiryNotif(Long intlRoamingExpiryNotif) {
        this.intlRoamingExpiryNotif = intlRoamingExpiryNotif;
    }

    public Boolean getIntlRoamingEnabled() {
        return intlRoamingEnabled;
    }

    public void setIntlRoamingEnabled(Boolean intlRoamingEnabled) {
        this.intlRoamingEnabled = intlRoamingEnabled;
    }

    public Boolean getIntlRoamingFreeTierCheck() {
        return intlRoamingFreeTierCheck;
    }

    public void setIntlRoamingFreeTierCheck(Boolean intlRoamingFreeTierCheck) {
        this.intlRoamingFreeTierCheck = intlRoamingFreeTierCheck;
    }

    public Integer getIntlRoamingAndroidVersionsToUpgrade() {
        return intlRoamingAndroidVersionsToUpgrade;
    }

    public void setIntlRoamingAndroidVersionsToUpgrade(Integer intlRoamingAndroidVersionsToUpgrade) {
        this.intlRoamingAndroidVersionsToUpgrade = intlRoamingAndroidVersionsToUpgrade;
    }

    public Integer getIntlRoamingIosVersionsToUpgrade() {
        return intlRoamingIosVersionsToUpgrade;
    }

    public void setIntlRoamingIosVersionsToUpgrade(Integer intlRoamingIosVersionsToUpgrade) {
        this.intlRoamingIosVersionsToUpgrade = intlRoamingIosVersionsToUpgrade;
    }

    @Override
    public String toJson()
            throws Exception {
        JSONObject obj = toJsonObject();
        return obj.toJSONString();
    }

    @Override
    public void fromJson(String json) throws Exception {
        Object obj = JSONValue.parseWithException(json);
        JSONObject valueMap = (JSONObject) obj;
        fromJsonObject(valueMap);
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject jsonObject = super.toJsonObject();

        jsonObject.put("confId",getConfId());
        jsonObject.put("intlRoamingBlacklist", Utils.convertToJSONArray(getIntlRoamingBlacklist()));
        jsonObject.put("indianCarriersList", Utils.convertToJSONArray(getIndianCarriersList()));
        jsonObject.put("indianMCCList", Utils.convertToJSONArray(getIndianMCCList()));
        jsonObject.put("intlRoamingCycle", getIntlRoamingCycle());
        jsonObject.put("intlRoamingQuota", getIntlRoamingQuota());
        jsonObject.put("intlRoamingExpiryNotif", getIntlRoamingExpiryNotif());
        jsonObject.put("intlRoamingEnabled",getIntlRoamingEnabled());
        jsonObject.put("intlRoamingFreeTierCheck", getIntlRoamingFreeTierCheck());
        jsonObject.put("intlRoamingAndroidVersionsToUpgrade",getIntlRoamingAndroidVersionsToUpgrade());
        jsonObject.put("intlRoamingIosVersionsToUpgrade", getIntlRoamingIosVersionsToUpgrade());

        return jsonObject;
    }

    @Override
    public void fromJsonObject(JSONObject jsonObj) {
        super.fromJsonObject(jsonObj);

        if (null != jsonObj.get("confId")) {
            confId = (Double) jsonObj.get("confId");
        }
        if (null != jsonObj.get("intlRoamingBlacklist")) {
            intlRoamingBlacklist = Utils.convertToStringSet((JSONArray) jsonObj.get("intlRoamingBlacklist"));
        }
        if (null != jsonObj.get("indianCarriersList")) {
            indianCarriersList = Utils.convertToStringSet((JSONArray) jsonObj.get("indianCarriersList"));
        }
        if (null != jsonObj.get("indianMCCList")) {
            indianMCCList = Utils.convertToStringSet((JSONArray) jsonObj.get("indianMCCList"));
        }
        if (null != jsonObj.get("intlRoamingCycle")) {
            intlRoamingCycle = (Long) jsonObj.get("intlRoamingCycle");
        }
        if (null != jsonObj.get("intlRoamingQuota")) {
            intlRoamingQuota = (Long) jsonObj.get("intlRoamingQuota");
        }
        if (null != jsonObj.get("intlRoamingEnabled")) {
            intlRoamingEnabled = (Boolean) jsonObj.get("intlRoamingEnabled");
        }
        if (null != jsonObj.get("intlRoamingFreeTierCheck")) {
            intlRoamingFreeTierCheck = (Boolean) jsonObj.get("intlRoamingFreeTierCheck");
        }
        if (null != jsonObj.get("intlRoamingAndroidVersionsToUpgrade")) {
            intlRoamingAndroidVersionsToUpgrade = (Integer) jsonObj.get("intlRoamingAndroidVersionsToUpgrade");
        }
        if (null != jsonObj.get("intlRoamingIosVersionsToUpgrade")) {
            intlRoamingIosVersionsToUpgrade = (Integer) jsonObj.get("intlRoamingIosVersionsToUpgrade");
        }
    }
}
