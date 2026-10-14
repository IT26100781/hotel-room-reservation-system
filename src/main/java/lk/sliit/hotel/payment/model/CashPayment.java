package lk.sliit.hotel.payment.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/** Inheritance and polymorphism: cash overrides calculateTotal to apply its saved discount. */
public final class CashPayment extends Payment {
    private final BigDecimal discountRate;
    public CashPayment(String id, String bookingId, BigDecimal amount, PaymentStatus status,
                       LocalDate date, Instant createdAt, BigDecimal discountRate) {
        this(id, bookingId, amount, status, date, createdAt, discountRate, null);
    }
    public CashPayment(String id, String bookingId, BigDecimal amount, PaymentStatus status,
                       LocalDate date, Instant createdAt, BigDecimal discountRate, BillingDetails billing) {
        super(id, bookingId, amount, status, date, createdAt, billing);
        this.discountRate = Objects.requireNonNull(discountRate);
        if (discountRate.signum() < 0 || discountRate.compareTo(BigDecimal.ONE) >= 0)
            throw new IllegalArgumentException("Cash discount must be >= 0 and < 1");
        if (calculateTotal().signum() <= 0)
            throw new IllegalArgumentException("Total after discount must be at least 0.01");
    }
    @Override public PaymentMethod getMethod() { return PaymentMethod.CASH; }
    @Override public BigDecimal getDiscountRate() { return discountRate; }
    @Override public BigDecimal calculateTotal() {
        return getAmount().multiply(BigDecimal.ONE.subtract(discountRate)).setScale(2, RoundingMode.HALF_UP);
    }
}
