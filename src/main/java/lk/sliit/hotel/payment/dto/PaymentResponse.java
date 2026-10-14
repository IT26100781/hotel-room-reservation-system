package lk.sliit.hotel.payment.dto;

import lk.sliit.hotel.payment.model.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record PaymentResponse(String id, String bookingId, String guestName, String bookingStatus,
        BigDecimal amount, PaymentMethod method, PaymentStatus status, LocalDate paymentDate,
        BigDecimal discountRate, BigDecimal discountAmount, BigDecimal total, String currency,
        String cardBrand, String lastFour, Instant createdAt, BillingDetails billing) {
    public static PaymentResponse from(Payment p, String guest, String bookingStatus) {
        return new PaymentResponse(p.getId(), p.getBookingId(), guest, bookingStatus, p.getAmount(),
            p.getMethod(), p.getStatus(), p.getPaymentDate(), p.getDiscountRate(), p.getDiscountAmount(),
            p.calculateTotal(), "LKR", p.getCardBrand(), p.getLastFour(), p.getCreatedAt(), p.getBilling());
    }
}
