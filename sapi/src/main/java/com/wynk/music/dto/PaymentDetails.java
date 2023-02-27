package com.wynk.music.dto;

/**
 * @author : Kunal Sharma
 * @since : 25/09/22, Sunday
 **/
public class PaymentDetails {

    String purchaseToken;
    String orderId;

    public static class Payment {
        public PaymentDetails payment;
    }
}
