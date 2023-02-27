package com.wynk.dto;

/**
 * @author chaudharys
 * POJO for request payload to subscribe the user triggered offer
 */
public class WcfUserOfferRequest {
  private String uid;
  private String uiappVersiond;
  private String productId;
  private String msisdn;
  private String returnUrl;
  private String buildNumber;
  private String platform;

  public WcfUserOfferRequest(){}

  //Getters & Setters
  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getUiappVersiond() {
    return uiappVersiond;
  }

  public void setUiappVersiond(String uiappVersiond) {
    this.uiappVersiond = uiappVersiond;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getMsisdn() {
    return msisdn;
  }

  public void setMsisdn(String msisdn) {
    this.msisdn = msisdn;
  }

  public String getReturnUrl() {
    return returnUrl;
  }

  public void setReturnUrl(String returnUrl) {
    this.returnUrl = returnUrl;
  }

  public String getBuildNumber() {
    return buildNumber;
  }

  public void setBuildNumber(String buildNumber) {
    this.buildNumber = buildNumber;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }
}
