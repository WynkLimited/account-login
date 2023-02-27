package com.wynk.constants;

/**
 * Created by Aakash on 21/09/17.
 */
public enum SubscriptionChannelPhases {

    SEND_OTP("AIRTEL CARRIER BILLING","1",Boolean.TRUE,null,null,"Airtel Account"),
    AIRTEL_CARRIER_BILLING("AIRTEL CARRIER BILLING","1",Boolean.TRUE,"transactionId","otp","Airtel Account"),
    OTHER_CHANNELS("Other Payment Options","2",Boolean.FALSE,null,null,"");


    private String paymentName;
    private String iconUrl;
    private Boolean isNativePayment;
    private String requestMandatoryBodyParameter;
    private String requestOptionalBodyParameter;
    private String billingText;

    SubscriptionChannelPhases(String paymentName, String iconUrl, Boolean isNativePayment, String requestMandatoryBodyParameter, String requestOptionalBodyParameter,
    String billingText) {
        this.paymentName = paymentName;
        this.iconUrl = iconUrl;
        this.isNativePayment = isNativePayment;
        this.requestMandatoryBodyParameter = requestMandatoryBodyParameter;
        this.requestOptionalBodyParameter = requestOptionalBodyParameter;
        this.billingText = billingText;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public Boolean getNativePayment() {
        return isNativePayment;
    }

    public String getRequestMandatoryBodyParameter() {
        return requestMandatoryBodyParameter;
    }

    public String getRequestOptionalBodyParameter() {
        return requestOptionalBodyParameter;
    }

    public String getBillingText() {
        return billingText;
    }
}
