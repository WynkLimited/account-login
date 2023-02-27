package com.wynk.common;

import com.wynk.server.ChannelContext;

public enum WynkAppType {
    DEFAULT, BASIC;

    public static final String MUSIC_HEADER_APP = "x-bsy-app";
    public static final String BASIC_APP = "wynk_basic_android";

    public static Boolean isWynkBasicApp() {
        String appType = ChannelContext.getRequest().headers().get(MUSIC_HEADER_APP);
        if (appType != null && appType.equalsIgnoreCase(BASIC_APP)) {
            return true;
        } else {
            return false;
        }
    }
}
