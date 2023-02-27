package com.wynk.wcf.dto;

public enum PlanType {
  FREE("Free"),
  ONE_TIME_SUBSCRIPTION("OneTimeSubscription"),
  SUBSCRIPTION("Subscription");

  private String value;

  PlanType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
