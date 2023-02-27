package com.wynk.wcf.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
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

  public String toJson() {
    return new Gson().toJson(this);
  }
}
