package com.wynk.wcf.dto;

import com.google.gson.Gson;
import com.wynk.music.dto.GeoLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferProvisionRequest {
  private String uid;
  private String appVersion;
  private Integer buildNo;
  private String msisdn;
  private String os;
  private String deviceId;
  private String appId;
  private String service;
  private GeoLocation geoLocation;

  public String toJson() {
    return new Gson().toJson(this);
  }
}
