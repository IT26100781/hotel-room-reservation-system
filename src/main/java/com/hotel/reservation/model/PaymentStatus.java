// src/main/java/com/hotel/reservation/model/PaymentStatus.java
package com.hotel.reservation.model;

/**
 * Payment lifecycle stored in {@code bookings.payment_status}.
 *
 * Values match the CHECK constraint in {@code 04-bookings.sql}:
 * {@code pending}, {@code paid}, {@code refunded}.
 */
public enum PaymentStatus {

    PENDING("pending", "Pending"),
    PAID("paid", "Paid"),
    REFUNDED("refunded", "Refunded");

    private final String value;
    private final String label;

    PaymentStatus(String value, String label) {
        this.value = value;
        this.label = label;
    }

    /** Database column value. */
    public String getValue() {
        return value;
    }

    /** Human-readable label used in JSP badges. */
    public String getLabel() {
        return label;
    }

    /**
     * Convert a database string to an enum constant.
     *
     * @throws IllegalArgumentException if the value is unknown
     */
    public static PaymentStatus fromString(String s) {
        if (s == null) throw new IllegalArgumentException("Payment status cannot be null");
        for (PaymentStatus ps : values()) {
            if (ps.value.equalsIgnoreCase(s.trim())) return ps;
        }
        throw new IllegalArgumentException("Unknown payment status: " + s);
    }
}
