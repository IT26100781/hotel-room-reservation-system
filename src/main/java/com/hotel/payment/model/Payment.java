package com.hotel.payment.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/** Immutable ledger entry. Amount and rates are snapshots, not browser-supplied prices. */
public abstract class Payment {
    private final String id, bookingId;
    private final BigDecimal nightlyRate, roomTotal, guestRate, amount;
    private final PaymentStatus status;
    private final LocalDate paidOn;
    protected Payment(String id, String bookingId, BigDecimal nightlyRate, BigDecimal roomTotal,
                      BigDecimal guestRate, BigDecimal amount, PaymentStatus status, LocalDate paidOn) {
        this.id=id; this.bookingId=bookingId; this.nightlyRate=nightlyRate; this.roomTotal=roomTotal;
        this.guestRate=guestRate; this.amount=amount; this.status=status; this.paidOn=paidOn;
    }
    public abstract BigDecimal calculateTotal();
    public abstract String getMethod();
    public abstract BigDecimal getMethodRate();
    protected BigDecimal total(BigDecimal methodRate) {
        return roomTotal.subtract(getGuestDiscount()).multiply(BigDecimal.ONE.subtract(methodRate)).setScale(2, RoundingMode.HALF_UP);
    }
    public BigDecimal getAmount() { return amount == null ? calculateTotal() : amount; }
    public BigDecimal getGuestDiscount() { return roomTotal.multiply(guestRate).setScale(2, RoundingMode.HALF_UP); }
    public BigDecimal getMethodDiscount() { return roomTotal.subtract(getGuestDiscount()).subtract(getAmount()); }
    public BigDecimal getCombinedRate() { return BigDecimal.ONE.subtract(BigDecimal.ONE.subtract(guestRate).multiply(BigDecimal.ONE.subtract(getMethodRate()))); }
    public String getId() { return id; }
    public String getBookingId() { return bookingId; }
    public BigDecimal getNightlyRate() { return nightlyRate; }
    public BigDecimal getRoomTotal() { return roomTotal; }
    public BigDecimal getGuestRate() { return guestRate; }
    public PaymentStatus getStatus() { return status; }
    public LocalDate getPaidOn() { return paidOn; }
    public String getPaidOnLabel() { return paidOn.toString(); }
    public boolean isRefunded() { return status == PaymentStatus.REFUNDED; }
    public boolean isPending() { return status == PaymentStatus.PENDING; }
    public String getCardType() { return null; }
    public String getCardLast4() { return null; }
    public String getMethodLabel() { return "Cash"; }
}
