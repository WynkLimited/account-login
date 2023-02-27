package com.wynk.common;


public enum PackProviderType {
    INTERNAL("internal"), EXTERNAL("external"), UNKNOWN("unknown");

    String value;

    PackProviderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PackProviderType fromValue(String value) {
        PackProviderType app = UNKNOWN;
        for(PackProviderType appType : PackProviderType.values()) {
            if(appType.getValue().equals(value)) {
                app = appType;
            }
        }
        return app;
    }
}
