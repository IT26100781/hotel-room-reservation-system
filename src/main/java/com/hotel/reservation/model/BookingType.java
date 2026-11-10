// src/main/java/com/hotel/reservation/model/BookingType.java
package com.hotel.reservation.model;

/**
 * How the booking was created — online by the guest, or at the front desk by staff.
 *
 * Stored in {@code bookings.booking_type} as {@code "ONLINE"} or {@code "DESK"}.
 * The two subclasses {@link OnlineReservation} and {@link DeskReservation} map 1-to-1
 * with these values, following the Single Table Inheritance (STI) pattern.
 */
public enum BookingType {

    /** Guest booked through the website. */
    ONLINE("ONLINE"),

    /** Staff booked at the front desk for a walk-in or phone guest. */
    DESK("DESK");

    private final String value;

    BookingType(String value) {
        this.value = value;
    }

    /** Database column value. */
    public String getValue() {
        return value;
    }

    /** Human-readable label for display. */
    public String getLabel() {
        return this == ONLINE ? "Online" : "Front desk";
    }

    /**
     * Convert a database string to an enum constant.
     *
     * @throws IllegalArgumentException if the value is unknown
     */
    public static BookingType fromString(String s) {
        if (s == null) throw new IllegalArgumentException("Booking type cannot be null");
        for (BookingType bt : values()) {
            if (bt.value.equalsIgnoreCase(s.trim())) return bt;
        }
        throw new IllegalArgumentException("Unknown booking type: " + s);
    }
}
