package com.hotel.reservation.model;

public enum PaymentStatus {
    PENDING("pending"),
    PAID("paid"),
    REFUNDED("refunded");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PaymentStatus fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return PENDING;
        }
        for (PaymentStatus p : PaymentStatus.values()) {
            if (p.value.equalsIgnoreCase(text.trim()) || p.name().equalsIgnoreCase(text.trim())) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unknown payment status: " + text);
    }
}
