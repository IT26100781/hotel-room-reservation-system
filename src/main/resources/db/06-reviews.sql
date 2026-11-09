-- Owner: Feedback / Review component
-- Add: CREATE TABLE IF NOT EXISTS reviews ( ... );
-- Agreed fields: id | bookingId | guestId | rating | verified | text
-- booking_id references bookings(id), guest_id references guests(id).

-- Placeholder so this file is not empty (Spring refuses empty scripts). Replace it with the CREATE TABLE.
SELECT 1;
-- Review module; run after guests and bookings table setup.
CREATE TABLE IF NOT EXISTS reviews (id VARCHAR(24) NOT NULL PRIMARY KEY,
    guest_id VARCHAR(10) NOT NULL,
    booking_id VARCHAR(20) NULL,
    rating TINYINT NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    review_text VARCHAR(500) NOT NULL,
    CONSTRAINT fk_review_guest FOREIGN KEY (guest_id) REFERENCES guests(id),
    CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT uk_review_booking UNIQUE (booking_id)
    );
-- Booking FK can be added when the reservation team finishes the bookings table.
