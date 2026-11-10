// src/main/java/com/hotel/reservation/model/DeskReservation.java
package com.hotel.reservation.model;

/**
 * A booking created by staff at the front desk for a walk-in or phone guest.
 *
 * Desk bookings can only be modified or cancelled by staff — the guest
 * cannot self-service them from "My bookings".
 * Created by {@link com.hotel.reservation.servlet.BookingNewServlet}.
 */
public class DeskReservation extends Reservation {

    /** Package-private constructor — use {@link Reservation#builder()} or {@link Reservation#create}. */
    DeskReservation() {
        super();
    }

    @Override
    public BookingType getBookingType() {
        return BookingType.DESK;
    }
}
