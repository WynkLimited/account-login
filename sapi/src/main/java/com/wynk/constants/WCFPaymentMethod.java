package com.wynk.constants;

/**
 * Created by Aakash on 05/07/17.
 */
public enum WCFPaymentMethod {
    ITUNES("itunes"),
    GOOGLE_WALLET("googleWallet");

    private String paymentMethod;

    WCFPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
