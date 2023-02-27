package com.wynk.dto;

import com.wynk.enums.Intent;
import com.wynk.enums.Theme;
import com.wynk.enums.WCFPackGroup;
import com.wynk.music.dto.GeoLocation;
import lombok.Data;

@Data
public class WCFInitWebViewRequest {

    private String appId = "mobility";
    private String appVersion;
    private Integer buildNo;
    private String deviceId;
    private String msisdn;
    private String os;
    private WCFPackGroup packGroup;
    private String service = "music";
    private String uid;
    private Theme theme;
    private String planId;
    private String ingressIntent;
    private GeoLocation geoLocation;
}