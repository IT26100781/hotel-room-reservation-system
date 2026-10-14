package lk.sliit.hotel.payment.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/** Immutable booking/pricing snapshot saved with a payment, before the cash discount. */
public record BillingDetails(String guestName, String guestEmail, String room,
        LocalDate checkIn, LocalDate checkOut, BigDecimal roomTotal, BigDecimal vipDiscountRate) {
    public BillingDetails {
        Objects.requireNonNull(guestName);
        Objects.requireNonNull(room);
        Objects.requireNonNull(roomTotal);
        Objects.requireNonNull(vipDiscountRate);
        guestEmail = guestEmail == null ? "" : guestEmail;
        if (roomTotal.signum() <= 0 || roomTotal.compareTo(new BigDecimal("999999999.99")) > 0)
            throw new IllegalArgumentException("Invalid booking room total");
        roomTotal = roomTotal.setScale(2, RoundingMode.UNNECESSARY);
        if (vipDiscountRate.signum() < 0 || vipDiscountRate.compareTo(BigDecimal.ONE) >= 0)
            throw new IllegalArgumentException("VIP discount must be >= 0 and < 1");
        if ((checkIn == null) != (checkOut == null) || (checkIn != null && !checkOut.isAfter(checkIn)))
            throw new IllegalArgumentException("Stay must end after check-in");
    }
    public BigDecimal amountDue() {
        return roomTotal.multiply(BigDecimal.ONE.subtract(vipDiscountRate)).setScale(2, RoundingMode.HALF_UP);
    }
    public BigDecimal vipDiscountAmount() { return roomTotal.subtract(amountDue()); }
    public long nights() { return checkIn == null ? 0 : ChronoUnit.DAYS.between(checkIn, checkOut); }
}
