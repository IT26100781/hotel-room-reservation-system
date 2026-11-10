package com.hotel.review.service;

import com.hotel.review.dao.ReviewDAO;
import com.hotel.review.model.*;
import com.hotel.user.User;
import com.hotel.user.guest.model.Guest;
import com.hotel.user.guest.dao.GuestDAO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {
    private final ReviewDAO dao;
    private final GuestDAO guestDAO;

    public ReviewService(ReviewDAO dao, GuestDAO guestDAO) {
        this.dao = dao;
        this.guestDAO = guestDAO;
    }

    public List<Review> allReviews() {
        return dao.findAll();
    }

    public List<Review> myReviews(User actor) {
        return dao.findByGuest(requireGuest(actor).getId());
    }

    public Review find(String id) {
        return dao.findById(id);
    }

    public Guest requireActiveGuest(User actor) {
        return requireGuest(actor);
    }

    public void create(User actor, String bookingId, int rating, String text) {
        Guest guest = requireGuest(actor);
        validate(rating, text);
        String cleaned = clean(text);
        String id = "RV" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
        Review review;
        if (bookingId != null && !bookingId.isBlank()) {
            String booking = bookingId.trim();
            if (!dao.completedBookingBelongsToGuest(booking, guest.getId()))
                throw new IllegalArgumentException("This booking is not a completed stay belonging to you.");
            if (dao.hasReviewForBooking(booking))
                throw new IllegalArgumentException("This booking already has a review.");
            review = new VerifiedReview(id, guest.getId(), booking, rating, cleaned, guest.getName());
        } else {
            review = new PublicReview(id, guest.getId(), rating, cleaned, guest.getName());
        }
        dao.insert(review);
    }

    public void update(User actor, String id, int rating, String text) {
        Guest guest = requireGuest(actor);
        validate(rating, text);
        Review existing = dao.findById(id);
        if (existing == null) throw new IllegalArgumentException("Review not found.");
        if (!existing.getGuestId().equals(guest.getId())) throw new SecurityException("Not your review.");
        dao.update(id, rating, clean(text));
    }

    public void delete(User actor, String id) {
        if (actor == null) throw new SecurityException("Sign in first.");
        Review existing = dao.findById(id);
        if (existing == null) throw new IllegalArgumentException("Review not found.");
        if (!actor.canModerateReviews()) {
            Guest guest = requireGuest(actor);
            if (!existing.getGuestId().equals(guest.getId()))
                throw new SecurityException("You cannot delete this review.");
        }
        dao.delete(id);
    }

    private Guest requireGuest(User actor) {
        if (!(actor instanceof Guest) || !actor.canAddReviews())
            throw new SecurityException("An active guest account is required.");
        // Always read CURRENT account status from DB; session Guest can be stale.
        Guest current = guestDAO.findById(actor.getId());
        if (current == null || !current.isActive())
            throw new SecurityException("Your guest account is deactivated.");
        return current;
    }

    private void validate(int rating, String text) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1–5 stars.");
        if (text == null || text.strip().isEmpty() || text.strip().length() > 500)
            throw new IllegalArgumentException("Review text must be between 1 and 500 characters.");
    }

    private String clean(String text) {
        return text.strip().replace('|', ' ');
    }
}
