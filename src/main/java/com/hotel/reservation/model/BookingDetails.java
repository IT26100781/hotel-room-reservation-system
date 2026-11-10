// src/main/java/com/hotel/reservation/model/BookingDetails.java
package com.hotel.reservation.model;

import com.hotel.room.model.Room;
import com.hotel.user.guest.model.Guest;

/**
 * Read-only DTO that wraps a {@link Reservation} with its related {@link Room}
 * and {@link Guest}, plus computed pricing fields the JSPs need.
 *
 * This is the main object passed to every reservation JSP as a request attribute.
 * It is never persisted — it is assembled in the service layer each time a
 * booking is loaded.
 *
 * Pricing logic:
 *   fullRateTotal  = room.calculateTotalRate(nights)          // polymorphic (Standard/Deluxe/Suite)
 *   roomDiscount   = (baseRate * nights) - fullRateTotal      // long-stay discount baked into Room
 *   guestRate      = guest.getDiscount()                      // 0 for regular, 0.10 for VIP
 *   guestDiscount  = fullRateTotal * guestRate
 *   estimatedTotal = fullRateTotal - guestDiscount
 */
public class BookingDetails {

    private final Reservation reservation;
    private final Room room;
    private final Guest guest;

    // ---- pre-computed pricing (doubles, matching Room.calculateTotalRate) ----
    private final double fullRateTotal;
    private final double roomDiscount;
    private final double guestRate;
    private final double guestDiscount;
    private final double estimatedTotal;

    /**
     * Assemble a BookingDetails from its parts.
     */
    public BookingDetails(Reservation reservation, Room room, Guest guest) {
        this.reservation = reservation;
        this.room = room;
        this.guest = guest;

        int nights = reservation.getNights();
        double baseTotal = room.getBaseRate() * nights;
        this.fullRateTotal = room.calculateTotalRate(nights);
        this.roomDiscount = baseTotal - fullRateTotal;
        this.guestRate = guest.getDiscount();
        this.guestDiscount = fullRateTotal * guestRate;
        this.estimatedTotal = fullRateTotal - guestDiscount;
    }

    // ---- delegates to Reservation (convenience for JSP EL) ----

    public Reservation getReservation() { return reservation; }
    public Room getRoom()               { return room; }
    public Guest getGuest()             { return guest; }

    // ---- pricing getters ----

    /** Room rate × nights before any discount. */
    public double getFullRateTotal()  { return fullRateTotal; }

    /** Amount saved by the room's own multi-night pricing (e.g. Suite 7-night deal). */
    public double getRoomDiscount()   { return roomDiscount; }

    /** Guest discount rate (0.0 or 0.10 for VIP). */
    public double getGuestRate()      { return guestRate; }

    /** Amount saved by VIP discount. */
    public double getGuestDiscount()  { return guestDiscount; }

    /** Final estimated total after all discounts. */
    public double getEstimatedTotal() { return estimatedTotal; }

    // ---- display helpers used by booking-card.jspf ----

    /** Label like "Booked online" or "Front desk". */
    public String getSourceLabel() {
        return reservation.getSourceLabel();
    }

    /** First letter of first name + first letter of last name (or just first letter). */
    public String getGuestInitials() {
        String name = guest.getName();
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
