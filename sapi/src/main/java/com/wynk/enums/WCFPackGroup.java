package com.wynk.enums;

public enum WCFPackGroup {

    WYNK_HT("wynk_ht"),
    WYNK_MUSIC("wynk_music");

    public final String label;

    private WCFPackGroup(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
