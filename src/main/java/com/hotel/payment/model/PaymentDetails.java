package com.hotel.payment.model;

import java.math.BigDecimal;

public final class PaymentDetails {
    private final Payment payment;
    private final BookingBill booking;

    public PaymentDetails(Payment payment, BookingBill booking) {
        this.payment = payment;
        this.booking = booking;
    }

    public Payment getPayment() {
        return payment;
    }

    public BookingBill getBooking() {
        return booking;
    }

    public BigDecimal getFullRateTotal() {
        return payment.getNightlyRate().multiply(BigDecimal.valueOf(booking.getNights()));
    }

    public BigDecimal getRoomDiscount() {
        return getFullRateTotal().subtract(payment.getRoomTotal());
    }
}
