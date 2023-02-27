package com.wynk.dto;

/**
 * Created by aakashkumar on 29/11/16.
 */
public enum EligibleSubscriptionState {

    USER_TRIGGERED(0,"USER_TRIGGERED"),PRE_AUTH(1,"PRE_AUTH"),PROVISIONED(2,"PROVISIONED"),DATA(3,"DATA_PACK"),ACTIVE(4,"ACTIVE"),CURRENT(5,"CURRENT"),FALLBACK(6,"FALLBACK");

    private Integer code;
    private String status;

    EligibleSubscriptionState(Integer code, String status) {
        this.code = code;
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }
}
