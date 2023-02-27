package com.wynk.constants;

import com.wynk.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aakash on 20/09/17.
 */
public enum WCFChannelEnum {

    PREPAID("PREPAID",false),
    POSTPAID("POSTPAID",false),
    UNKNOWN("UNKNOWN",false),
    CPE_4G("CPE4G",false),
    MIFI_4G("CPE4G",false),
    DONGLE_4G("DONGLE4G",false),
    WINGLE_3G("WINGLE3G",false),
    WINGLE_4G("WINGLE4G",false),
    DONGLE_3G("DONGLE3G",false),
    BROADBAND("BROADBAND",false),
    DTH("DTH",false),
    HEADER_ENRICHMENT("HEADER_ENRICHMENT",true),
    OTP("OTP",true),
    AUTOMATIC_LOGIN("AUTOMATIC_LOGIN",false),
    IMSI("IMSI",true);

    private String channel;
    private Boolean isVerifiedUser;

    private static Map<String,WCFChannelEnum> WCFChannelEnumMap = new HashMap();

    static {
        loadWCFChannelMap();
    }

    WCFChannelEnum(String channel,Boolean isVerifiedUser) {
        this.channel = channel;
        this.isVerifiedUser = isVerifiedUser;
    }

    public String getChannel() {
        return channel;
    }

    public Boolean getVerifiedUser() {
        return isVerifiedUser;
    }

    public static void loadWCFChannelMap(){
        for(WCFChannelEnum wcfChannelEnum : WCFChannelEnum.values()){
            WCFChannelEnumMap.put(wcfChannelEnum.getChannel(),wcfChannelEnum);
        }
    }

    public static WCFChannelEnum getWCFChannelByChannel(String channel){
        if(StringUtils.isBlank(channel)){
            return null;
        }
        return WCFChannelEnumMap.get(channel);
    }
}
