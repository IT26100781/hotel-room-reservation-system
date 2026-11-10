
-- Owner: Review Management Module
-- Run after creating guests and bookings tables.

CREATE TABLE IF NOT EXISTS reviews (
    id VARCHAR(24) NOT NULL PRIMARY KEY,

    guest_id VARCHAR(10) NOT NULL,

    booking_id VARCHAR(20) NULL,

    rating TINYINT NOT NULL,

    verified BOOLEAN NOT NULL DEFAULT FALSE,

    review_text VARCHAR(500) NOT NULL,

    created_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_review_guest
        FOREIGN KEY (guest_id)
        REFERENCES guests(id),

    CONSTRAINT chk_review_rating
        CHECK (rating BETWEEN 1 AND 5),

    CONSTRAINT uk_review_booking
        UNIQUE (booking_id)
);
