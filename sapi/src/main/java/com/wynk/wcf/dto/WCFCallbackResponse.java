package com.wynk.wcf.dto;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WCFCallbackResponse {
  private String uid;
  private String msisdn;
  private String event;
  private String status;
  private Integer planId;
  private Long validTillDate;
  private Boolean active;
  private Boolean autoRenewal;

  public WCFCallbackResponse fromJson(String json) {
    return new Gson().fromJson(json, WCFCallbackResponse.class);
  }
}
