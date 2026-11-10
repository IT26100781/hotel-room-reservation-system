-- Owner: Reservation component
-- Creates the bookings table and initial seed data for the reservation module.

CREATE TABLE IF NOT EXISTS bookings (
    id             VARCHAR(10)    NOT NULL,
    guest_id       VARCHAR(10)    NOT NULL,
    room_id        VARCHAR(10)    NOT NULL,
    check_in       DATE           NOT NULL,
    check_out      DATE           NOT NULL,
    guests         INT            NOT NULL,
    total_amount   DECIMAL(10,2)  NOT NULL,
    status         VARCHAR(20)    NOT NULL DEFAULT 'active',
    payment_status VARCHAR(20)    NOT NULL DEFAULT 'pending',
    checked_in     BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_bookings_guest FOREIGN KEY (guest_id) REFERENCES guests(id),
    CONSTRAINT fk_bookings_room  FOREIGN KEY (room_id)  REFERENCES rooms(id),
    CONSTRAINT chk_bookings_dates   CHECK (check_out > check_in),
    CONSTRAINT chk_bookings_guests  CHECK (guests >= 1),
    CONSTRAINT chk_bookings_amount  CHECK (total_amount >= 0.00),
    CONSTRAINT chk_bookings_status  CHECK (status IN ('active', 'cancelled')),
    CONSTRAINT chk_bookings_payment CHECK (payment_status IN ('pending', 'paid', 'refunded'))
);

CREATE INDEX idx_bookings_room_dates ON bookings (room_id, check_in, check_out, status);
CREATE INDEX idx_bookings_guest ON bookings (guest_id);

-- Initial seed bookings matching the UI prototypes (if rooms & guests exist)
INSERT IGNORE INTO bookings (id, guest_id, room_id, check_in, check_out, guests, total_amount, status, payment_status, checked_in) VALUES
('BK01', 'G01', 'R101', DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), DATE_ADD(CURRENT_DATE, INTERVAL 4 DAY), 2, 55500.00, 'active', 'pending', FALSE),
('BK02', 'G02', 'R201', DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY), DATE_ADD(CURRENT_DATE, INTERVAL 8 DAY), 2, 75600.00, 'active', 'pending', FALSE);
