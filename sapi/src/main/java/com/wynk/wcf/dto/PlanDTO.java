package com.wynk.wcf.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;
import lombok.Data;

@Data
public class PlanDTO {

  private int id;
  private String title;
  private String service;
  private int linkedOfferId;
  private PriceDTO price;
  private PlanPeriodDTO period;
  private PlanType planType;
  private Map<String, String> sku;

  @JsonIgnore
  public double getFinalPrice() {
    return this.price.getAmount();
  }

  @JsonIgnore
  public long getFinalPriceInPaise() {
    return (long) getFinalPrice() * 100;
  }
}
