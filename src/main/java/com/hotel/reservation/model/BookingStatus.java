// src/main/java/com/hotel/reservation/model/BookingStatus.java
package com.hotel.reservation.model;

/**
 * Lifecycle status stored in the {@code bookings.status} column.
 *
 * Only two values are persisted: ACTIVE and CANCELLED.
 * Derived states like "completed", "no-show", "arriving" and "in-house"
 * are calculated at runtime by {@link BookingState} using the dates and
 * the checked_in flag — they are never written to the database.
 */
public enum BookingStatus {

    /** The booking is live (not yet cancelled). */
    ACTIVE("active"),

    /** The guest or staff cancelled the booking. */
    CANCELLED("cancelled");

    private final String value;

    BookingStatus(String value) {
        this.value = value;
    }

    /** Database column value, e.g. {@code "active"}. */
    public String getValue() {
        return value;
    }

    /** Human-readable label for JSP display. */
    public String getLabel() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    /**
     * Convert a database string to an enum constant.
     *
     * @throws IllegalArgumentException if the value is unknown
     */
    public static BookingStatus fromString(String s) {
        if (s == null) throw new IllegalArgumentException("Booking status cannot be null");
        for (BookingStatus bs : values()) {
            if (bs.value.equalsIgnoreCase(s.trim())) return bs;
        }
        throw new IllegalArgumentException("Unknown booking status: " + s);
    }
}
