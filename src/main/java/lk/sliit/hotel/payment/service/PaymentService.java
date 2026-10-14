package lk.sliit.hotel.payment.service;

import lk.sliit.hotel.payment.dto.*;
import lk.sliit.hotel.payment.exception.RecordNotFoundException;
import lk.sliit.hotel.payment.integration.*;
import lk.sliit.hotel.payment.model.*;
import lk.sliit.hotel.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.*;

@Service
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
public class PaymentService {
    private final PaymentRepository repository;
    private final BookingGateway bookings;
    private final Clock clock;
    private final BigDecimal cashDiscountRate;

    public PaymentService(PaymentRepository repository, BookingGateway bookings, Clock clock,
                          @Value("${payment.cash-discount-rate}") BigDecimal cashDiscountRate) {
        this.repository = repository;
        this.bookings = bookings;
        this.clock = clock;
        if (cashDiscountRate.signum() < 0 || cashDiscountRate.compareTo(BigDecimal.ONE) >= 0)
            throw new IllegalArgumentException("payment.cash-discount-rate must be >= 0 and < 1");
        this.cashDiscountRate = cashDiscountRate;
    }

    // One application process owns the file. Synchronizing the whole operation prevents lost updates.
    public synchronized PaymentResponse create(CreatePaymentRequest request) {
        BookingInfo booking = bookings.requirePayable(request.bookingId());
        if (request.status() == PaymentStatus.REFUNDED)
            throw new IllegalArgumentException("Create PENDING or PAID; refund a paid transaction later");
        if (request.paymentDate().isAfter(LocalDate.now(clock)))
            throw new IllegalArgumentException("Payment date cannot be in the future");
        List<Payment> payments = repository.findAll();
        if (payments.stream().anyMatch(p -> p.getBookingId().equals(request.bookingId())
                && p.getStatus() != PaymentStatus.REFUNDED))
            throw new IllegalStateException("This booking already has an active payment. Update its status instead.");
        // Optional client amount is a stale-price check, never the source of the charge.
        BigDecimal amount = booking.amountDue();
        if (request.amount() != null && request.amount().compareTo(amount) != 0)
            throw new IllegalArgumentException("Booking amount has changed. Reload the booking before recording payment.");
        String id = "P-" + UUID.randomUUID();
        Payment p;
        if (request.method() == PaymentMethod.CASH) {
            if ((request.cardBrand() != null && !request.cardBrand().isEmpty())
                    || (request.lastFour() != null && !request.lastFour().isEmpty()))
                throw new IllegalArgumentException("Cash payments cannot contain card details");
            p = new CashPayment(id, request.bookingId(), amount, request.status(),
                    request.paymentDate(), clock.instant(), cashDiscountRate, booking.billing());
        } else {
            p = new CardPayment(id, request.bookingId(), amount, request.status(),
                    request.paymentDate(), clock.instant(), request.cardBrand(), request.lastFour(), booking.billing());
        }
        payments.add(p);
        repository.saveAll(payments);
        return view(p, bookingIndex());
    }

    public synchronized List<PaymentResponse> list(String q, PaymentMethod method, PaymentStatus status) {
        Map<String, BookingInfo> index = bookingIndex();
        String search = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        return repository.findAll().stream()
            .filter(p -> method == null || p.getMethod() == method)
            .filter(p -> status == null || p.getStatus() == status)
            .sorted(Comparator.comparing(Payment::getCreatedAt).reversed().thenComparing(Payment::getId))
            .map(p -> view(p, index))
            .filter(p -> (p.id() + " " + p.bookingId() + " " + p.guestName()).toLowerCase(Locale.ROOT).contains(search))
            .toList();
    }

    public synchronized PaymentResponse get(String id) {
        return view(find(repository.findAll(), id), bookingIndex());
    }

    public synchronized PaymentResponse updateStatus(String id, PaymentStatus next) {
        List<Payment> payments = repository.findAll();
        Payment p = find(payments, id);
        if (p.getStatus() == PaymentStatus.PENDING && next == PaymentStatus.PAID)
            bookings.requirePayable(p.getBookingId());
        p.changeStatus(next);
        repository.saveAll(payments);
        return view(p, bookingIndex());
    }

