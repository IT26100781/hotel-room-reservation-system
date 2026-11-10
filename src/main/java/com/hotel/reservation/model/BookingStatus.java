package com.hotel.reservation.model;

public enum BookingStatus {
    ACTIVE("active"),
    CANCELLED("cancelled");

    private final String value;

    BookingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BookingStatus fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return ACTIVE;
        }
        for (BookingStatus b : BookingStatus.values()) {
            if (b.value.equalsIgnoreCase(text.trim()) || b.name().equalsIgnoreCase(text.trim())) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unknown booking status: " + text);
    }
}
