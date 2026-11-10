package com.hotel.payment.dao;

import com.hotel.payment.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentDAO {
    private final JdbcTemplate jdbc;

    public PaymentDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private Payment map(java.sql.ResultSet r, int row) throws java.sql.SQLException {
        if ("cash".equals(r.getString("method")))
            return new CashPayment(r.getString("id"), r.getString("booking_id"), r.getBigDecimal("nightly_rate"), r.getBigDecimal("room_total"), r.getBigDecimal("guest_rate"), r.getBigDecimal("amount"), PaymentStatus.parse(r.getString("status")), r.getDate("paid_on").toLocalDate());
        return new CardPayment(r.getString("id"), r.getString("booking_id"), r.getBigDecimal("nightly_rate"), r.getBigDecimal("room_total"), r.getBigDecimal("guest_rate"), r.getBigDecimal("amount"), PaymentStatus.parse(r.getString("status")), r.getDate("paid_on").toLocalDate(), r.getString("card_type"), r.getString("card_last4"));
    }

    public List<Payment> findAll() {
        return jdbc.query("SELECT * FROM payments ORDER BY paid_on DESC,id DESC", this::map);
    }

    public Payment find(String id) {
        return jdbc.query("SELECT * FROM payments WHERE id=?", this::map, id).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("Payment not found."));
    }

    public Payment findLocked(String id) {
        return jdbc.query("SELECT * FROM payments WHERE id=? FOR UPDATE", this::map, id).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("Payment not found."));
    }

    public Payment forBooking(String id) {
        return jdbc.query("SELECT * FROM payments WHERE booking_id=?", this::map, id).stream().findFirst().orElse(null);
    }

    public String nextId() {
        jdbc.queryForObject("SELECT next_value FROM payment_sequence WHERE singleton=1 FOR UPDATE", Long.class);
        jdbc.update("UPDATE payment_sequence SET next_value=GREATEST(next_value,(SELECT COALESCE(MAX(CAST(SUBSTRING(id,2) AS UNSIGNED)),0) FROM payments))+1 WHERE singleton=1");
        return "P" + jdbc.queryForObject("SELECT next_value FROM payment_sequence WHERE singleton=1", Long.class);
    }

    public void save(Payment p) {
        jdbc.update("INSERT INTO payments (id,booking_id,nightly_rate,room_total,guest_rate,amount,method,card_type,card_last4,status,paid_on) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                p.getId(), p.getBookingId(), p.getNightlyRate(), p.getRoomTotal(), p.getGuestRate(), p.getAmount(), p.getMethod(), p.getCardType(), p.getCardLast4(), p.getStatus().getValue(), p.getPaidOn());
    }

    public void status(String id, PaymentStatus status) {
        jdbc.update("UPDATE payments SET status=? WHERE id=?", status.getValue(), id);
    }

    public void delete(String id) {
        jdbc.update("DELETE FROM payments WHERE id=?", id);
    }
}
