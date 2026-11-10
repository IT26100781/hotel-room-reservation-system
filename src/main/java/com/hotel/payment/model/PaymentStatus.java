package com.hotel.payment.model;

public enum PaymentStatus {
    PENDING, PAID, REFUNDED;

    public String getValue() {
        return name().toLowerCase();
    }

    public String getLabel() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static PaymentStatus parse(String value) {
        if (value != null) for (PaymentStatus s : values()) if (s.getValue().equals(value)) return s;
        throw new IllegalArgumentException("Choose a valid payment status.");
    }
}
