package com.wynk.dto;

import com.wynk.user.dto.DeviceEnityKey;
import com.wynk.utils.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by Aakash on 20/09/17.
 */
public class WCFRegistrationChannel {

    private String channelName;
    private Boolean isVerifiedUser;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Boolean getVerifiedUser() {
        return isVerifiedUser;
    }

    public void setVerifiedUser(Boolean verifiedUser) {
        isVerifiedUser = verifiedUser;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }

    public void fromJsonObject(JSONObject jsonObj) {
        if (jsonObj.get(DeviceEnityKey.WCFRegistrationChannel.channelName) != null) {
            setChannelName(jsonObj.get(DeviceEnityKey.WCFRegistrationChannel.channelName).toString());
        }

        if (jsonObj.get(DeviceEnityKey.WCFRegistrationChannel.isVerifiedUser) != null) {
            setVerifiedUser(Boolean.parseBoolean(jsonObj.get(DeviceEnityKey.WCFRegistrationChannel.isVerifiedUser).toString()));
        }
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        if(StringUtils.isNotBlank(getChannelName())) {
            jsonObj.put(DeviceEnityKey.WCFRegistrationChannel.channelName, getChannelName());
        }
        if(getVerifiedUser() != null) {
            jsonObj.put(DeviceEnityKey.WCFRegistrationChannel.isVerifiedUser, getVerifiedUser());
        }
        return jsonObj;
    }
}
