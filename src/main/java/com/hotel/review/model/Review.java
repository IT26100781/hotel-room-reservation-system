package com.hotel.review.model;

/**
 * Shared base type: a public review or a stay-verified review.
 */
public abstract class Review {
    private final String id;
    private final String guestId;
    private final String bookingId;
    private final int rating;
    private final String text;
    private final String guestName;

    protected Review(String id, String guestId, String bookingId, int rating, String text, String guestName) {
        this.id = id;
        this.guestId = guestId;
        this.bookingId = bookingId;
        this.rating = rating;
        this.text = text;
        this.guestName = guestName;
    }

    public String getId() {
        return id;
    }

    public String getGuestId() {
        return guestId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public int getRating() {
        return rating;
    }

    public String getText() {
        return text;
    }

    public String getGuestName() {
        return guestName;
    }

    public abstract boolean isVerified();

    public String getKind() {
        return isVerified() ? "verified" : "public";
    }
}


