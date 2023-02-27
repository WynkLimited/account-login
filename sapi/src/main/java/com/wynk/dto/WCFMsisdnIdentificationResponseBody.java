package com.wynk.dto;

import com.wynk.constants.WCFChannelEnum;
import com.wynk.utils.StringUtils;
import com.wynk.utils.Utils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Created by Aakash on 27/03/17.
 */
public class WCFMsisdnIdentificationResponseBody {

    private String msisdn;
    private WCFOperatorInfo operatorInfo;
    private WCFChannelEnum channel;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public WCFOperatorInfo getOperatorInfo() {
        return operatorInfo;
    }

    public void setOperatorInfo(WCFOperatorInfo operatorInfo) {
        this.operatorInfo = operatorInfo;
    }

    public WCFChannelEnum getChannel() {
        return channel;
    }

    public void setChannel(WCFChannelEnum channel) {
        this.channel = channel;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }

    public void fromJsonObject(JSONObject jsonObj) {
        String msisdn = jsonObj.get("msisdn") != null ? jsonObj.get("msisdn").toString() : "";
        if (StringUtils.isNotBlank(msisdn)) {
            msisdn = Utils.getPrefixedMsisdn(msisdn);
            setMsisdn(msisdn);
        }
        if (jsonObj.get("operatorInfo") != null) {
            String operatorInfoObject = jsonObj.get("operatorInfo").toString();
            WCFOperatorInfo wcfOperatorInfo = new WCFOperatorInfo();
            wcfOperatorInfo.fromJson(operatorInfoObject);
            setOperatorInfo(wcfOperatorInfo);
        }

        if(jsonObj.get("channel") != null){
            String wcfChannel = jsonObj.get("channel").toString();
            setChannel(WCFChannelEnum.getWCFChannelByChannel(wcfChannel));
        }
    }

    @Override
    public String toString() {
        return "WCFMsisdnIdentificationResponseBody{" +
                "msisdn='" + msisdn + '\'' +
                ", operatorInfo=" + operatorInfo +
                '}';
    }
}
