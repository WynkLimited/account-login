package com.wynk.common;

import java.util.HashMap;
import java.util.Map;

public enum Version {
    V1("1", "0"), V2("2", "0"), V3("3", "0");

    private String major;
    private String minor;

    private Version(String major, String minor) {
        this.major = major;
        this.minor = minor;
    }

    public String getMajorVersion() {
        return major;
    }

    public String getMinorVersion() {
        return minor;
    }

    private static Map<String, Version> versionMap = new HashMap<String, Version>();

    static {
        for (Version version : Version.values()) {
            versionMap.put(version.name().toLowerCase(), version);
        }
    }

    public static Version getVersion(String versionName, Version defaultVersion) {
        Version version = versionMap.get(versionName);
        if (version != null) {
            return version;
        }
        return defaultVersion;
    }
}
