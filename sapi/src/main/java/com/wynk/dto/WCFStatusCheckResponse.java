package com.wynk.dto;

import java.util.List;

/**
 * Created by aakashkumar on 05/12/16.
 */
public class WCFStatusCheckResponse {

    private String status;
    private String tid;
    private Long validTillDate;
    private Integer partnerProductId;
    private Boolean autoRenewal;
    private Integer productId;
    private List<SubscriptionStatusCheck> subStatus;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public Long getValidTillDate() {
        return validTillDate;
    }

    public void setValidTillDate(Long validTillDate) {
        this.validTillDate = validTillDate;
    }

    public Boolean getAutoRenewal() {
        return autoRenewal;
    }

    public void setAutoRenewal(Boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }

    public Integer getPartnerProductId() {
        return partnerProductId;
    }

    public void setPartnerProductId(Integer partnerProductId) {
        this.partnerProductId = partnerProductId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public List<SubscriptionStatusCheck> getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(List<SubscriptionStatusCheck> subStatus) {
        this.subStatus = subStatus;
    }
}
