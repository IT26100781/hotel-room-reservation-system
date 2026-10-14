package lk.sliit.hotel.payment.integration;

import lk.sliit.hotel.payment.model.BillingDetails;
import java.math.BigDecimal;

/** Read-only information supplied by the booking component. amountDue is AFTER VIP discount. */
public record BookingInfo(String id, String guestName, String room, String status,
                          BigDecimal amountDue, BillingDetails billing) {
    public BookingInfo(String id, String guestName, String room, String status, BigDecimal amountDue) {
        this(id, guestName, room, status, amountDue, null);
    }
    public BookingInfo {
        if (billing != null && billing.amountDue().compareTo(amountDue) != 0)
            throw new IllegalArgumentException("Booking amount must match the VIP-adjusted room total");
    }
    public boolean cancelled() { return "CANCELLED".equalsIgnoreCase(status); }
}
