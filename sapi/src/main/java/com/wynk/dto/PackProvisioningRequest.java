package com.wynk.dto;

import com.google.gson.annotations.SerializedName;
import com.wynk.common.PackProvisioningAction;
import com.wynk.music.dto.MusicPlatformType;
import com.wynk.utils.ObjectUtils;
import com.wynk.utils.UserDeviceUtils;
import com.wynk.utils.Utils;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

public class PackProvisioningRequest {
    
    private String msisdn;
    private int    productId;
    private String orderId;
    
    @SerializedName("timestamp")
    private Date date;
    private String action;
    private String channel;

    public String getUid() {
        String uid = StringUtils.EMPTY;
        if(StringUtils.isNotBlank(msisdn)) {
            try {
                uid = UserDeviceUtils.generateUUID(msisdn, null, null, MusicPlatformType.WYNK_APP);
            }
            catch (Exception e) {
                // eat this
            }
        }
        return uid;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public int getProductId() {
        return productId;
    }

    public String getOrderId() {
        return orderId;
    }

    public Date getTimestamp() {
        return date;
    }

    public PackProvisioningAction getAction() {
        return PackProvisioningAction.fromValue(action);
    }

    public String getChannel() {
        return ObjectUtils.getEmptySafeString(channel, StringUtils.EMPTY);
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public static class Builder {

        private String msisdn;
        private int    productId;
        private String orderId;
        private Date date;
        private String action;
        private String channel;

        public Builder msisdn(String msisdn) {
            String normalizedMsisdn = StringUtils.EMPTY;
            if(StringUtils.isNotBlank(msisdn)) {
                normalizedMsisdn = Utils.normalizePhoneNumber(msisdn);
            }
            this.msisdn = normalizedMsisdn;
            return this;
        }

        public Builder productId(int productId) {
            this.productId = productId;
            return this;
        }

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder date(long timestamp) {
            this.date = new Date(timestamp);
            return this;
        }

        public Builder action(PackProvisioningAction action) {
            this.action = action.getValue();
            return this;
        }

        public Builder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public PackProvisioningRequest build() {
            return new PackProvisioningRequest(this);
        }
    }

    private PackProvisioningRequest(Builder builder) {
        this.msisdn = builder.msisdn;
        this.productId = builder.productId;
        this.orderId = builder.orderId;
        this.date = builder.date;
        this.action = builder.action;
        this.channel = builder.channel;
    }

    // For mongo
    private PackProvisioningRequest() {
        super();
    }

    @Override
    public String toString() {
        return "ProvisioningRequest [uid=" + getUid() + ", productId=" + productId + ", orderId=" + orderId + ", date=" + date + ", action=" + action + ", channel=" + channel + "]";
    }

}
