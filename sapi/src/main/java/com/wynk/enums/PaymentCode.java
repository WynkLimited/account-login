package com.wynk.enums;

public enum PaymentCode {
    GOOGLE_WALLET("GOOGLE_WALLET"),
    ITUNES("ITUNES"),
    UNKNOWN(""),
    GOOGLE_IAP("GOOGLE_IAP");

    public final String label;

    private PaymentCode(String label) {
        this.label = label;
    }

    public static PaymentCode getPaymentCode(String value) {
        for (PaymentCode e : values()) {
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
