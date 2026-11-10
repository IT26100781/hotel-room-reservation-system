-- Owner: Room component
-- One table for all room types; room_type decides which subclass is created
-- (StandardRoom / DeluxeRoom / SuiteRoom). Rooms are soft-deleted (status = 'retired'),
-- so bookings that reference a room stay valid.

CREATE TABLE IF NOT EXISTS rooms (
    id           VARCHAR(10)   NOT NULL,
    room_type    VARCHAR(20)   NOT NULL,
    room_name    VARCHAR(100)  NOT NULL,
    room_number  INT           NOT NULL,
    base_rate    DECIMAL(10,2) NOT NULL,
    capacity     INT           NOT NULL,
    room_size    INT           NOT NULL,
    image_url    VARCHAR(500),
    status       VARCHAR(15)   NOT NULL DEFAULT 'active',
    PRIMARY KEY (id),
    UNIQUE KEY uk_rooms_number (room_number),
    CONSTRAINT chk_rooms_type     CHECK (room_type IN ('StandardRoom', 'DeluxeRoom', 'SuiteRoom')),
    CONSTRAINT chk_rooms_status   CHECK (status IN ('active', 'maintenance', 'retired')),
    CONSTRAINT chk_rooms_rate     CHECK (base_rate > 0),
    CONSTRAINT chk_rooms_capacity CHECK (capacity >= 1),
    CONSTRAINT chk_rooms_size     CHECK (room_size > 0)
);

-- Initial seed data matching the UI prototype
INSERT IGNORE INTO rooms (id, room_number, room_name, room_type, base_rate, capacity, room_size, image_url, status) VALUES
('R101', 101, 'Garden Standard', 'StandardRoom', 18500.00, 2, 32, 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?auto=format&fit=crop&w=700&q=80', 'active'),
('R102', 102, 'Pine Standard', 'StandardRoom', 18500.00, 2, 32, 'https://images.unsplash.com/photo-1595576508898-0ad5c879a061?auto=format&fit=crop&w=700&q=80', 'active'),
('R201', 201, 'Valley Deluxe', 'DeluxeRoom', 28000.00, 3, 44, 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=700&q=80', 'active'),
('R202', 202, 'Bungalow Deluxe', 'DeluxeRoom', 28000.00, 3, 44, 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=700&q=80', 'maintenance'),
('R301', 301, 'Estate Suite', 'SuiteRoom', 46000.00, 4, 62, 'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=700&q=80', 'active');
