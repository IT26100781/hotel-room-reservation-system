-- Owner: Guest / User component
-- Regular and VIP guests share one table; the guest_type column decides which class is created
-- (RegularGuest or VipGuest). Guests are never deleted: deactivate sets active = FALSE,
-- because bookings and reviews still point at the guest id.

CREATE TABLE IF NOT EXISTS guests (
    id            VARCHAR(10)  NOT NULL,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    guest_type    VARCHAR(10)  NOT NULL DEFAULT 'REGULAR',
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_guests_email (email),
    CONSTRAINT chk_guests_type CHECK (guest_type IN ('REGULAR', 'VIP'))
);
