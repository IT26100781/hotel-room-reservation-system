// src/main/java/com/hotel/reservation/model/BookingMetrics.java
package com.hotel.reservation.model;

/**
 * Dashboard statistics shown on {@code manage.jsp} for staff.
 *
 * All counts are computed in a single pass in
 * {@link com.hotel.reservation.service.ReservationService#getMetrics}.
 */
public class BookingMetrics {

    private final int arrivingToday;
    private final int checkedInToday;
    private final int inHouse;
    private final int paymentPending;
    private final int noShowsThisWeek;

    public BookingMetrics(int arrivingToday, int checkedInToday, int inHouse,
                          int paymentPending, int noShowsThisWeek) {
        this.arrivingToday = arrivingToday;
        this.checkedInToday = checkedInToday;
        this.inHouse = inHouse;
        this.paymentPending = paymentPending;
        this.noShowsThisWeek = noShowsThisWeek;
    }

    public int getArrivingToday()   { return arrivingToday; }
    public int getCheckedInToday()  { return checkedInToday; }
    public int getInHouse()         { return inHouse; }
    public int getPaymentPending()  { return paymentPending; }
    public int getNoShowsThisWeek() { return noShowsThisWeek; }
}
