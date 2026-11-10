package com.hotel.payment.model;

import com.hotel.room.model.Room;
import com.hotel.user.guest.model.Guest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Payment-side view of a reservation; replace the adapter when ReservationService is ready.
 */
public final class BookingBill {
    private final String id, status;
    private final LocalDate checkIn, checkOut;
    private final boolean checkedIn;
    private final Guest guest;
    private final Room room;

    public BookingBill(String id, String status, LocalDate checkIn, LocalDate checkOut, boolean checkedIn, Guest guest, Room room) {
        this.id = id;
        this.status = status;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.checkedIn = checkedIn;
        this.guest = guest;
        this.room = room;
    }

    public BookingBill getReservation() {
        return this;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public String getCheckInLabel() {
        return checkIn.toString();
    }

    public String getCheckOutLabel() {
        return checkOut.toString();
    }

    public String getStayLabel() {
        return checkIn + " – " + checkOut;
    }

    public Guest getGuest() {
        return guest;
    }

    public Room getRoom() {
        return room;
    }

    public BigDecimal getGuestRate() {
        return BigDecimal.valueOf(guest.getDiscount());
    }

    public BigDecimal getNightlyRate() {
        return BigDecimal.valueOf(room.getBaseRate()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getRoomTotal() {
        return BigDecimal.valueOf(room.calculateTotalRate(Math.toIntExact(getNights()))).setScale(2, RoundingMode.HALF_UP);
    }
}
