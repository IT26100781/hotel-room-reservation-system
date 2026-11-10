-- Owner: Payment component
-- One table for both payment methods; method decides which subclass is created
-- (CashPayment / CardPayment). Card payments store only the card type and last 4 digits.
-- The rates are copied in when the payment is taken, so an invoice never changes later.
-- booking_id is UNIQUE: a booking can only be paid once (PaymentService relies on this).

CREATE TABLE IF NOT EXISTS payments (
    id           VARCHAR(10)   NOT NULL,
    booking_id   VARCHAR(10)   NOT NULL,
    nightly_rate DECIMAL(10,2) NOT NULL,
    room_total   DECIMAL(10,2) NOT NULL,
    guest_rate   DECIMAL(4,2)  NOT NULL DEFAULT 0,
    amount       DECIMAL(10,2) NOT NULL,
    method       VARCHAR(10)   NOT NULL,
    card_type    VARCHAR(20),
    card_last4   CHAR(4),
    status       VARCHAR(15)   NOT NULL DEFAULT 'paid',
    paid_on      DATE          NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_payments_booking (booking_id),
    CONSTRAINT fk_payments_booking FOREIGN KEY (booking_id) REFERENCES bookings (id),
    CONSTRAINT chk_payments_method CHECK (method IN ('cash', 'card')),
    CONSTRAINT chk_payments_status CHECK (status IN ('paid', 'refunded')),
    CONSTRAINT chk_payments_amount CHECK (amount >= 0)
);

-- Upgrade the original scaffold's status constraint without deleting existing rows.
ALTER TABLE payments DROP CHECK chk_payments_status,
    ADD CONSTRAINT chk_payments_status CHECK (status IN ('pending', 'paid', 'refunded'));

-- Row-locked sequence avoids duplicate IDs under concurrent staff submissions.
CREATE TABLE IF NOT EXISTS payment_sequence (
    singleton INT NOT NULL PRIMARY KEY,
    next_value BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_payment_sequence_singleton CHECK (singleton = 1)
);
INSERT IGNORE INTO payment_sequence (singleton, next_value) VALUES (1, 0);
