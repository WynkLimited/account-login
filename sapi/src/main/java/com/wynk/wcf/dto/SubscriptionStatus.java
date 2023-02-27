package com.wynk.wcf.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionStatus {
  private String service;
  private boolean active;
  private int productId;
  private long validity;
  private String packGroup;
  private int planId;
}