    public synchronized void delete(String id) {
        List<Payment> payments = repository.findAll();
        payments.remove(find(payments, id));
        repository.saveAll(payments); // Hard-delete: the actual line is removed from payments.txt.
    }

    public synchronized Map<String, Object> invoice(String id) {
        PaymentResponse p = get(id);
        String type = switch (p.status()) {
            case PENDING -> "INVOICE"; case PAID -> "RECEIPT"; case REFUNDED -> "REFUND_RECORD";
        };
        String note = switch (p.status()) {
            case PENDING -> "Payment is pending. This document is not proof of payment.";
            case PAID -> "Staff recorded payment received at the hotel desk.";
            case REFUNDED -> "Staff recorded a refund. This application does not transfer money.";
        };
        return Map.of("documentNumber", "INV-" + p.id().substring(2),
                "documentType", type, "payment", p, "note", note);
    }

    public synchronized Map<String, Object> summary() {
        List<Payment> payments = repository.findAll();
        Map<String, BookingInfo> index = bookingIndex();
        List<Payment> paid = payments.stream().filter(p -> p.getStatus() == PaymentStatus.PAID).toList();
        List<Payment> refunded = payments.stream().filter(p -> p.getStatus() == PaymentStatus.REFUNDED).toList();
        Set<String> paidBookingIds = new HashSet<>();
        paid.forEach(p -> paidBookingIds.add(p.getBookingId()));
        List<BookingInfo> awaiting = index.values().stream()
                .filter(b -> !b.cancelled() && !paidBookingIds.contains(b.id())).toList();
        List<PaymentResponse> refundNeeded = paid.stream()
                .filter(p -> index.containsKey(p.getBookingId()) && index.get(p.getBookingId()).cancelled())
                .map(p -> view(p, index)).toList();
        return Map.of("collectedAmount", sum(paid), "paidCount", paid.size(),
                "awaitingCount", awaiting.size(),
                "awaitingEstimate", awaiting.stream().map(BookingInfo::amountDue).reduce(BigDecimal.ZERO, BigDecimal::add),
                "refundedAmount", sum(refunded), "refundedCount", refunded.size(), "refundNeeded", refundNeeded);
    }

    public synchronized List<BookingInfo> availableBookings() {
        Set<String> occupied = new HashSet<>();
        repository.findAll().stream().filter(p -> p.getStatus() != PaymentStatus.REFUNDED)
                .forEach(p -> occupied.add(p.getBookingId()));
        return bookings.findAll().stream().filter(b -> !b.cancelled() && !occupied.contains(b.id())).toList();
    }

    /** The booking component can use this result instead of storing a second, stale paid flag. */
    public synchronized Map<String, Object> bookingPaymentState(String bookingId) {
        bookings.require(bookingId);
        List<PaymentResponse> records = list(null, null, null).stream()
                .filter(p -> p.bookingId().equals(bookingId)).toList();
        boolean paid = records.stream().anyMatch(p -> p.status() == PaymentStatus.PAID);
        return Map.of("bookingId", bookingId, "paid", paid, "payments", records);
    }

    public Map<String, Object> settings() {
        return Map.of("cashDiscountRate", cashDiscountRate, "currency", "LKR");
    }

    private BigDecimal sum(List<Payment> payments) {
        return payments.stream().map(Payment::calculateTotal).reduce(new BigDecimal("0.00"), BigDecimal::add);
    }
    private Payment find(List<Payment> payments, String id) {
        return payments.stream().filter(p -> p.getId().equals(id)).findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Payment not found: " + id));
    }
    private Map<String, BookingInfo> bookingIndex() {
        Map<String, BookingInfo> index = new HashMap<>();
        bookings.findAll().forEach(b -> index.put(b.id(), b));
        return index;
    }
    private PaymentResponse view(Payment p, Map<String, BookingInfo> index) {
        BookingInfo b = index.get(p.getBookingId());
        return PaymentResponse.from(p, p.getBilling() != null ? p.getBilling().guestName() : b == null ? "Booking unavailable" : b.guestName(),
                b == null ? "UNKNOWN" : b.status());
    }
}
