// src/main/java/com/hotel/reservation/model/Reservation.java
package com.hotel.reservation.model;

import com.hotel.user.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Domain model for a hotel booking (single-table inheritance).
 *
 * The {@code booking_type} column decides whether the concrete instance is
 * {@link OnlineReservation} (guest self-service) or {@link DeskReservation}
 * (created by staff). Use {@link #create(String, String, String, String,
 * LocalDate, LocalDate, int, String, String, boolean)} to reconstruct
 * the correct subclass from a database row.
 *
 * Encapsulation: all fields are private; state is exposed through getters and
 * the {@link ReservationBuilder} enforces required values at build time.
 */
public abstract class Reservation {

    // ---- persisted fields (match the bookings table columns) ----
    private String id;
    private String guestId;
    private String roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int guests;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private boolean checkedIn;

    // ---- constructor (package-private, used by subclasses) ----

    protected Reservation() {
        this.status = BookingStatus.ACTIVE;
        this.paymentStatus = PaymentStatus.PENDING;
        this.checkedIn = false;
    }

    // ---- factory: reconstruct the right subclass from a DB row ----

    /**
     * Factory method that mirrors {@code Room.create()} — picks the subclass
     * from the {@code booking_type} discriminator column.
     */
    public static Reservation create(String id, String guestId, String roomId,
                                     String bookingType,
                                     LocalDate checkIn, LocalDate checkOut, int guests,
                                     String status, String paymentStatus, boolean checkedIn) {
        BookingType type = BookingType.fromString(bookingType);
        Reservation r = (type == BookingType.ONLINE)
                ? new OnlineReservation()
                : new DeskReservation();
        r.id = id;
        r.guestId = guestId;
        r.roomId = roomId;
        r.checkIn = checkIn;
        r.checkOut = checkOut;
        r.guests = guests;
        r.status = BookingStatus.fromString(status);
        r.paymentStatus = PaymentStatus.fromString(paymentStatus);
        r.checkedIn = checkedIn;
        return r;
    }

    // ---- abstract: subclass identity ----

    /** Returns {@link BookingType#ONLINE} or {@link BookingType#DESK}. */
    public abstract BookingType getBookingType();

    /** Human label shown on booking cards, e.g. "Online" or "Front desk". */
    public String getSourceLabel() {
        return getBookingType().getLabel();
    }

    // ---- computed properties (used by JSPs via EL) ----

