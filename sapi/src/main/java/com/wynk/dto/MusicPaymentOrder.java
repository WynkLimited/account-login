package com.wynk.dto;

/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 19/05/14
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class MusicPaymentOrder extends PaymentOrder {
    private String productId;

    public MusicPaymentOrder(String orderId, String customerId, float amount, String productId) {

        super(orderId, customerId, amount);
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
