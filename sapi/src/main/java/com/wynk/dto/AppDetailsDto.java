package com.wynk.dto;

import lombok.Data;

/**
 * @author : Kunal Sharma
 * @since : 11/07/22, Monday
 **/

@Data
public class AppDetailsDto {

    private String os;
    private String appId;
    private String service;
    private String deviceId;
    private String appVersion;
    private String buildNo;
    private String deviceType;
}
