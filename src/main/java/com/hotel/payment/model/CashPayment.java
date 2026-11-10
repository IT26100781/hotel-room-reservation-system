package com.hotel.payment.model;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class CashPayment extends Payment {
    public static final BigDecimal DISCOUNT = new BigDecimal("0.05");
    public CashPayment(String id, String bookingId, BigDecimal nightlyRate, BigDecimal roomTotal,
                       BigDecimal guestRate, BigDecimal amount, PaymentStatus status, LocalDate date) {
        super(id, bookingId, nightlyRate, roomTotal, guestRate, amount, status, date);
    }
    @Override public BigDecimal calculateTotal() { return total(DISCOUNT); }
    @Override public String getMethod() { return "cash"; }
    @Override public BigDecimal getMethodRate() { return DISCOUNT; }
}
