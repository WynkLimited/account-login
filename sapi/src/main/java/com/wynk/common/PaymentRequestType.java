package com.wynk.common;

public enum PaymentRequestType {
    DEFAULT, SUBSCRIBE, RENEW_SUBSCRIPTION;

    public static PaymentRequestType getByName(String reqType) {
        try {
            return PaymentRequestType.valueOf(reqType);
        }
        catch (Exception ex) {
            return DEFAULT;
        }
    }
}
