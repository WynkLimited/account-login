package com.wynk.user.dto;

public class DeviceEnityKey {

    //@Field("dt")
    public static final String deviceType = "dt"; // model of device

    //@Field("di")
    public static final String deviceId = "di"; // GCM key

    //@Field("dk")
    public static final String deviceKey = "dk"; // used for push notification

    // @Field("dv")
    public static final String deviceVersion = "dv";

    //@Field("os")
    public static final String os = "os"; // Lets club all 4 into the x-bsy-did format

    // @Field("ov")
    public static final String osVersion = "ov";

    // @Field("av")
    public static final String appVersion = "av";

    // @Field("ai")
    public static final String appId = "ai";

    // @Field("bn")
    public static final String buildNumber = "bn";

    // @Field("drd")
    public static final String registrationDate = "drd";

    // @Field("dlat")
    public static final String lastActivityTime = "dlat";

    // @Field("drs")
    public static final String resolution = "drs";

    // @Field("dop")
    public static final String operator = "dop";

    // @Field("dproem")
    public static final String preInstallOem = "dproem";

    // @Field("dklut")
    public static final String deviceKeyLastUpdateTime = "dklut";

    // @Field("dadvi")
    public static final String advertisingId = "dadvi";

    // @Field("dfbid")
    public static final String fbUserId = "dfbid";

    // @Field("dimei")
    public static final String imeiNumber = "dimei";

    // @Field("dsim")
    public static final String isDualSim = "dsim";

    //  @Field("sim")
    public static final String simInfo = "sim";

    //  @Field("inlr")
    public static final String intlRoaming = "inlr";

    // @Field("rc")
    public static final String registrationChannel = "rc";

    // @Field("wat")
    public static final String appType = "wat";

    // @Field("cc")
    public static final String customerCategory = "cc";

    // @Field("is4g")
    public static final String is4gEnabled = "is4g";

    // @Field("tdi")
    public static final String totpDeviceId = "tdi";

    // @Field("tk")
    public static final String totpDeviceKey = "tk";

    public interface SimInfo {

        //@Field("nm")
        String name = "nm";

        //@Field("cr")
        String carrier = "cr";

        //@Field("rm")
        String roaming = "rm";

        // @Field("nw")
        String network = "nw";

        //@Field("imei")
        String imeiNumber = "imei";

        // @Field("mcc")
        String mcc = "mcc";

        // @Field("mnc")
        String mnc = "mnc";

        //@Field("imsi")
        String imsiNumber = "imsi";
    }

    public interface IntlRoaming {
        // @Field("ipd")
        String initialProvisionedDate = "ipd"; // date on which initial provisioning had started

        //@Field("pd")
        String provisionedDate = "pd"; // date on which current provisioning has started

        // @Field("ire")
        String intlRoamingEnabled = "ire"; // current international roaming activated

        //@Field("cct")
        String countConsumedTime = "cct"; // total count of consumed time while on international roaming

        //@Field("lcut")
        String lastConsumptionUpdateTime = "lcut"; // last timestamp of consumed time update to db


    }

    public interface WCFRegistrationChannel {

        //@Field("wcf_cn")
        String channelName = "wcf_cn";

        // @Field("wcf_ivu")
        String isVerifiedUser = "wcf_ivu";
    }


}
