package lk.sliit.hotel.payment.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/** Encapsulation: fields are private and status changes must obey the lifecycle. */
public abstract class Payment {
    private final String id;
    private final String bookingId;
    private final BigDecimal amount;
    private PaymentStatus status;
    private final LocalDate paymentDate;
    private final Instant createdAt;
    private final BillingDetails billing;

    protected Payment(String id, String bookingId, BigDecimal amount, PaymentStatus status,
                      LocalDate paymentDate, Instant createdAt) {
        this(id, bookingId, amount, status, paymentDate, createdAt, null);
    }

    protected Payment(String id, String bookingId, BigDecimal amount, PaymentStatus status,
                      LocalDate paymentDate, Instant createdAt, BillingDetails billing) {
        this.billing = billing;
        if (billing != null && billing.amountDue().compareTo(amount) != 0)
            throw new IllegalArgumentException("Billing snapshot does not match payment amount");
        this.id = identifier(id);
        this.bookingId = identifier(bookingId);
        Objects.requireNonNull(amount, "Amount required");
        if (amount.signum() <= 0 || amount.compareTo(new BigDecimal("999999999.99")) > 0)
            throw new IllegalArgumentException("Amount must be between 0.01 and 999999999.99");
        try { this.amount = amount.setScale(2, RoundingMode.UNNECESSARY); }
        catch (ArithmeticException ex) { throw new IllegalArgumentException("Use at most two decimal places"); }
        this.status = Objects.requireNonNull(status);
        this.paymentDate = Objects.requireNonNull(paymentDate);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    private static String identifier(String value) {
        if (value == null || !value.matches("[A-Za-z0-9_-]{1,64}"))
            throw new IllegalArgumentException("ID must contain 1-64 letters, digits, hyphens or underscores");
        return value;
    }

    public String getId() { return id; }
    public String getBookingId() { return bookingId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public BillingDetails getBilling() { return billing; }
    public Instant getCreatedAt() { return createdAt; }
    public abstract PaymentMethod getMethod();
    public abstract BigDecimal getDiscountRate();
    public abstract BigDecimal calculateTotal();
    public String getCardBrand() { return ""; }
    public String getLastFour() { return ""; }
    public BigDecimal getDiscountAmount() { return amount.subtract(calculateTotal()); }

    public void changeStatus(PaymentStatus next) {
        Objects.requireNonNull(next);
        if (next == status) return;
        if (!((status == PaymentStatus.PENDING && next == PaymentStatus.PAID)
                || (status == PaymentStatus.PAID && next == PaymentStatus.REFUNDED)))
            throw new IllegalStateException("Allowed changes: PENDING -> PAID -> REFUNDED");
        status = next;
    }
}
