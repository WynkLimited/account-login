package com.wynk.common;

import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : Kunal Sharma
 * @since : 12/01/23, Thursday
 **/
public enum AppType {

    THANKS_ANDROID(ImmutableSet.of("in.bsb.myairtel.inhouse",
            "com.BhartiMobile.myairtel",
            "com.wynk.discoverios",
            "com.myairtelapp.debug",
            "com.myairtelapp",
            "com.airtel.sample")),

    THANKS_IOS(new HashSet<>());

    private Set<String> name;

    AppType(Set<String> name) {
        this.name = name;
    }

    public Set<String> getName() {
        return name;
    }

    public static boolean isThanksBuild(AppType appType) {
        return appType != null && (appType == THANKS_ANDROID || appType == THANKS_IOS);
    }
}
