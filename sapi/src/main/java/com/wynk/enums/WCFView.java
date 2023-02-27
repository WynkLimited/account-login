package com.wynk.enums;

public enum WCFView {

    SMALL("small"),
    LARGE("large"),
    POPUP("popup"),
    UNKNOWN("");

    public final String label;

    private WCFView(String label) {
        this.label = label;
    }

    public static WCFView getView(String value) {
        for (WCFView e : values()) {
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