    /** Number of nights in the stay. */
    public int getNights() {
        return (int) ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    /** Runtime state derived from dates, status and checked_in flag. */
    public BookingState getState() {
        return BookingState.of(status, checkedIn, checkIn, checkOut);
    }

    /** Formatted check-in label, e.g. "15 Dec 2026". */
    public String getCheckInLabel() {
        return new DateRange(checkIn, checkOut).getCheckInLabel();
    }

    /** Formatted check-out label, e.g. "18 Dec 2026". */
    public String getCheckOutLabel() {
        return new DateRange(checkIn, checkOut).getCheckOutLabel();
    }

    /** Human-readable stay summary. */
    public String getStayLabel() {
        return new DateRange(checkIn, checkOut).getStayLabel();
    }

    /** Days until check-in (0 if today or past). */
    public long getDaysUntilCheckIn() {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), checkIn);
        return Math.max(0, days);
    }

    /** Free cancellation policy text. */
    public String getCancellationPolicy() {
        return "Free cancellation any time before check-in day.";
    }

    // ---- permission checks (IDOR protection) ----

    /**
     * A guest can cancel their own ONLINE booking before check-in,
     * as long as it is still active and not yet paid.
     */
    public boolean isGuestCancellable() {
        if (status != BookingStatus.ACTIVE) return false;
        if (paymentStatus == PaymentStatus.PAID) return false;
        if (checkedIn) return false;
        if (!LocalDate.now().isBefore(checkIn)) return false;
        return getBookingType() == BookingType.ONLINE;
    }

    /** Staff can cancel any active booking that has not been checked in. */
    public boolean isStaffCancellable() {
        return status == BookingStatus.ACTIVE && !checkedIn;
    }

    /** Guest can change dates on their own online booking before check-in if not paid. */
    public boolean isGuestDatesEditable() {
        if (status != BookingStatus.ACTIVE) return false;
        if (paymentStatus == PaymentStatus.PAID) return false;
        if (checkedIn) return false;
        if (!LocalDate.now().isBefore(checkIn)) return false;
        return getBookingType() == BookingType.ONLINE;
    }

    /** Staff can change dates on any active, unpaid, not-checked-in booking. */
    public boolean isStaffDatesEditable() {
        return status == BookingStatus.ACTIVE
                && paymentStatus != PaymentStatus.PAID
                && !checkedIn;
    }

    /** Booking is payable when active and payment is pending. */
    public boolean isPayable() {
        return status == BookingStatus.ACTIVE && paymentStatus == PaymentStatus.PENDING;
    }

    /** Check-in is due when arriving today and not yet checked in. */
    public boolean isCheckInDue() {
        return status == BookingStatus.ACTIVE
                && !checkedIn
                && !LocalDate.now().isBefore(checkIn)
                && LocalDate.now().isBefore(checkOut);
    }

    /** A refund is pending if the booking is cancelled but was already paid. */
    public boolean isRefundPending() {
        return status == BookingStatus.CANCELLED && paymentStatus == PaymentStatus.PAID;
    }

    /** Guest initials for avatar display (first letter of first + last name). */
    // Note: actual initials need the guest name, handled in BookingDetails.

    /**
     * IDOR check: does the given user own this booking?
     */
    public boolean isOwnedBy(User user) {
        return user != null && user.getId().equals(guestId);
    }

    /**
     * Can the given user view this booking?
     * Owners can view their own; staff can view any.
     */
    public boolean canBeViewedBy(User user) {
        return user != null && (isOwnedBy(user) || user.canManageAnyBooking());
    }

    // ---- getters ----

    public String getId() { return id; }
    public String getGuestId() { return guestId; }
    public String getRoomId() { return roomId; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public int getGuests() { return guests; }
    public BookingStatus getStatus() { return status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public boolean isCheckedIn() { return checkedIn; }

    // ---- setters (used by DAO/Service) ----

    public void setId(String id) { this.id = id; }
    public void setGuestId(String guestId) { this.guestId = guestId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }
    public void setGuests(int guests) { this.guests = guests; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setCheckedIn(boolean checkedIn) { this.checkedIn = checkedIn; }

    // ---- builder ----

    /**
     * Fluent builder for creating a new Reservation before it is persisted.
     * Usage: {@code Reservation.builder().online().guestId("G01")...build()}
     */
    public static ReservationBuilder builder() {
        return new ReservationBuilder();
    }

    /**
     * Builder that enforces required fields and constructs the correct subclass.
     */
    public static class ReservationBuilder {
        private BookingType type;
        private String guestId;
        private String roomId;
        private LocalDate checkIn;
        private LocalDate checkOut;
        private int guests;

        public ReservationBuilder online() { this.type = BookingType.ONLINE; return this; }
        public ReservationBuilder desk()   { this.type = BookingType.DESK;   return this; }
        public ReservationBuilder guestId(String guestId)     { this.guestId = guestId;   return this; }
        public ReservationBuilder roomId(String roomId)       { this.roomId = roomId;     return this; }
        public ReservationBuilder checkIn(LocalDate checkIn)  { this.checkIn = checkIn;   return this; }
        public ReservationBuilder checkOut(LocalDate checkOut) { this.checkOut = checkOut; return this; }
        public ReservationBuilder guests(int guests)          { this.guests = guests;     return this; }

        /**
         * Validates and builds the Reservation. The ID is not set yet —
         * {@link com.hotel.reservation.service.ReservationService} assigns it.
         */
        public Reservation build() {
            if (type == null) throw new IllegalStateException("Booking type is required");
            if (guestId == null) throw new IllegalStateException("Guest ID is required");
            if (roomId == null) throw new IllegalStateException("Room ID is required");
            // DateRange validates checkIn < checkOut
            new DateRange(checkIn, checkOut);
            if (guests < 1) throw new IllegalStateException("At least 1 guest is required");

            Reservation r = (type == BookingType.ONLINE)
                    ? new OnlineReservation()
                    : new DeskReservation();
            r.guestId = guestId;
            r.roomId = roomId;
            r.checkIn = checkIn;
            r.checkOut = checkOut;
            r.guests = guests;
            return r;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + id + " " + guestId + " " + roomId
                + " " + checkIn + "→" + checkOut + " " + status + "]";
    }
}
