package com.hotel.review.model;

public final class PublicReview extends Review {
    public PublicReview(String id, String guestId, int rating, String text, String guestName) {
        super(id, guestId, null, rating, text, guestName);
    }
    @Override public boolean isVerified() { return false; }
}
