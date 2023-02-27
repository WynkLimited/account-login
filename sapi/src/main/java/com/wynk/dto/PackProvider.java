package com.wynk.dto;

import com.wynk.common.PackProviderNotificationDetails;
import com.wynk.common.PackProviderType;

public class PackProvider {

    private String appName;
    private String appId;
    private String appSecret;
    private String email;
    private String contact;
    private String type;
    private PackProviderNotificationDetails notifyDetails;

    public String getAppName() {
        return appName;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public PackProviderType getType() {
        return PackProviderType.fromValue(type);
    }

    public PackProviderNotificationDetails getNotifyDetails() {
        return notifyDetails;
    }

    public static class Builder {

        private String appName;
        private String email;
        private String contact;
        private String appId;
        private String appSecret;
        private String type;
        private PackProviderNotificationDetails notifyDetails;

        public Builder appName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder contact(String contact) {
            this.contact = contact;
            return this;
        }

        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder appSecret(String appSecret) {
            this.appSecret = appSecret;
            return this;
        }

        public Builder type(PackProviderType type) {
            this.type = type.getValue();
            return this;
        }

        public Builder notifyDetails(PackProviderNotificationDetails notifyDetails) {
            this.notifyDetails = notifyDetails;
            return this;
        }

        public PackProvider build() {
            return new PackProvider(this);
        }
    }

    private PackProvider(Builder builder) {
        this.appName = builder.appName;
        this.email = builder.email;
        this.contact = builder.contact;
        this.appId = builder.appId;
        this.appSecret = builder.appSecret;
        this.type = builder.type;
        this.notifyDetails = builder.notifyDetails;
    }

    private PackProvider() {
        super();
    }

    @Override
    public String toString() {
        return "PackProvider{" +
                "appName='" + appName + '\'' +
                ", appId='" + appId + '\'' +
                ", appSecret='" + appSecret + '\'' +
                ", email='" + email + '\'' +
                ", contact='" + contact + '\'' +
                ", type='" + type + '\'' +
                ", notifyDetails=" + notifyDetails +
                '}';
    }
}