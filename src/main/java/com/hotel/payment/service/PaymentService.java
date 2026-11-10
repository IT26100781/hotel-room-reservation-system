package com.hotel.payment.service;

import com.hotel.payment.dao.*;
import com.hotel.payment.model.*;
import com.hotel.user.User;
import com.hotel.user.admin.model.Staff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class PaymentService {
    private final PaymentDAO payments;
    private final BookingBillingAdapter bookings;

    public PaymentService(PaymentDAO payments, BookingBillingAdapter bookings) {
        this.payments = payments;
        this.bookings = bookings;
    }

    public void authorize(User user) {
        if (!(user instanceof Staff staff) || !staff.isActive() || !user.canRecordPayment())
            throw new SecurityException("Payments are available only to staff.");
    }

    public List<BookingBill> unpaid(User user) {
        authorize(user);
        return bookings.unpaid();
    }

    public List<PaymentDetails> list(User user) {
        authorize(user);
        return payments.findAll().stream().map(p -> new PaymentDetails(p, bookings.find(p.getBookingId()))).toList();
    }

    public PaymentDetails invoice(User user, String id, String bookingId) {
        authorize(user);
        Payment p = id != null && !id.isBlank() ? payments.find(id) : payments.forBooking(bookingId);
        if (p == null) throw new IllegalArgumentException("Payment not found.");
        return new PaymentDetails(p, bookings.find(p.getBookingId()));
    }

    @Transactional
    public String record(User user, String bookingId, String method, String status, String date,
                         String holder, String number, String expiry, String cvv) {
        authorize(user);
        PaymentStatus state = PaymentStatus.parse(status);
        if (state == PaymentStatus.REFUNDED)
            throw new IllegalArgumentException("A new payment must be pending or paid.");
        LocalDate paidOn;
        try {
            paidOn = LocalDate.parse(date);
        } catch (Exception e) {
            throw new IllegalArgumentException("Enter a valid transaction date.");
        }
        if (paidOn.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Transaction date cannot be in the future.");
        bookings.lock(bookingId);
        BookingBill b = bookings.findLocked(bookingId);
        if (!"active".equals(b.getStatus()))
            throw new IllegalArgumentException("Cancelled bookings cannot take payment.");
        if (payments.forBooking(bookingId) != null)
            throw new IllegalArgumentException("This booking already has a payment. Open its invoice or update its status.");
        Payment p;
        if ("cash".equals(method))
            p = new CashPayment(payments.nextId(), bookingId, b.getNightlyRate(), b.getRoomTotal(), b.getGuestRate(), null, state, paidOn);
        else if ("card".equals(method)) {
            CardSimulator.Result card = new CardSimulator().validate(holder, number, expiry, cvv, state == PaymentStatus.PAID);
            p = new CardPayment(payments.nextId(), bookingId, b.getNightlyRate(), b.getRoomTotal(), b.getGuestRate(), null, state, paidOn, card.type(), card.last4());
        } else throw new IllegalArgumentException("Choose cash or card.");
        if (p.getAmount().signum() <= 0 || p.getAmount().compareTo(new BigDecimal("99999999.99")) > 0)
            throw new IllegalArgumentException("Booking total is outside the supported payment range.");
        payments.save(p);
        bookings.updatePaymentStatus(bookingId, state.getValue());
        return p.getId();
    }

    @Transactional
    public void change(User user, String id, String action) {
        authorize(user);
        Payment initial = payments.find(id);
        bookings.lock(initial.getBookingId());
        Payment p = payments.findLocked(id);
        BookingBill b = bookings.findLocked(p.getBookingId());
        if ("delete".equals(action)) {
            payments.delete(id);
            bookings.updatePaymentStatus(p.getBookingId(), "pending");
            return;
        }
        PaymentStatus next;
        if ("paid".equals(action) && p.isPending()) {
            if (!"active".equals(b.getStatus()))
                throw new IllegalArgumentException("A cancelled booking cannot be marked paid.");
            if ("0002".equals(p.getCardLast4()))
                throw new IllegalArgumentException("Simulation: this test card is declined. Delete the pending record and try a successful test card.");
            next = PaymentStatus.PAID;
        } else if ("refund".equals(action) && p.getStatus() == PaymentStatus.PAID) next = PaymentStatus.REFUNDED;
        else throw new IllegalArgumentException("Invalid status change. Only Pending → Paid → Refunded is allowed.");
        payments.status(id, next);
        bookings.updatePaymentStatus(p.getBookingId(), next.getValue());
    }

    public Map<String, Object> metrics(User user, List<PaymentDetails> list) {
        authorize(user);
        BigDecimal collected = BigDecimal.ZERO, refunded = BigDecimal.ZERO, estimate = BigDecimal.ZERO;
        long paid = 0, returned = 0, pending = 0;
        for (PaymentDetails d : list) {
            Payment p = d.getPayment();
            if (p.isPending()) {
                pending++;
                estimate = estimate.add(p.getAmount());
            } else if (p.isRefunded()) {
                returned++;
                refunded = refunded.add(p.getAmount());
            } else {
                paid++;
                collected = collected.add(p.getAmount());
            }
        }
        for (BookingBill b : unpaid(user)) {
            pending++;
            estimate = estimate.add(b.getRoomTotal().multiply(BigDecimal.ONE.subtract(b.getGuestRate())));
        }
        return Map.of("collected", collected, "paidCount", paid, "refunded", refunded, "refundedCount", returned, "awaitingCount", pending, "awaitingEstimate", estimate);
    }
}
