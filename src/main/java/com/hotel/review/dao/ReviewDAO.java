package com.hotel.review.dao;

import com.hotel.review.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ReviewDAO {
    private final JdbcTemplate jdbc;

    public ReviewDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        String id = rs.getString("id"), guest = rs.getString("guest_id");
        String booking = rs.getString("booking_id"), name = rs.getString("guest_name");
        int rating = rs.getInt("rating");
        String text = rs.getString("review_text");
        return rs.getBoolean("verified")
                ? new VerifiedReview(id, guest, booking, rating, text, name)
                : new PublicReview(id, guest, rating, text, name);
    }

    private static final String BASE = "SELECT r.id, r.guest_id, r.booking_id, r.rating, r.verified, "
            + "r.review_text, g.name AS guest_name FROM reviews r JOIN guests g ON r.guest_id = g.id ";

    public List<Review> findAll() {
        return jdbc.query(
                BASE + "ORDER BY r.created_at DESC, r.id DESC",
                this::mapRow
        );
    }

    public List<Review> findByGuest(String guestId) {
        return jdbc.query(BASE + "WHERE r.guest_id=? ORDER BY r.id DESC", this::mapRow, guestId);
    }

    public Review findById(String id) {
        List<Review> reviews = jdbc.query(BASE + "WHERE r.id=?", this::mapRow, id);
        return reviews.isEmpty() ? null : reviews.get(0);
    }

    public boolean hasReviewForBooking(String bookingId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM reviews WHERE booking_id=?", Integer.class, bookingId);
        return count != null && count > 0;
    }

    public boolean completedBookingBelongsToGuest(
            String bookingId, String guestId) {

        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM bookings " +
                        "WHERE id=? AND guest_id=? " +
                        "AND check_out <= CURRENT_DATE() " +
                        "AND LOWER(TRIM(status)) = 'active'",
                Integer.class, bookingId, guestId
        );

        return count != null && count > 0;
    }

    public void insert(Review review) {
        jdbc.update("INSERT INTO reviews(id, guest_id, booking_id, rating, verified, review_text) "
                        + "VALUES (?,?,?,?,?,?)", review.getId(), review.getGuestId(), review.getBookingId(),
                review.getRating(), review.isVerified(), review.getText());
    }

    public int update(String id, int rating, String text) {
        return jdbc.update("UPDATE reviews SET rating=?, review_text=? WHERE id=?", rating, text, id);
    }

    public int delete(String id) {
        return jdbc.update("DELETE FROM reviews WHERE id=?", id);
    }
}
