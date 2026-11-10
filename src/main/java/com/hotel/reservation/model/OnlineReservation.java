// src/main/java/com/hotel/reservation/model/OnlineReservation.java
package com.hotel.reservation.model;

/**
 * A booking made by a guest through the website.
 *
 * Guests can cancel or change dates on their own online bookings
 * (before check-in day, while payment is still pending).
 * Created by {@link com.hotel.reservation.servlet.BookRoomServlet}.
 */
public class OnlineReservation extends Reservation {

    /** Package-private constructor — use {@link Reservation#builder()} or {@link Reservation#create}. */
    OnlineReservation() {
        super();
    }

    @Override
    public BookingType getBookingType() {
        return BookingType.ONLINE;
    }
}
