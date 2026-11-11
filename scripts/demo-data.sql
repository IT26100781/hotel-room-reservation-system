-- The Orchid Estate: demo data for the viva.
-- Run it on a separate demo database, never on real data. It DELETES every booking, payment,
-- review, guest, room and staff row first, then inserts a fresh, consistent set.
-- All dates are relative to @demo_day (the viva day). Change that one line if the demo moves,
-- or set it to CURRENT_DATE to build the data around today.
-- Every demo account uses the password: 12345678

SET @demo_day = DATE '2026-11-12';

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM reviews;
DELETE FROM payments;
DELETE FROM bookings;
DELETE FROM rooms;
DELETE FROM guests;
DELETE FROM staff;
SET FOREIGN_KEY_CHECKS = 1;

-- Staff: 2 admins, 2 front desk staff, 1 deactivated account
INSERT INTO staff (id, name, email, password_hash, role, active, must_change_password) VALUES
('S01', 'Sithum Herath', 'admin@orchidestate.lk', 'USDWmlvi2OZfQnDQLucRyw==:pwZwvTnFTEoVv0/cz/2YBmr06T6O8Ow5ygbxnpTDd9Y=', 'ADMIN', TRUE, FALSE),
('S02', 'Kasun Wijeratne', 'kasun@orchidestate.lk', 'Y03r35zUEVVyu0ngvnF2DA==:+6YduqCnS3PR+VVHF4McOMclASiQ0gIwUCtPpH85T0I=', 'ADMIN', TRUE, FALSE),
('S03', 'Nadeesha Fonseka', 'nadeesha@orchidestate.lk', 'FjYevLV2qP/hpMd7/3RGfw==:O42FZK5evFi2AMvstOWz4PrVWxveV5Udx8Lw6atd8SQ=', 'STAFF', TRUE, FALSE),
('S04', 'Ruwan Jayawardena', 'ruwan@orchidestate.lk', 'H7DTXHEeG6V0qaMRdL811A==:fsegavq2A9ENP6c9bOeU4BCOSxgGHv8E2/7JExFOQPU=', 'STAFF', TRUE, FALSE),
('S05', 'Imesha Dias', 'imesha@orchidestate.lk', 'EcTdJ1+WPw+TCFJUVrax4w==:GoA1IOaV0mrkoPwGJqLhG15SHh+m8UT70YcfKPUeZTc=', 'STAFF', FALSE, FALSE);

-- Guests: 3 VIP, 4 regular, 1 deactivated
INSERT INTO guests (id, name, email, password_hash, guest_type, active) VALUES
('G01', 'Nimal Perera', 'nimal@demo.lk', 'ToOMX3LykRUQ2GkLOK+ESg==:6mRA5pfBlKSoqgFkhWVSrRjxgT6nHUpixWpufCLyaNQ=', 'VIP', TRUE),
('G02', 'Kamala Silva', 'kamala@demo.lk', 'v38QOoVdjkcONrE7FHx/ow==:Ac/kQ5q4GsQA+YReHzAd8YuCY7gtf7LJTOSh6fCEEd0=', 'REGULAR', TRUE),
('G03', 'Ravindu Bandara', 'ravindu@demo.lk', 'Kxgw9KXnJI+sk4uLMo1urA==:UGO7DYu086o9isF57f5YZjKbbewXU/rhJc3/1iV6ahQ=', 'VIP', TRUE),
('G04', 'Sarah Collins', 'sarah@demo.lk', 'wbem9t1eo0boodT4bvOO3Q==:aVFxOnlKLUk7fAzkkcCxkG2CfLkI7DjjI+cBRrW8CSg=', 'REGULAR', TRUE),
('G05', 'Ashan Fernando', 'ashan@demo.lk', 'HYN+4h0O796nZId/jI3GyQ==:4QLJNp4csQAKZR870ye65es39NwjFa4vcDnDPlfafds=', 'REGULAR', TRUE),
('G06', 'Dilini Jayasuriya', 'dilini@demo.lk', 'v7iVctZuv4u9UBDaBUtypQ==:oxa2pFI7utU6Ekoeaie87u8LeQ850Y655S5IELwrqz0=', 'VIP', TRUE),
('G07', 'Mohamed Rizwan', 'rizwan@demo.lk', 'RbAakPQ0mORI7kpvfwNL9A==:magNLdmZlbSII9wDJ3HXBnmflgkJ/f+4fXCw0SiUvp4=', 'REGULAR', FALSE),
('G08', 'Tharushi Kumari', 'tharushi@demo.lk', '7F/DcgypSfiH9dSNHN41Eg==:UxFr9ct8UFIaWhAZw07yVKg7flwiMWDbOv3tSLbYgAc=', 'REGULAR', TRUE);

