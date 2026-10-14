package lk.sliit.hotel.payment.repository;

import lk.sliit.hotel.payment.model.Payment;
import java.util.List;

/** Abstraction: callers do not need to know the file format. */
public interface PaymentRepository {
    List<Payment> findAll();
    void saveAll(List<Payment> payments);
}
