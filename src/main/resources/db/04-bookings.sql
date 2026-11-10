-- Owner: Reservation component
-- One table for both booking types; booking_type decides which subclass is created
-- (OnlineReservation / DeskReservation). Bookings are cancelled with status = 'cancelled',
-- never deleted, because payments and reviews point at them.
-- "Completed" and "no-show" are not stored: Reservation works them out from checked_in and the dates.

CREATE TABLE IF NOT EXISTS bookings (
    id             VARCHAR(10) NOT NULL,
    guest_id       VARCHAR(10) NOT NULL,
    room_id        VARCHAR(10) NOT NULL,
    check_in       DATE        NOT NULL,
    check_out      DATE        NOT NULL,
    guests         INT         NOT NULL,
    booking_type   VARCHAR(10) NOT NULL,
    status         VARCHAR(15) NOT NULL DEFAULT 'active',
    payment_status VARCHAR(15) NOT NULL DEFAULT 'pending',
    checked_in     BOOLEAN     NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    KEY idx_bookings_room (room_id),
    KEY idx_bookings_guest (guest_id),
    CONSTRAINT fk_bookings_guest FOREIGN KEY (guest_id) REFERENCES guests (id),
    CONSTRAINT fk_bookings_room  FOREIGN KEY (room_id)  REFERENCES rooms (id),
    CONSTRAINT chk_bookings_type    CHECK (booking_type IN ('ONLINE', 'DESK')),
    CONSTRAINT chk_bookings_status  CHECK (status IN ('active', 'cancelled')),
    CONSTRAINT chk_bookings_payment CHECK (payment_status IN ('pending', 'paid', 'refunded')),
    CONSTRAINT chk_bookings_dates   CHECK (check_out > check_in),
    CONSTRAINT chk_bookings_guests  CHECK (guests >= 1)
);
