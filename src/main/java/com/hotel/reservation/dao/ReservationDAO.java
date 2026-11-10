package com.hotel.reservation.dao;

import com.hotel.reservation.model.BookingStatus;
import com.hotel.reservation.model.PaymentStatus;
import com.hotel.reservation.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

/**
 * Spring Repository for bookings table using Spring's JdbcTemplate.
 * Matches the team's RoomDAO / StaffDAO convention.
 */
@Repository
public class ReservationDAO {

    private final JdbcTemplate jdbcTemplate;

    public ReservationDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Reservation.Builder()
                .id(rs.getString("id"))
                .guestId(rs.getString("guest_id"))
                .roomId(rs.getString("room_id"))
                .dateRange(rs.getDate("check_in").toLocalDate(), rs.getDate("check_out").toLocalDate())
                .guests(rs.getInt("guests"))
                .totalAmount(rs.getDouble("total_amount"))
                .status(BookingStatus.fromString(rs.getString("status")))
                .paymentStatus(PaymentStatus.fromString(rs.getString("payment_status")))
                .checkedIn(rs.getBoolean("checked_in"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }

    /**
     * Checks if a room has an active booking that overlaps with the proposed dates.
     * Overlap formula: (existing.check_in < proposed.check_out) AND (existing.check_out > proposed.check_in)
     */
    public boolean hasOverlap(String roomId, LocalDate checkIn, LocalDate checkOut) {
        String sql = "SELECT COUNT(*) FROM bookings "
                   + "WHERE room_id = ? "
                   + "  AND status = 'active' "
                   + "  AND check_in < ? "
                   + "  AND check_out > ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, roomId, Date.valueOf(checkOut), Date.valueOf(checkIn));
        return count != null && count > 0;
    }

    public boolean save(Reservation b) {
        String sql = "INSERT INTO bookings (id, guest_id, room_id, check_in, check_out, guests, total_amount, "
                   + "status, payment_status, checked_in, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int rows = jdbcTemplate.update(sql,
                b.getId(),
                b.getGuestId(),
                b.getRoomId(),
                Date.valueOf(b.getCheckIn()),
                Date.valueOf(b.getCheckOut()),
                b.getGuests(),
                b.getTotalAmount(),
                b.getStatus().getValue(),
                b.getPaymentStatus().getValue(),
                b.isCheckedIn(),
                Timestamp.valueOf(b.getCreatedAt())
        );
        return rows == 1;
    }

    public Reservation findById(String id) {
        if (id == null || id.trim().isEmpty()) return null;
        String sql = "SELECT id, guest_id, room_id, check_in, check_out, guests, total_amount, "
                   + "status, payment_status, checked_in, created_at FROM bookings WHERE id = ?";
        List<Reservation> results = jdbcTemplate.query(sql, this::mapRow, id.trim());
        return results.isEmpty() ? null : results.get(0);
    }

    public List<Reservation> findByGuestId(String guestId) {
        String sql = "SELECT id, guest_id, room_id, check_in, check_out, guests, total_amount, "
                   + "status, payment_status, checked_in, created_at FROM bookings WHERE guest_id = ? ORDER BY check_in DESC";
        return jdbcTemplate.query(sql, this::mapRow, guestId);
    }

    public List<Reservation> findAll() {
        String sql = "SELECT id, guest_id, room_id, check_in, check_out, guests, total_amount, "
                   + "status, payment_status, checked_in, created_at FROM bookings ORDER BY check_in DESC";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public boolean updateStatus(String id, BookingStatus status) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status.getValue(), id) == 1;
    }

    public boolean updatePaymentStatus(String id, PaymentStatus status) {
        String sql = "UPDATE bookings SET payment_status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status.getValue(), id) == 1;
    }

    public boolean updateCheckedIn(String id, boolean checkedIn) {
        String sql = "UPDATE bookings SET checked_in = ? WHERE id = ?";
        return jdbcTemplate.update(sql, checkedIn, id) == 1;
    }
}