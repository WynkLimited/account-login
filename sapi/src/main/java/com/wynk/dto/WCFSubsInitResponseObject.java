package com.wynk.dto;

/**
 * Created by Aakash on 13/12/16.
 */
public class WCFSubsInitResponseObject {

    private String wcfProductId;
    private String redirectUri;
    private Boolean carrierBillingAllow;
    private String transactionId;

    public String getWcfProductId() {
        return wcfProductId;
    }

    public void setWcfProductId(String wcfProductId) {
        this.wcfProductId = wcfProductId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Boolean getCarrierBillingAllow() {
        return carrierBillingAllow;
    }

    public void setCarrierBillingAllow(Boolean carrierBillingAllow) {
        this.carrierBillingAllow = carrierBillingAllow;
    }

    @Override
    public String toString() {
        return "WCFSubsInitResponseObject{" +
                "wcfProductId='" + wcfProductId + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                ", carrierBillingAllow=" + carrierBillingAllow +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}
