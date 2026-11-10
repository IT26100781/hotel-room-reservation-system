package com.hotel.review.model;

public final class VerifiedReview extends Review {
    public VerifiedReview(String id, String guestId, String bookingId, int rating, String text, String guestName) {
        super(id, guestId, bookingId, rating, text, guestName);
    }

    @Override
    public boolean isVerified() {
        return true;
    }
}
