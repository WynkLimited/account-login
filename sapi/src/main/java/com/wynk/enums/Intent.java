package com.wynk.enums;

public enum Intent {

    AD_FREE("ad_free"),
    DOWNLOAD("download"),
    HELLOTUNE("hellotune"),
    SpecialHT("SpecialHT"),
    INFINITE_SONGS("infinite_songs"),
    MY_ACCOUNT("my_account"),
    UNKNOWN("");

    public final String label;

    private Intent(String label) {
        this.label = label;
    }

    public static Intent getIntent(String value) {
        for (Intent e : values()) {
            if (e.label.equalsIgnoreCase(value)) {
                return e;
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
