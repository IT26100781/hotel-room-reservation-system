package com.hotel.payment.dao;

import com.hotel.payment.model.BookingBill;
import com.hotel.room.model.Room;
import com.hotel.user.guest.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Narrow bridge to the shared booking schema while the reservation component is unfinished.
 */
@Repository
public class BookingBillingAdapter {
    private final JdbcTemplate jdbc;

    public BookingBillingAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String SELECT = """
            SELECT b.*, g.name guest_name, g.email guest_email, g.guest_type,
                   r.room_name, r.room_number, r.room_type, r.base_rate, r.capacity, r.room_size,
                   r.image_url, r.status room_status
            FROM bookings b JOIN guests g ON g.id=b.guest_id JOIN rooms r ON r.id=b.room_id
            """;

    private BookingBill map(java.sql.ResultSet rs, int row) throws java.sql.SQLException {
        Guest guest = "VIP".equals(rs.getString("guest_type"))
                ? new VipGuest(rs.getString("guest_id"), rs.getString("guest_name"), rs.getString("guest_email"), "")
                : new RegularGuest(rs.getString("guest_id"), rs.getString("guest_name"), rs.getString("guest_email"), "");
        Room room = Room.create(rs.getString("room_type"), rs.getString("room_id"), rs.getString("room_name"),
                rs.getInt("room_number"), rs.getDouble("base_rate"), rs.getInt("capacity"), rs.getInt("room_size"), rs.getString("image_url"), rs.getString("room_status"));
        return new BookingBill(rs.getString("id"), rs.getString("status"), rs.getDate("check_in").toLocalDate(),
                rs.getDate("check_out").toLocalDate(), rs.getBoolean("checked_in"), guest, room);
    }

    public BookingBill find(String id) {
        return jdbc.query(SELECT + " WHERE b.id=?", this::map, id).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("Booking not found."));
    }

    public BookingBill findLocked(String id) {
        return jdbc.query(SELECT + " WHERE b.id=? FOR UPDATE", this::map, id).stream().findFirst().orElseThrow(() -> new IllegalArgumentException("Booking not found."));
    }

    public void lock(String id) {
        if (jdbc.queryForList("SELECT id FROM bookings WHERE id=? FOR UPDATE", String.class, id).isEmpty())
            throw new IllegalArgumentException("Booking not found.");
    }

    public List<BookingBill> unpaid() {
        return jdbc.query(SELECT + " WHERE b.status='active' AND b.payment_status='pending' AND NOT EXISTS (SELECT 1 FROM payments p WHERE p.booking_id=b.id) ORDER BY b.check_in,b.id", this::map);
    }

    public void updatePaymentStatus(String id, String status) {
        jdbc.update("UPDATE bookings SET payment_status=? WHERE id=?", status, id);
    }
}