-- Rooms: every type and every status (active, maintenance, retired)
INSERT INTO rooms (id, room_number, room_name, room_type, base_rate, capacity, room_size, image_url, status) VALUES
('R101', 101, 'Garden Standard', 'StandardRoom', 18500.00, 2, 32, 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?auto=format&fit=crop&w=700&q=80', 'active'),
('R102', 102, 'Pine Standard', 'StandardRoom', 18500.00, 2, 32, 'https://images.unsplash.com/photo-1595576508898-0ad5c879a061?auto=format&fit=crop&w=700&q=80', 'active'),
('R103', 103, 'Tea Terrace Standard', 'StandardRoom', 19500.00, 2, 30, 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=700&q=80', 'active'),
('R104', 104, 'Courtyard Standard', 'StandardRoom', 18500.00, 2, 30, 'https://images.unsplash.com/photo-1598928506311-c55ded91a20c?auto=format&fit=crop&w=700&q=80', 'active'),
('R105', 105, 'Old Wing Standard', 'StandardRoom', 16000.00, 2, 26, 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?auto=format&fit=crop&w=700&q=80', 'retired'),
('R201', 201, 'Valley Deluxe', 'DeluxeRoom', 28000.00, 3, 44, 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=700&q=80', 'active'),
('R202', 202, 'Bungalow Deluxe', 'DeluxeRoom', 28000.00, 3, 44, 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=700&q=80', 'maintenance'),
('R203', 203, 'Lakeview Deluxe', 'DeluxeRoom', 30000.00, 3, 46, 'https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=700&q=80', 'active'),
('R204', 204, 'Rose Garden Deluxe', 'DeluxeRoom', 29000.00, 3, 42, 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?auto=format&fit=crop&w=700&q=80', 'active'),
('R301', 301, 'Estate Suite', 'SuiteRoom', 46000.00, 4, 62, 'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=700&q=80', 'active'),
('R302', 302, 'Hilltop Suite', 'SuiteRoom', 52000.00, 4, 70, 'https://images.unsplash.com/photo-1591088398332-8a7791972843?auto=format&fit=crop&w=700&q=80', 'active');

-- Bookings: completed, in house, arriving today, upcoming, cancelled, refunded, no-show
INSERT INTO bookings (id, guest_id, room_id, check_in, check_out, guests, booking_type, status, payment_status, checked_in) VALUES
('BK01', 'G03', 'R301', DATE_ADD(@demo_day, INTERVAL -20 DAY), DATE_ADD(@demo_day, INTERVAL -17 DAY), 2, 'ONLINE', 'active', 'paid', TRUE),  -- completed stay, reviewed
('BK02', 'G04', 'R201', DATE_ADD(@demo_day, INTERVAL -15 DAY), DATE_ADD(@demo_day, INTERVAL -13 DAY), 2, 'ONLINE', 'active', 'paid', TRUE),  -- completed stay, reviewed
('BK03', 'G05', 'R101', DATE_ADD(@demo_day, INTERVAL -12 DAY), DATE_ADD(@demo_day, INTERVAL -10 DAY), 1, 'DESK', 'active', 'paid', TRUE),  -- completed walk-in stay, reviewed
('BK04', 'G06', 'R203', DATE_ADD(@demo_day, INTERVAL -9 DAY), DATE_ADD(@demo_day, INTERVAL -6 DAY), 3, 'ONLINE', 'active', 'paid', TRUE),  -- completed stay, reviewed
('BK05', 'G01', 'R102', DATE_ADD(@demo_day, INTERVAL -6 DAY), DATE_ADD(@demo_day, INTERVAL -3 DAY), 2, 'ONLINE', 'active', 'paid', TRUE),  -- completed stay, NOT reviewed yet (live verified review demo)
('BK06', 'G07', 'R103', DATE_ADD(@demo_day, INTERVAL -30 DAY), DATE_ADD(@demo_day, INTERVAL -28 DAY), 1, 'DESK', 'active', 'paid', TRUE),  -- completed stay of the deactivated guest
('BK07', 'G02', 'R201', DATE_ADD(@demo_day, INTERVAL -1 DAY), DATE_ADD(@demo_day, INTERVAL 2 DAY), 2, 'DESK', 'active', 'pending', TRUE),  -- in house now, card payment pending (Mark paid demo)
('BK08', 'G04', 'R101', @demo_day, DATE_ADD(@demo_day, INTERVAL 2 DAY), 2, 'ONLINE', 'active', 'pending', FALSE),  -- arriving today (check in and take payment demo)
('BK09', 'G03', 'R203', DATE_ADD(@demo_day, INTERVAL 5 DAY), DATE_ADD(@demo_day, INTERVAL 8 DAY), 2, 'ONLINE', 'active', 'pending', FALSE),  -- upcoming VIP stay (change dates / cancel demo)
('BK10', 'G05', 'R302', DATE_ADD(@demo_day, INTERVAL 10 DAY), DATE_ADD(@demo_day, INTERVAL 13 DAY), 3, 'ONLINE', 'active', 'pending', FALSE),  -- upcoming suite stay
('BK11', 'G06', 'R204', DATE_ADD(@demo_day, INTERVAL 3 DAY), DATE_ADD(@demo_day, INTERVAL 5 DAY), 2, 'ONLINE', 'cancelled', 'paid', FALSE),  -- cancelled after paying (Refund needed demo)
('BK12', 'G02', 'R103', DATE_ADD(@demo_day, INTERVAL -40 DAY), DATE_ADD(@demo_day, INTERVAL -38 DAY), 1, 'ONLINE', 'cancelled', 'refunded', FALSE),  -- cancelled and refunded
('BK13', 'G01', 'R104', DATE_ADD(@demo_day, INTERVAL -4 DAY), DATE_ADD(@demo_day, INTERVAL -2 DAY), 1, 'ONLINE', 'active', 'pending', FALSE),  -- no-show (never checked in)
('BK14', 'G01', 'R301', DATE_ADD(@demo_day, INTERVAL 20 DAY), DATE_ADD(@demo_day, INTERVAL 23 DAY), 2, 'ONLINE', 'active', 'pending', FALSE);  -- upcoming VIP suite stay

-- Payments: amounts follow the app's rules (VIP 10% off, then 5% off for cash; suites 5% off for 3+ nights)
INSERT INTO payments (id, booking_id, nightly_rate, room_total, guest_rate, amount, method, card_type, card_last4, status, paid_on) VALUES
('P1', 'BK01', 46000.00, 131100.00, 0.10, 117990.00, 'card', 'Visa', '4242', 'paid', DATE_ADD(@demo_day, INTERVAL -20 DAY)),
('P2', 'BK02', 28000.00, 56000.00, 0.00, 53200.00, 'cash', NULL, NULL, 'paid', DATE_ADD(@demo_day, INTERVAL -15 DAY)),
('P3', 'BK03', 18500.00, 37000.00, 0.00, 35150.00, 'cash', NULL, NULL, 'paid', DATE_ADD(@demo_day, INTERVAL -12 DAY)),
('P4', 'BK04', 30000.00, 90000.00, 0.10, 81000.00, 'card', 'Mastercard', '4444', 'paid', DATE_ADD(@demo_day, INTERVAL -9 DAY)),
('P5', 'BK05', 18500.00, 55500.00, 0.10, 47452.50, 'cash', NULL, NULL, 'paid', DATE_ADD(@demo_day, INTERVAL -6 DAY)),
('P6', 'BK06', 19500.00, 39000.00, 0.00, 39000.00, 'card', 'Visa', '4242', 'paid', DATE_ADD(@demo_day, INTERVAL -30 DAY)),
('P7', 'BK07', 28000.00, 84000.00, 0.00, 84000.00, 'card', 'Visa', '4242', 'pending', DATE_ADD(@demo_day, INTERVAL -1 DAY)),
('P8', 'BK11', 29000.00, 58000.00, 0.10, 52200.00, 'card', 'Visa', '4242', 'paid', DATE_ADD(@demo_day, INTERVAL -2 DAY)),
('P9', 'BK12', 19500.00, 39000.00, 0.00, 37050.00, 'cash', NULL, NULL, 'refunded', DATE_ADD(@demo_day, INTERVAL -40 DAY));
UPDATE payment_sequence SET next_value = 9 WHERE singleton = 1;

-- Reviews: verified (linked to a completed booking) and public
INSERT INTO reviews (id, guest_id, booking_id, rating, verified, review_text, created_at) VALUES
('RV0001', 'G03', 'BK01', 5, TRUE, 'We woke to mist over the tea fields every morning. The suite terrace is the best seat in Sri Lanka.', DATE_ADD(TIMESTAMP(@demo_day, CURRENT_TIME), INTERVAL -16 DAY)),
('RV0002', 'G04', 'BK02', 5, TRUE, 'Quiet, warm and beautifully run. Check-in took two minutes and they remembered our tea order from the first evening.', DATE_ADD(TIMESTAMP(@demo_day, CURRENT_TIME), INTERVAL -12 DAY)),
('RV0003', 'G05', 'BK03', 4, TRUE, 'Simple room, spotless, and the breakfast hoppers were worth the trip on their own.', DATE_ADD(TIMESTAMP(@demo_day, CURRENT_TIME), INTERVAL -9 DAY)),
('RV0004', 'G06', 'BK04', 5, TRUE, 'The lakeview room smelled of roses every evening. Pure calm.', DATE_ADD(TIMESTAMP(@demo_day, CURRENT_TIME), INTERVAL -5 DAY)),
('RV0005', 'G07', 'BK06', 3, TRUE, 'Lovely place, but the Standard room is quite small for two big suitcases. Great breakfast.', DATE_ADD(TIMESTAMP(@demo_day, CURRENT_TIME), INTERVAL -27 DAY)),
('RV0006', 'G08', NULL, 4, FALSE, 'Stopped by for high tea on the terrace. Lovely gardens and very friendly staff. Will come back to stay next time.', DATE_ADD(TIMESTAMP(@demo_day, CURRENT_TIME), INTERVAL -7 DAY)),
('RV0007', 'G02', NULL, 2, FALSE, 'Dinner service was slow on a busy Saturday night.', DATE_ADD(TIMESTAMP(@demo_day, CURRENT_TIME), INTERVAL -3 DAY));
