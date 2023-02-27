package com.wynk.dto;

public class CPNotificationDTO {

    private String id;

    private double amount;

    private int    errorCode;

    private String errorMessage;

    private double lowBalance;

    private String msisdn;

    private int    xactionId;

    private long   chargingTimestamp;

    private int    productId;

    private String temp1;

    private String temp2;

    private String temp3;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public double getLowBalance() {
        return lowBalance;
    }

    public void setLowBalance(double lowBalance) {
        this.lowBalance = lowBalance;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public int getXactionId() {
        return xactionId;
    }

    public void setXactionId(int xactionId) {
        this.xactionId = xactionId;
    }

    public long getChargingTimestamp() {
        return chargingTimestamp;
    }

    public void setChargingTimestamp(long chargingTimestamp) {
        this.chargingTimestamp = chargingTimestamp;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getTemp1() {
        return temp1;
    }

    public void setTemp1(String temp1) {
        this.temp1 = temp1;
    }

    public String getTemp2() {
        return temp2;
    }

    public void setTemp2(String temp2) {
        this.temp2 = temp2;
    }

    public String getTemp3() {
        return temp3;
    }

    public void setTemp3(String temp3) {
        this.temp3 = temp3;
    }

    @Override
    public String toString() {
        return "CPNotificationDTO [amount=" + amount + ", errorCode=" + errorCode + ", errorMessage=" + errorMessage + ", lowBalance=" + lowBalance + ", msisdn=" + msisdn + ", xactionId=" + xactionId
                + ", chargingTimestamp=" + chargingTimestamp + ", productId=" + productId + ", temp1=" + temp1 + ", temp2=" + temp2 + ", temp3=" + temp3 + "]";
    }

}