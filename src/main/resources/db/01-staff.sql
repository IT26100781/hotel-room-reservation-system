-- Owner: Admin / Staff component
-- Staff and Admin accounts share one table; the role column decides which class is created.

CREATE TABLE IF NOT EXISTS staff (
    id            VARCHAR(10)  NOT NULL,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(10)  NOT NULL,
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_staff_email (email),
    CONSTRAINT chk_staff_role CHECK (role IN ('STAFF', 'ADMIN'))
);
