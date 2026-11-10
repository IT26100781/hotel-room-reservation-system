package com.hotel.reservation;

import com.hotel.reservation.dao.ReservationDAO;
import com.hotel.reservation.model.BookingStatus;
import com.hotel.reservation.model.DateRange;
import com.hotel.reservation.model.PaymentStatus;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.service.ReservationService;
import com.hotel.room.model.DeluxeRoom;
import com.hotel.room.model.Room;
import com.hotel.room.model.RoomStatus;
import com.hotel.room.model.StandardRoom;
import com.hotel.room.service.RoomService;
import com.hotel.user.admin.model.Admin;
import com.hotel.user.guest.model.RegularGuest;
import com.hotel.user.guest.model.VipGuest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationModelAndServiceTest {

    private ReservationService reservationService;
    private RoomService mockRoomService;
    private List<Reservation> fakeDb;

    private RegularGuest regularGuest;
    private VipGuest vipGuest;
    private Admin adminUser;
    private Room activeRoom;

    @BeforeEach
    void setUp() {
        fakeDb = new ArrayList<>();

        // In-memory fake DAO to avoid needing a live MySQL database
        ReservationDAO fakeDao = new ReservationDAO(null) {
            @Override
            public boolean hasOverlap(String roomId, LocalDate checkIn, LocalDate checkOut) {
                DateRange proposed = new DateRange(checkIn, checkOut);
                return fakeDb.stream()
                        .filter(r -> r.getRoomId().equalsIgnoreCase(roomId) && r.isActive())
                        .anyMatch(r -> r.getDateRange().overlapsWith(proposed));
            }

            @Override
            public boolean save(Reservation b) {
                fakeDb.add(b);
                return true;
            }

            @Override
            public Reservation findById(String id) {
                return fakeDb.stream()
                        .filter(r -> r.getId().equalsIgnoreCase(id))
                        .findFirst()
                        .orElse(null);
            }

            @Override
            public List<Reservation> findByGuestId(String guestId) {
                return fakeDb.stream()
                        .filter(r -> r.getGuestId().equalsIgnoreCase(guestId))
                        .toList();
            }

            @Override
            public List<Reservation> findAll() {
                return new ArrayList<>(fakeDb);
            }

            @Override
            public boolean updateStatus(String id, BookingStatus status) {
                Reservation r = findById(id);
                if (r != null) {
                    // simulate status change
                    r.cancel();
                    return true;
                }
                return false;
            }

            @Override
            public boolean updatePaymentStatus(String id, PaymentStatus status) {
                Reservation r = findById(id);
                if (r != null) {
                    r.setPaymentStatus(status);
                    return true;
                }
                return false;
            }

            @Override
            public boolean updateCheckedIn(String id, boolean checkedIn) {
                Reservation r = findById(id);
                if (r != null) {
                    r.markCheckedIn();
                    return true;
                }
                return false;
            }
        };

        mockRoomService = mock(RoomService.class);
        reservationService = new ReservationService(fakeDao, mockRoomService);

        regularGuest = new RegularGuest("G01", "Nimal Silva", "nimal@test.com", "hash");
        vipGuest = new VipGuest("G02", "Kamal Perera", "kamal@test.com", "hash");
        adminUser = new Admin("S01", "Admin Staff", "admin@test.com", "hash");

        activeRoom = new StandardRoom("R101", "Garden Standard", 101, 10000.00, 2, 32, "url", RoomStatus.ACTIVE);
        when(mockRoomService.getRoomById("R101")).thenReturn(activeRoom);
    }

    // ==========================================
    // 1. DateRange & Overlap Logic Tests
    // ==========================================

    @Test
    void testDateRangeValidation() {
        LocalDate today = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () -> new DateRange(today, today));
        assertThrows(IllegalArgumentException.class, () -> new DateRange(today.plusDays(2), today));

        DateRange valid = new DateRange(today, today.plusDays(3));
        assertEquals(3, valid.getNights());
    }

    @Test
    void testDateRangeOverlapMatrix() {
        LocalDate d10 = LocalDate.of(2026, 10, 10);
        LocalDate d15 = LocalDate.of(2026, 10, 15);
        DateRange base = new DateRange(d10, d15); // [10, 15)

        // Completely before: [5, 10) - checkout on 10th should NOT overlap with checkin on 10th
        DateRange before = new DateRange(LocalDate.of(2026, 10, 5), d10);
        assertFalse(base.overlapsWith(before));
        assertFalse(before.overlapsWith(base));

        // Completely after: [15, 20) - checkin on 15th should NOT overlap with checkout on 15th
        DateRange after = new DateRange(d15, LocalDate.of(2026, 10, 20));
        assertFalse(base.overlapsWith(after));
        assertFalse(after.overlapsWith(base));

        // Partial overlap start: [8, 12)
        DateRange overlapStart = new DateRange(LocalDate.of(2026, 10, 8), LocalDate.of(2026, 10, 12));
        assertTrue(base.overlapsWith(overlapStart));

        // Partial overlap end: [13, 18)
        DateRange overlapEnd = new DateRange(LocalDate.of(2026, 10, 13), LocalDate.of(2026, 10, 18));
        assertTrue(base.overlapsWith(overlapEnd));

        // Enclosing: [8, 18)
        DateRange enclosing = new DateRange(LocalDate.of(2026, 10, 8), LocalDate.of(2026, 10, 18));
        assertTrue(base.overlapsWith(enclosing));

        // Enclosed: [11, 14)
        DateRange enclosed = new DateRange(LocalDate.of(2026, 10, 11), LocalDate.of(2026, 10, 14));
        assertTrue(base.overlapsWith(enclosed));
    }

    // ==========================================
    // 2. Dynamic Pricing & VIP Discount Tests
    // ==========================================

    @Test
    void testPricingForRegularGuest() {
        LocalDate in = LocalDate.now().plusDays(1);
        LocalDate out = in.plusDays(3); // 3 nights @ 10,000 = 30,000

        Reservation res = reservationService.createBooking(regularGuest, "R101", in, out, 2);
        assertNotNull(res);
        assertEquals(30000.00, res.getTotalAmount(), 0.01);
    }

    @Test
    void testPricingForVipGuestGets10PercentOff() {
        LocalDate in = LocalDate.now().plusDays(1);
        LocalDate out = in.plusDays(3); // 3 nights @ 10,000 = 30,000 -> 10% off = 27,000

        Reservation res = reservationService.createBooking(vipGuest, "R101", in, out, 2);
        assertNotNull(res);
        assertEquals(27000.00, res.getTotalAmount(), 0.01);
    }

    // ==========================================
    // 3. Double Booking Prevention Test
    // ==========================================

    @Test
    void testDoubleBookingIsPrevented() {
        LocalDate in = LocalDate.now().plusDays(2);
        LocalDate out = in.plusDays(4);

        // First booking succeeds
        reservationService.createBooking(regularGuest, "R101", in, out, 2);

        // Conflicting booking attempt should throw IllegalStateException
        LocalDate conflictIn = in.plusDays(1);
        LocalDate conflictOut = out.plusDays(1);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                reservationService.createBooking(vipGuest, "R101", conflictIn, conflictOut, 2)
        );
        assertTrue(ex.getMessage().contains("already booked"));
    }

    // ==========================================
    // 4. Security & IDOR Cancellation Tests
    // ==========================================

    @Test
    void testGuestCannotCancelAnotherGuestsBooking() {
        LocalDate in = LocalDate.now().plusDays(5);
        LocalDate out = in.plusDays(7);

        Reservation res = reservationService.createBooking(regularGuest, "R101", in, out, 2);

        // VIP guest tries to cancel Regular guest's booking -> SecurityException
        assertThrows(SecurityException.class, () ->
                reservationService.cancelBooking(vipGuest, res.getId())
        );
    }

    @Test
    void testGuestCanCancelTheirOwnUpcomingBooking() {
        LocalDate in = LocalDate.now().plusDays(5);
        LocalDate out = in.plusDays(7);

        Reservation res = reservationService.createBooking(regularGuest, "R101", in, out, 2);

        reservationService.cancelBooking(regularGuest, res.getId());
        assertEquals(BookingStatus.CANCELLED, res.getStatus());
    }

    @Test
    void testAdminCanCancelAnyActiveBooking() {
        LocalDate in = LocalDate.now().plusDays(5);
        LocalDate out = in.plusDays(7);

        Reservation res = reservationService.createBooking(regularGuest, "R101", in, out, 2);

        reservationService.cancelBooking(adminUser, res.getId());
        assertEquals(BookingStatus.CANCELLED, res.getStatus());
    }

    // ==========================================
    // 5. Downstream Integration Contracts
    // ==========================================

    @Test
    void testPendingPaymentBookingsIntegration() {
        LocalDate in = LocalDate.now().plusDays(1);
        LocalDate out = in.plusDays(2);

        Reservation res = reservationService.createBooking(regularGuest, "R101", in, out, 2);
        List<Reservation> pending = reservationService.getPendingPaymentBookings();

        assertEquals(1, pending.size());
        assertEquals(res.getId(), pending.get(0).getId());

        // Update payment to PAID
        reservationService.updatePaymentStatus(res.getId(), PaymentStatus.PAID);
        assertTrue(reservationService.getPendingPaymentBookings().isEmpty());
    }
}
