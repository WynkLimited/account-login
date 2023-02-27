package com.wynk.dto;

import lombok.Data;

/**
 * @author : Kunal Sharma
 * @since : 06/10/22, Thursday
 **/

@Data
public class VerifyReceiptResponseV3 {

    private PageDetails pageDetails;
    private PaymentReceiptDetails paymentDetails;

    @Data
    public static class PageDetails {
        private String pageUrl;
    }

    @Data
    public static class PaymentReceiptDetails {
        private String purchaseToken;
        private String orderId;
        private Boolean valid;
    }
}