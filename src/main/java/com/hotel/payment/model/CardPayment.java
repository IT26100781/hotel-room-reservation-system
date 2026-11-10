package com.hotel.payment.model;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class CardPayment extends Payment {
    private final String cardType, cardLast4;
    public CardPayment(String id, String bookingId, BigDecimal nightlyRate, BigDecimal roomTotal,
                       BigDecimal guestRate, BigDecimal amount, PaymentStatus status, LocalDate date,
                       String cardType, String cardLast4) {
        super(id, bookingId, nightlyRate, roomTotal, guestRate, amount, status, date);
        this.cardType=cardType; this.cardLast4=cardLast4;
    }
    @Override public BigDecimal calculateTotal() { return total(BigDecimal.ZERO); }
    @Override public String getMethod() { return "card"; }
    @Override public BigDecimal getMethodRate() { return BigDecimal.ZERO; }
    @Override public String getCardType() { return cardType; }
    @Override public String getCardLast4() { return cardLast4; }
    @Override public String getMethodLabel() { return cardType + " •••• " + cardLast4 + " (simulated)"; }
}
