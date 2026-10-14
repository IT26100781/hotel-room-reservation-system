package lk.sliit.hotel.payment.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

/** A desk transaction reference only. Never accepts a full card number or CVV. */
public final class CardPayment extends Payment {
    private final String cardBrand;
    private final String lastFour;
    public CardPayment(String id, String bookingId, BigDecimal amount, PaymentStatus status,
                       LocalDate date, Instant createdAt, String cardBrand, String lastFour) {
        this(id, bookingId, amount, status, date, createdAt, cardBrand, lastFour, null);
    }
    public CardPayment(String id, String bookingId, BigDecimal amount, PaymentStatus status,
                       LocalDate date, Instant createdAt, String cardBrand, String lastFour, BillingDetails billing) {
        super(id, bookingId, amount, status, date, createdAt, billing);
        this.cardBrand = cardBrand == null ? "" : cardBrand;
        this.lastFour = lastFour == null ? "" : lastFour;
        if (!Set.of("", "VISA", "MASTERCARD", "AMEX", "OTHER").contains(this.cardBrand))
            throw new IllegalArgumentException("Unsupported card brand");
        if (!this.lastFour.matches("([0-9]{4})?"))
            throw new IllegalArgumentException("Last four must be exactly four digits, or empty");
    }
    @Override public PaymentMethod getMethod() { return PaymentMethod.CARD; }
    @Override public BigDecimal getDiscountRate() { return BigDecimal.ZERO; }
    @Override public BigDecimal calculateTotal() { return getAmount(); }
    @Override public String getCardBrand() { return cardBrand; }
    @Override public String getLastFour() { return lastFour; }
}
