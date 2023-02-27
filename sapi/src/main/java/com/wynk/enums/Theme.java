package com.wynk.enums;

public enum Theme {

    LIGHT("light"),
    DARK("dark"),
    UNKNOWN("");

    public final String label;

    private Theme(String label) {
        this.label = label;
    }

    public static Theme getTheme(String value) {
        for (Theme e : values()) {
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
