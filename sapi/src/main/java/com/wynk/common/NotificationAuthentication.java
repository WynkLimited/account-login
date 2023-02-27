package com.wynk.common;


public enum NotificationAuthentication {
    NONE("none"), BASIC("basic"), HMAC("hmac");

    private String value;

    private NotificationAuthentication(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static NotificationAuthentication fromValue(String value) {
        NotificationAuthentication auth = NONE;
        for(NotificationAuthentication notifyAuth : NotificationAuthentication.values()) {
            if(notifyAuth.getValue().equals(value)) {
                auth = notifyAuth;
            }
        }
        return auth;
    }
}