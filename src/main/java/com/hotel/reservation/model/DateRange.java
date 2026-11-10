// src/main/java/com/hotel/reservation/model/DateRange.java
package com.hotel.reservation.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Immutable value object representing a check-in / check-out date pair.
 *
 * Validates that {@code checkOut > checkIn} at construction time and provides
 * helper methods used by the service and the JSPs (night count, overlap test,
 * formatted labels for display).
 */
public final class DateRange {

    /** Pretty date format shown on confirmation and booking cards. */
    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("d MMM yyyy");

    private final LocalDate checkIn;
    private final LocalDate checkOut;

    /**
     * Creates a validated date range.
     *
     * @throws IllegalArgumentException if check-out is not after check-in
     */
    public DateRange(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out must be after check-in.");
        }
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    // ---- core queries ----

    /** Number of nights between check-in and check-out. */
    public int getNights() {
        return (int) ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    /**
     * Two date ranges overlap when:
     * {@code A.checkIn < B.checkOut && A.checkOut > B.checkIn}.
     */
    public boolean overlapsWith(DateRange other) {
        return this.checkIn.isBefore(other.checkOut) && this.checkOut.isAfter(other.checkIn);
    }

    // ---- display helpers (used by JSP EL) ----

    /** e.g. "15 Dec 2026" */
    public String getCheckInLabel() {
        return checkIn.format(DISPLAY);
    }

    /** e.g. "18 Dec 2026" */
    public String getCheckOutLabel() {
        return checkOut.format(DISPLAY);
    }

    /** e.g. "15 Dec – 18 Dec 2026 (3 nights)" */
    public String getStayLabel() {
        int n = getNights();
        return checkIn.format(DISPLAY) + " – " + checkOut.format(DISPLAY)
                + " (" + n + (n == 1 ? " night)" : " nights)");
    }

    // ---- getters ----

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    @Override
    public String toString() {
        return checkIn + " → " + checkOut + " (" + getNights() + " nights)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateRange that)) return false;
        return checkIn.equals(that.checkIn) && checkOut.equals(that.checkOut);
    }

    @Override
    public int hashCode() {
        return 31 * checkIn.hashCode() + checkOut.hashCode();
    }
}
