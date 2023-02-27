package com.wynk.wcf.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class WCFMsisdnIdentificationRequest {
  public class MsisdnIdentificationRequest {
    private List<String> imsi;
    private String xMsisdn;
    private String appVersion;
    private String os;
    private String deviceId;
    private String service;
    private String dthCustID;
    private String appId;
    private String deviceType;
    private Integer buildNo;
  }
}
