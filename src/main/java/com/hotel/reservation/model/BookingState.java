// src/main/java/com/hotel/reservation/model/BookingState.java
package com.hotel.reservation.model;

import java.time.LocalDate;

/**
 * Runtime state of a booking, derived from the persisted fields (status, checked_in, dates).
 *
 * This is never stored in the database — it is computed each time a booking is loaded.
 * The JSPs reference {@code r.state.value}, {@code r.state.badge}, and {@code r.state.label}.
 */
public final class BookingState {

    private final String value;   // e.g. "upcoming", "arriving", "inhouse", "completed", "noshow", "cancelled"
    private final String badge;   // CSS class for the badge component
    private final String label;   // human-readable text

    private BookingState(String value, String badge, String label) {
        this.value = value;
        this.badge = badge;
        this.label = label;
    }

    // ---- factory ----

    /**
     * Derive the runtime state from the persisted booking fields.
     */
    public static BookingState of(BookingStatus status, boolean checkedIn,
                                  LocalDate checkIn, LocalDate checkOut) {
        LocalDate today = LocalDate.now();

        if (status == BookingStatus.CANCELLED) {
            return new BookingState("cancelled", "badge-cancelled", "Cancelled");
        }

        // Active booking — decide by dates and checked_in flag.
        if (checkedIn) {
            if (today.isBefore(checkOut)) {
                return new BookingState("inhouse", "badge-inhouse", "Checked in");
            }
            return new BookingState("completed", "badge-completed", "Completed");
        }

        // Not checked in.
        if (today.isBefore(checkIn)) {
            return new BookingState("upcoming", "badge-upcoming", "Upcoming");
        }
        if (today.equals(checkIn)) {
            return new BookingState("arriving", "badge-arriving", "Arriving today");
        }
        // Check-in date has passed and not checked in = no-show.
        return new BookingState("noshow", "badge-noshow", "No-show");
    }

    // ---- getters (used by JSP EL) ----

    public String getValue() {
        return value;
    }

    public String getBadge() {
        return badge;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
