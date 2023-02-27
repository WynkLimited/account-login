package com.wynk.dto;

/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 19/05/14
 * Time: 4:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaymentOrder {
    private String orderId;
    private String customerId;
    private float amount;

    private boolean orderExecuted;
    private int exceptionCode;
    private String exceptionMsg;
    private int reTries;

    public PaymentOrder(String orderId, String customerId, float amount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public boolean isOrderExecuted() {
        return orderExecuted;
    }

    public void setOrderExecuted(boolean orderExecuted) {
        this.orderExecuted = orderExecuted;
    }

    public int getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(int exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public String getExceptionMsg() {
        return exceptionMsg;
    }

    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }

    public int getReTries() {
        return reTries;
    }

    public void setReTries(int reTries) {
        this.reTries = reTries;
    }
}
