package com.wynk.dto;

import com.wynk.user.dto.DeviceEnityKey;
import com.wynk.utils.ObjectUtils;
import org.json.simple.JSONObject;


/**
 * Created by vivek on 12/04/17.
 */

public class IntlRoaming {

    private long initialProvisionedDate; // date on which initial provisioning had started
    private long provisionedDate; // date on which current provisioning has started
    private boolean intlRoamingEnabled; // current international roaming activated
    private long countConsumedTime; // total count of consumed time while on international roaming
    private long lastConsumptionUpdateTime; // last timestamp of consumed time update to db

    public long getInitialProvisionedDate() {
        return initialProvisionedDate;
    }

    public void setInitialProvisionedDate(long initialProvisionedDate) {
        this.initialProvisionedDate = initialProvisionedDate;
    }

    public long getProvisionedDate() {
        return provisionedDate;
    }

    public void setProvisionedDate(long provisionedDate) {
        this.provisionedDate = provisionedDate;
    }

    public boolean isIntlRoamingEnabled() {
        return intlRoamingEnabled;
    }

    public void setIntlRoamingEnabled(boolean intlRoamingEnabled) {
        this.intlRoamingEnabled = intlRoamingEnabled;
    }

    public long getCountConsumedTime() {
        return countConsumedTime;
    }

    public void setCountConsumedTime(long countConsumedTime) {
        this.countConsumedTime = countConsumedTime;
    }

    public long getLastConsumptionUpdateTime() {
        return lastConsumptionUpdateTime;
    }

    public void setLastConsumptionUpdateTime(long lastConsumptionUpdateTime) {
        this.lastConsumptionUpdateTime = lastConsumptionUpdateTime;
    }

    public void fromJsonObject(JSONObject jsonObj) {
        if(jsonObj.get(DeviceEnityKey.IntlRoaming.initialProvisionedDate) != null) {
            setInitialProvisionedDate(ObjectUtils.getNumber(jsonObj.get(DeviceEnityKey.IntlRoaming.initialProvisionedDate), 0).longValue());
        }
        if(jsonObj.get(DeviceEnityKey.IntlRoaming.provisionedDate) != null){
            setProvisionedDate(ObjectUtils.getNumber(jsonObj.get(DeviceEnityKey.IntlRoaming.provisionedDate), 0).longValue());
        }
        if(jsonObj.get(DeviceEnityKey.IntlRoaming.intlRoamingEnabled) != null){
            setIntlRoamingEnabled((Boolean) jsonObj.get(DeviceEnityKey.IntlRoaming.intlRoamingEnabled));
        }
        if(jsonObj.get(DeviceEnityKey.IntlRoaming.countConsumedTime) != null){
            setCountConsumedTime(ObjectUtils.getNumber(jsonObj.get(DeviceEnityKey.IntlRoaming.countConsumedTime), 0).longValue());
        }
        if(jsonObj.get(DeviceEnityKey.IntlRoaming.lastConsumptionUpdateTime) != null){
            setLastConsumptionUpdateTime(ObjectUtils.getNumber(jsonObj.get(DeviceEnityKey.IntlRoaming.lastConsumptionUpdateTime), 0).longValue());
        }
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(DeviceEnityKey.IntlRoaming.initialProvisionedDate, getInitialProvisionedDate());
        jsonObj.put(DeviceEnityKey.IntlRoaming.provisionedDate, getProvisionedDate());
        jsonObj.put(DeviceEnityKey.IntlRoaming.intlRoamingEnabled, isIntlRoamingEnabled());
        jsonObj.put(DeviceEnityKey.IntlRoaming.countConsumedTime, getCountConsumedTime());
        jsonObj.put(DeviceEnityKey.IntlRoaming.lastConsumptionUpdateTime, getLastConsumptionUpdateTime());
        return jsonObj;
    }
}