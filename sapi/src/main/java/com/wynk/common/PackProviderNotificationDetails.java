package com.wynk.common;


public class PackProviderNotificationDetails {

    private boolean notificationRequired;
    private String url;
    private String auth;
    private String username;
    private String password;

    public boolean isNotificationRequired() {
        return notificationRequired;
    }

    public String getUrl() {
        return url;
    }

    public NotificationAuthentication getAuth() {
        return NotificationAuthentication.fromValue(auth);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static class Builder {

        private boolean notificationRequired;
        private String url;
        private String auth;
        private String username;
        private String password;

        public Builder notificationRequired(boolean notificationRequired) {
            this.notificationRequired = notificationRequired;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder auth(NotificationAuthentication auth) {
            this.auth = auth.getValue();
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public PackProviderNotificationDetails build() {
            return new PackProviderNotificationDetails(this);
        }
    }

    private PackProviderNotificationDetails(Builder builder) {
        this.notificationRequired = builder.notificationRequired;
        this.url = builder.url;
        this.auth = builder.auth;
        this.username = builder.username;
        this.password = builder.password;
    }

    private PackProviderNotificationDetails() {
        super();
    }
}