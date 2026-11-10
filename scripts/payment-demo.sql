-- Optional local demo data for Payment & Billing while Reservation is unfinished.
-- Run against hotel_reservation. All entries are explicitly labelled demo; no login is enabled.
INSERT IGNORE INTO guests(id,name,email,password_hash,guest_type,active) VALUES
('GPAYDEMO','Payment Demo Guest','payment-demo@hotel.test','DEMO_NO_LOGIN','VIP',FALSE);
INSERT IGNORE INTO bookings(id,guest_id,room_id,check_in,check_out,guests,booking_type) VALUES
('BKPDEMO1','GPAYDEMO','R101',DATE_ADD(CURRENT_DATE,INTERVAL 30 DAY),DATE_ADD(CURRENT_DATE,INTERVAL 32 DAY),1,'DESK'),
('BKPDEMO2','GPAYDEMO','R201',DATE_ADD(CURRENT_DATE,INTERVAL 30 DAY),DATE_ADD(CURRENT_DATE,INTERVAL 32 DAY),1,'DESK'),
('BKPDEMO3','GPAYDEMO','R301',DATE_ADD(CURRENT_DATE,INTERVAL 30 DAY),DATE_ADD(CURRENT_DATE,INTERVAL 33 DAY),1,'DESK');
