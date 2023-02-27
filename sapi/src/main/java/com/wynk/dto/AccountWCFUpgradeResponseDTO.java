package com.wynk.dto;

/**
 * Created by Aakash on 05/09/17.
 */
public class AccountWCFUpgradeResponseDTO {

    private String url;
    private Boolean carrierBillingAllow;
    private String status;
    private Boolean isOtpRequired;
    private String transactionId;
    private Integer productId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getCarrierBillingAllow() {
        return carrierBillingAllow;
    }

    public void setCarrierBillingAllow(Boolean carrierBillingAllow) {
        this.carrierBillingAllow = carrierBillingAllow;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getOtpRequired() {
        return isOtpRequired;
    }

    public void setOtpRequired(Boolean otpRequired) {
        isOtpRequired = otpRequired;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}
