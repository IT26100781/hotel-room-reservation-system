package lk.sliit.hotel.payment.integration;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;
import lk.sliit.hotel.payment.model.BillingDetails;

/** Illustrative fixtures from the frontend's booking references. Not real booking storage. */
@Component
@Profile("demo")
public class DemoBookingGateway implements BookingGateway {
    public List<BookingInfo> findAll() {
        return List.of(
            fixture("BK1024", "Nethmi Perera", "nethmi@example.com", "Estate Suite · Room 301", "2026-10-12", "2026-10-15", "138000", "0.10"),
            fixture("BK1026", "Dilini Jayasuriya", "", "Deluxe Valley Room · 201", "2026-10-07", "2026-10-10", "84000", "0"),
            fixture("BK1031", "Nethmi Perera", "nethmi@example.com", "Deluxe Valley Room · 201", "2026-12-20", "2026-12-24", "112000", "0.10"),
            new BookingInfo("BK1028", "Sarah Collins", "Deluxe 204", "CONFIRMED", new BigDecimal("55500.00")),
            new BookingInfo("BK1027", "Ravindu Bandara", "Suite 301", "CONFIRMED", new BigDecimal("124200.00")),
            new BookingInfo("BK1025", "Ashan Fernando", "Standard 102", "CONFIRMED", new BigDecimal("37000.00")),
            new BookingInfo("BK0990", "Nethmi Perera", "Deluxe 205", "CANCELLED", new BigDecimal("82800.00"))
        );
    }
    private BookingInfo fixture(String id, String guest, String email, String room, String from,
                                String to, String total, String vipRate) {
        BillingDetails billing = new BillingDetails(guest, email, room, LocalDate.parse(from),
                LocalDate.parse(to), new BigDecimal(total), new BigDecimal(vipRate));
        return new BookingInfo(id, guest, room, "CONFIRMED", billing.amountDue(), billing);
    }
}
