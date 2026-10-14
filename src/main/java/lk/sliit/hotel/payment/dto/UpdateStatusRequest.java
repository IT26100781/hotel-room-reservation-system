package lk.sliit.hotel.payment.dto;
import jakarta.validation.constraints.NotNull;
import lk.sliit.hotel.payment.model.PaymentStatus;
public record UpdateStatusRequest(@NotNull PaymentStatus status) { }
