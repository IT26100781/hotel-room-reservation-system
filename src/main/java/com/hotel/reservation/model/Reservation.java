package com.hotel.reservation.model;

import com.hotel.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a hotel booking.
 * Encapsulates stay dates, pricing, payment state, check-in state, and security rules.
 */
public class Reservation {

    private final String id;
    private final String guestId;
    private final String roomId;
    private final DateRange dateRange;
    private final int guests;
    private final double totalAmount;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private boolean checkedIn;
    private final LocalDateTime createdAt;

    // Transient metadata for UI rendering
    private String roomName;
    private int roomNumber;
    private String guestName;

    public Reservation(String id, String guestId, String roomId, DateRange dateRange,
                       int guests, double totalAmount, BookingStatus status,
                       PaymentStatus paymentStatus, boolean checkedIn, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id, "Reservation id cannot be null");
        this.guestId = Objects.requireNonNull(guestId, "Guest id cannot be null");
        this.roomId = Objects.requireNonNull(roomId, "Room id cannot be null");
        this.dateRange = Objects.requireNonNull(dateRange, "Date range cannot be null");
        if (guests < 1) {
            throw new IllegalArgumentException("Number of guests must be at least 1");
        }
        if (totalAmount < 0.0) {
            throw new IllegalArgumentException("Total amount cannot be negative");
        }
        this.guests = guests;
        this.totalAmount = totalAmount;
        this.status = status != null ? status : BookingStatus.ACTIVE;
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PENDING;
        this.checkedIn = checkedIn;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getGuestId() { return guestId; }
    public String getRoomId() { return roomId; }
    public DateRange getDateRange() { return dateRange; }
    public LocalDate getCheckIn() { return dateRange.getCheckIn(); }
    public LocalDate getCheckOut() { return dateRange.getCheckOut(); }
    public long getNights() { return dateRange.getNights(); }
    public int getGuests() { return guests; }
    public double getTotalAmount() { return totalAmount; }
    public BookingStatus getStatus() { return status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public boolean isCheckedIn() { return checkedIn; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getFormattedAmount() {
        return String.format("%,.2f", totalAmount);
    }

    public boolean isActive() {
        return status == BookingStatus.ACTIVE;
    }

    public boolean isCancelled() {
        return status == BookingStatus.CANCELLED;
    }

    public boolean isPaid() {
        return paymentStatus == PaymentStatus.PAID;
    }

    public boolean isPendingPayment() {
        return paymentStatus == PaymentStatus.PENDING;
    }

    public boolean isRefunded() {
        return paymentStatus == PaymentStatus.REFUNDED;
    }

    public boolean isArrivingToday(LocalDate today) {
        return isActive() && !checkedIn && dateRange.getCheckIn().equals(today);
    }

    public boolean isInHouse(LocalDate today) {
        return isActive() && checkedIn && dateRange.isCurrent(today);
    }

    public boolean isUpcoming(LocalDate today) {
        return isActive() && !checkedIn && dateRange.isUpcoming(today);
    }

    public boolean isCompleted(LocalDate today) {
        return isActive() && dateRange.isPast(today);
    }

    public boolean isNoShow(LocalDate today) {
        return isActive() && !checkedIn && dateRange.getCheckIn().isBefore(today);
    }

    /**
     * Determines whether a given user can cancel this reservation.
     * Prevents IDOR:
     * - Staff with canManageAnyBooking() can cancel active bookings before check-in.
     * - The guest who owns the booking can cancel BEFORE the check-in date.
     */
    public boolean canBeCancelledBy(User user) {
        if (user == null || !isActive()) {
            return false;
        }
        if (user.canManageAnyBooking()) {
            return !checkedIn;
        }
        if (user.getId().equals(this.guestId)) {
            return !checkedIn && !dateRange.getCheckIn().isBefore(LocalDate.now());
        }
        return false;
    }

    public void cancel() {
        if (checkedIn) {
            throw new IllegalStateException("Cannot cancel a booking that has already checked in.");
        }
        this.status = BookingStatus.CANCELLED;
    }

    public void markCheckedIn() {
        if (!isActive()) {
            throw new IllegalStateException("Cannot check in a cancelled booking.");
        }
        this.checkedIn = true;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = Objects.requireNonNull(paymentStatus, "Payment status cannot be null");
    }

    public static class Builder {
        private String id;
        private String guestId;
        private String roomId;
        private DateRange dateRange;
        private int guests = 1;
        private double totalAmount;
        private BookingStatus status = BookingStatus.ACTIVE;
        private PaymentStatus paymentStatus = PaymentStatus.PENDING;
        private boolean checkedIn = false;
        private LocalDateTime createdAt = LocalDateTime.now();

        public Builder id(String id) { this.id = id; return this; }
        public Builder guestId(String guestId) { this.guestId = guestId; return this; }
        public Builder roomId(String roomId) { this.roomId = roomId; return this; }
        public Builder dateRange(DateRange dateRange) { this.dateRange = dateRange; return this; }
        public Builder dateRange(LocalDate checkIn, LocalDate checkOut) {
            this.dateRange = new DateRange(checkIn, checkOut);
            return this;
        }
        public Builder guests(int guests) { this.guests = guests; return this; }
        public Builder totalAmount(double totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder status(BookingStatus status) { this.status = status; return this; }
        public Builder paymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public Builder checkedIn(boolean checkedIn) { this.checkedIn = checkedIn; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Reservation build() {
            return new Reservation(id, guestId, roomId, dateRange, guests, totalAmount, status, paymentStatus, checkedIn, createdAt);
        }
    }
}
