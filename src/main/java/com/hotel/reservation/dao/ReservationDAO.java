// src/main/java/com/hotel/reservation/dao/ReservationDAO.java
package com.hotel.reservation.dao;

import com.hotel.reservation.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Data-access object for the {@code bookings} table.
 *
 * Uses Spring's {@link JdbcTemplate} with {@link java.sql.PreparedStatement}
 * parameters to prevent SQL injection. Every query maps rows through
 * {@link #mapRow} which calls {@link Reservation#create} to build the
 * correct subclass (OnlineReservation / DeskReservation) from the
 * {@code booking_type} discriminator column.
 */
@Repository
public class ReservationDAO {

    private final JdbcTemplate jdbc;

    /** Column list matching the bookings table (no total_amount, no created_at). */
    private static final String COLUMNS =
            "id, guest_id, room_id, booking_type, check_in, check_out, guests, " +
            "status, payment_status, checked_in";

    public ReservationDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ---- row mapper ----

    /**
     * Maps a {@link ResultSet} row to a {@link Reservation} subclass
     * using the factory method.
     */
    private Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Reservation.create(
                rs.getString("id"),
                rs.getString("guest_id"),
                rs.getString("room_id"),
                rs.getString("booking_type"),
                rs.getDate("check_in").toLocalDate(),
                rs.getDate("check_out").toLocalDate(),
                rs.getInt("guests"),
                rs.getString("status"),
                rs.getString("payment_status"),
                rs.getBoolean("checked_in")
        );
    }

    // ---- CRUD ----

    /**
     * Insert a new booking row.
     *
     * @return true if one row was inserted
     */
    public boolean save(Reservation r) {
        String sql = "INSERT INTO bookings (" + COLUMNS + ") VALUES (?,?,?,?,?,?,?,?,?,?)";
        int rows = jdbc.update(sql,
                r.getId(),
                r.getGuestId(),
                r.getRoomId(),
                r.getBookingType().getValue(),
                r.getCheckIn(),
                r.getCheckOut(),
                r.getGuests(),
                r.getStatus().getValue(),
                r.getPaymentStatus().getValue(),
                r.isCheckedIn()
        );
        return rows == 1;
    }

    /**
     * Find a single booking by its ID (e.g. "BK01").
     *
     * @return the Reservation, or null if not found
     */
    public Reservation findById(String id) {
        String sql = "SELECT " + COLUMNS + " FROM bookings WHERE id = ?";
        List<Reservation> results = jdbc.query(sql, this::mapRow, id);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * All bookings for a specific guest, newest check-in first.
     */
    public List<Reservation> findByGuestId(String guestId) {
        String sql = "SELECT " + COLUMNS + " FROM bookings WHERE guest_id = ? ORDER BY check_in DESC";
        return jdbc.query(sql, this::mapRow, guestId);
    }

    /**
     * All bookings in the system, newest check-in first (for the staff manage page).
     */
    public List<Reservation> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM bookings ORDER BY check_in DESC, id DESC";
        return jdbc.query(sql, this::mapRow);
    }

    // ---- overlap detection ----

    /**
     * Checks whether a room has any active booking that overlaps with the given date range.
     * Uses the standard overlap formula: A.check_in < B.check_out AND A.check_out > B.check_in.
     *
     * @param excludeId booking ID to exclude (for date-change operations), or null
     * @return true if there is a conflicting booking
     */
    public boolean hasOverlap(String roomId, LocalDate checkIn, LocalDate checkOut, String excludeId) {
        String sql = "SELECT COUNT(*) FROM bookings " +
                     "WHERE room_id = ? AND status = 'active' " +
                     "AND check_in < ? AND check_out > ?";
        if (excludeId != null) {
            sql += " AND id != ?";
            Integer count = jdbc.queryForObject(sql, Integer.class, roomId, checkOut, checkIn, excludeId);
            return count != null && count > 0;
        }
        Integer count = jdbc.queryForObject(sql, Integer.class, roomId, checkOut, checkIn);
        return count != null && count > 0;
    }

    /** Convenience overload without excludeId. */
    public boolean hasOverlap(String roomId, LocalDate checkIn, LocalDate checkOut) {
        return hasOverlap(roomId, checkIn, checkOut, null);
    }

    // ---- status updates ----

    /** Update the payment_status column. */
    public boolean updatePaymentStatus(String id, String paymentStatus) {
        int rows = jdbc.update("UPDATE bookings SET payment_status = ? WHERE id = ?",
                paymentStatus, id);
        return rows == 1;
    }

    /** Cancel a booking (set status to 'cancelled'). */
    public boolean cancel(String id) {
        int rows = jdbc.update("UPDATE bookings SET status = 'cancelled' WHERE id = ?", id);
        return rows == 1;
    }

    /** Mark a guest as checked in. */
    public boolean checkIn(String id) {
        int rows = jdbc.update("UPDATE bookings SET checked_in = TRUE WHERE id = ?", id);
        return rows == 1;
    }

    /** Update the check-in and check-out dates. */
    public boolean updateDates(String id, LocalDate checkIn, LocalDate checkOut) {
        int rows = jdbc.update("UPDATE bookings SET check_in = ?, check_out = ? WHERE id = ?",
                checkIn, checkOut, id);
        return rows == 1;
    }

    // ---- ID generation helper ----

    /**
     * Returns the largest numeric suffix among all booking IDs with the "BK" prefix.
     * Used by the service to generate the next sequential ID.
     */
    public int findMaxIdNumber() {
        Integer max = jdbc.queryForObject(
                "SELECT COALESCE(MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)), 0) FROM bookings WHERE id LIKE 'BK%'",
                Integer.class);
        return max != null ? max : 0;
    }

    // ---- queries for other modules ----

    /**
     * Active bookings with pending payment (used by Payment module via service).
     */
    public List<Reservation> findPendingPayment() {
        String sql = "SELECT " + COLUMNS + " FROM bookings " +
                     "WHERE status = 'active' AND payment_status = 'pending' " +
                     "ORDER BY check_in, id";
        return jdbc.query(sql, this::mapRow);
    }

    /**
     * Check if a guest has a completed stay for a specific booking (for Review module).
     */
    public boolean hasCompletedStay(String guestId, String bookingId) {
        String sql = "SELECT COUNT(*) FROM bookings " +
                     "WHERE id = ? AND guest_id = ? AND status = 'active' " +
                     "AND checked_in = TRUE AND check_out <= CURDATE()";
        Integer count = jdbc.queryForObject(sql, Integer.class, bookingId, guestId);
        return count != null && count > 0;
    }

    /**
     * Find all rooms that have NO overlapping active booking for the given dates.
     * Returns room IDs that are free.
     */
    public List<String> findFreeRoomIds(LocalDate checkIn, LocalDate checkOut) {
        String sql = "SELECT r.id FROM rooms r " +
                     "WHERE r.status = 'ACTIVE' " +
                     "AND NOT EXISTS (" +
                     "  SELECT 1 FROM bookings b " +
                     "  WHERE b.room_id = r.id AND b.status = 'active' " +
                     "  AND b.check_in < ? AND b.check_out > ?" +
                     ")";
        return jdbc.queryForList(sql, String.class, checkOut, checkIn);
    }
}
