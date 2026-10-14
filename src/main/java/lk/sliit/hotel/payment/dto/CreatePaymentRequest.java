package lk.sliit.hotel.payment.dto;

import jakarta.validation.constraints.*;
import lk.sliit.hotel.payment.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePaymentRequest(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{1,64}") String bookingId,
        @DecimalMin("0.01") @DecimalMax("999999999.99") @Digits(integer=9, fraction=2) BigDecimal amount,
        @NotNull PaymentMethod method, @NotNull PaymentStatus status,
        @NotNull LocalDate paymentDate,
        @Pattern(regexp = "(VISA|MASTERCARD|AMEX|OTHER)?") String cardBrand,
        @Pattern(regexp = "([0-9]{4})?") String lastFour
) { }
