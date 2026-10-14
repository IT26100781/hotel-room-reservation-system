package lk.sliit.hotel.payment.integration;

import lk.sliit.hotel.payment.exception.RecordNotFoundException;
import java.util.List;

/** Implement this with the booking team's service when merging the group project. */
public interface BookingGateway {
    List<BookingInfo> findAll();
    default BookingInfo require(String id) {
        return findAll().stream().filter(b -> b.id().equals(id)).findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Booking not found: " + id));
    }
    default BookingInfo requirePayable(String id) {
        BookingInfo b = require(id);
        if (b.cancelled()) throw new IllegalStateException("Cannot take payment for a cancelled booking");
        return b;
    }
}
