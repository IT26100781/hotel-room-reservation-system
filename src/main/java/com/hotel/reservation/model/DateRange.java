package com.hotel.reservation.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Value Object encapsulating a reservation date interval.
 * Guarantees that checkOut is always strictly after checkIn.
 */
public final class DateRange {

    private final LocalDate checkIn;
    private final LocalDate checkOut;

    public DateRange(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates cannot be null");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date (" + checkOut + ") must be after check-in date (" + checkIn + ")");
        }
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    /**
     * Determines whether this date range overlaps with another.
     * Hotel night model: check-in day is inclusive, check-out day is exclusive.
     * Formula: (this.checkIn < other.checkOut) && (this.checkOut > other.checkIn)
     */
    public boolean overlapsWith(DateRange other) {
        Objects.requireNonNull(other, "Comparison date range cannot be null");
        return this.checkIn.isBefore(other.checkOut) && this.checkOut.isAfter(other.checkIn);
    }

    public boolean isPast(LocalDate today) {
        return this.checkOut.isBefore(today);
    }

    public boolean isCurrent(LocalDate today) {
        return !today.isBefore(this.checkIn) && today.isBefore(this.checkOut);
    }

    public boolean isUpcoming(LocalDate today) {
        return this.checkIn.isAfter(today);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateRange that)) return false;
        return checkIn.equals(that.checkIn) && checkOut.equals(that.checkOut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkIn, checkOut);
    }

    @Override
    public String toString() {
        return checkIn + " -> " + checkOut + " (" + getNights() + " nights)";
    }
}
