package com.wynk.sms;

public class SmsData {
  private String msisdn;
  private String msg;

  public SmsData(String msg) {
    this.msg = msg;
  }

  public SmsData(String msisdn, String msg) {
    this.msisdn = msisdn;
    this.msg = msg;
  }

  public String getMsisdn() {
    return msisdn;
  }

  public void setMsisdn(String msisdn) {
    this.msisdn = msisdn;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  @Override
  public String toString() {
    return "SmsData{" + "msisdn='" + msisdn + '\'' + ", msg='" + msg + '\'' + '}';
  }
}
