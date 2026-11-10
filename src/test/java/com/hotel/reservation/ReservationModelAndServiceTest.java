package com.hotel.reservation;

import com.hotel.reservation.dao.ReservationDAO;
import com.hotel.reservation.model.*;
import com.hotel.reservation.service.ReservationService;
import com.hotel.room.model.Room;
import com.hotel.room.model.RoomStatus;
import com.hotel.room.model.StandardRoom;
import com.hotel.room.service.RoomService;
import com.hotel.user.admin.model.Admin;
import com.hotel.user.admin.model.Staff;
import com.hotel.user.guest.model.Guest;
import com.hotel.user.guest.model.RegularGuest;
import com.hotel.user.guest.model.VipGuest;
import com.hotel.user.guest.service.GuestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests verifying the Reservation domain model, Single-Table Inheritance,
 * polymorphic pricing, thread-safe service business logic, security permissions,
 * and downstream cross-module contracts.
 */
class ReservationModelAndServiceTest {

    private ReservationService reservationService;
    private RoomService mockRoomService;
    private GuestService mockGuestService;
    private List<Reservation> fakeDb;

    private RegularGuest regularGuest;
    private VipGuest vipGuest;
    private Staff staffUser;
    private Admin adminUser;
    private Room activeRoom;

    @BeforeEach
    void setUp() {
        fakeDb = new ArrayList<>();

        // In-memory fake DAO mimicking JDBC behavior for isolated testing
        ReservationDAO fakeDao = new ReservationDAO(null) {
            @Override
            public boolean hasOverlap(String roomId, LocalDate checkIn, LocalDate checkOut, String excludeId) {
                DateRange proposed = new DateRange(checkIn, checkOut);
                return fakeDb.stream()
                        .filter(r -> r.getRoomId().equalsIgnoreCase(roomId))
                        .filter(r -> r.getStatus() == BookingStatus.ACTIVE)
                        .filter(r -> excludeId == null || !r.getId().equalsIgnoreCase(excludeId))
                        .anyMatch(r -> new DateRange(r.getCheckIn(), r.getCheckOut()).overlapsWith(proposed));
            }

            @Override
            public boolean hasOverlap(String roomId, LocalDate checkIn, LocalDate checkOut) {
                return hasOverlap(roomId, checkIn, checkOut, null);
            }

            @Override
            public boolean save(Reservation r) {
                fakeDb.add(r);
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
            public boolean cancel(String id) {
                Reservation r = findById(id);
                if (r != null) {
                    // Update status in place for fake db
                    Reservation updated = Reservation.create(
                            r.getId(), r.getGuestId(), r.getRoomId(), r.getBookingType().getValue(),
                            r.getCheckIn(), r.getCheckOut(), r.getGuests(),
                            BookingStatus.CANCELLED.getValue(), r.getPaymentStatus().getValue(), r.isCheckedIn()
                    );
                    fakeDb.remove(r);
                    fakeDb.add(updated);
                    return true;
                }
                return false;
            }

            @Override
            public boolean checkIn(String id) {
                Reservation r = findById(id);
                if (r != null) {
                    Reservation updated = Reservation.create(
                            r.getId(), r.getGuestId(), r.getRoomId(), r.getBookingType().getValue(),
                            r.getCheckIn(), r.getCheckOut(), r.getGuests(),
                            r.getStatus().getValue(), r.getPaymentStatus().getValue(), true
                    );
                    fakeDb.remove(r);
                    fakeDb.add(updated);
                    return true;
                }
                return false;
            }

            @Override
            public int findMaxIdNumber() {
                return fakeDb.size();
            }

            @Override
            public List<Reservation> findPendingPayment() {
                return fakeDb.stream()
                        .filter(r -> r.getStatus() == BookingStatus.ACTIVE && r.getPaymentStatus() == PaymentStatus.PENDING)
                        .toList();
            }

            @Override
            public boolean hasCompletedStay(String guestId, String bookingId) {
                return fakeDb.stream()
                        .anyMatch(r -> r.getId().equalsIgnoreCase(bookingId)
                                && r.getGuestId().equalsIgnoreCase(guestId)
                                && r.getStatus() == BookingStatus.ACTIVE
                                && r.isCheckedIn()
                                && !r.getCheckOut().isAfter(LocalDate.now()));
            }
        };

        mockRoomService = mock(RoomService.class);
        mockGuestService = mock(GuestService.class);

        reservationService = new ReservationService(fakeDao, mockRoomService, mockGuestService);

        regularGuest = new RegularGuest("G01", "Nimal Silva", "nimal@test.com", "hash");
        vipGuest = new VipGuest("G02", "Kamal Perera", "kamal@test.com", "hash");
        staffUser = new Staff("S01", "Staff Member", "staff@test.com", "hash");
        adminUser = new Admin("A01", "Admin Boss", "admin@test.com", "hash");

        activeRoom = new StandardRoom("R101", "Garden Standard", 101, 10000.00, 2, 32, "url", RoomStatus.ACTIVE);
        when(mockRoomService.getRoomById("R101")).thenReturn(activeRoom);
        when(mockGuestService.findGuestById(any(), eq("G01"))).thenReturn(regularGuest);
        when(mockGuestService.findGuestById(any(), eq("G02"))).thenReturn(vipGuest);
    }

    // ==========================================
    // 1. Domain Inheritance & Factory Tests
    // ==========================================

    @Test
    void testInheritanceHierarchyOnlineAndDesk() {
        Reservation online = Reservation.create("BK01", "G01", "R101", "ONLINE",
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), 2,
                "active", "pending", false);

        assertTrue(online instanceof OnlineReservation);
        assertEquals(BookingType.ONLINE, online.getBookingType());
        assertEquals("Online", online.getSourceLabel());

        Reservation desk = Reservation.create("BK02", "G01", "R101", "DESK",
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(7), 2,
                "active", "pending", false);

        assertTrue(desk instanceof DeskReservation);
        assertEquals(BookingType.DESK, desk.getBookingType());
        assertEquals("Front desk", desk.getSourceLabel());
    }

    // ==========================================
    // 2. DateRange & Overlap Logic Tests
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

        // Checkout on 10th does NOT overlap with checkin on 10th
        DateRange before = new DateRange(LocalDate.of(2026, 10, 5), d10);
        assertFalse(base.overlapsWith(before));
        assertFalse(before.overlapsWith(base));

        // Checkin on 15th does NOT overlap with checkout on 15th
        DateRange after = new DateRange(d15, LocalDate.of(2026, 10, 20));
        assertFalse(base.overlapsWith(after));
        assertFalse(after.overlapsWith(base));

        // Overlapping ranges
        DateRange overlapStart = new DateRange(LocalDate.of(2026, 10, 8), LocalDate.of(2026, 10, 12));
        assertTrue(base.overlapsWith(overlapStart));

        DateRange overlapEnd = new DateRange(LocalDate.of(2026, 10, 13), LocalDate.of(2026, 10, 18));
        assertTrue(base.overlapsWith(overlapEnd));
    }

    // ==========================================
    // 3. Dynamic Pricing & VIP Discount Tests
    // ==========================================

    @Test
    void testPricingAndVipDiscountViaBookingDetails() {
        LocalDate in = LocalDate.now().plusDays(1);
        LocalDate out = in.plusDays(3); // 3 nights @ 10,000 = 30,000

        // Regular Guest -> 0% discount
        Reservation regRes = reservationService.createOnlineBooking(regularGuest, "R101", in, out, 2);
        BookingDetails regDetails = new BookingDetails(regRes, activeRoom, regularGuest);
        assertEquals(30000.00, regDetails.getFullRateTotal(), 0.01);
        assertEquals(0.0, regDetails.getGuestDiscount(), 0.01);
        assertEquals(30000.00, regDetails.getEstimatedTotal(), 0.01);

        // VIP Guest -> 10% discount
        LocalDate inVip = LocalDate.now().plusDays(5);
        LocalDate outVip = inVip.plusDays(3);
        Reservation vipRes = reservationService.createOnlineBooking(vipGuest, "R101", inVip, outVip, 2);
        BookingDetails vipDetails = new BookingDetails(vipRes, activeRoom, vipGuest);
        assertEquals(30000.00, vipDetails.getFullRateTotal(), 0.01);
        assertEquals(3000.00, vipDetails.getGuestDiscount(), 0.01);
        assertEquals(27000.00, vipDetails.getEstimatedTotal(), 0.01);
    }

    // ==========================================
    // 4. Double Booking Prevention Test
    // ==========================================

    @Test
    void testDoubleBookingIsPrevented() {
        LocalDate in = LocalDate.now().plusDays(2);
        LocalDate out = in.plusDays(4);

        reservationService.createOnlineBooking(regularGuest, "R101", in, out, 2);

        LocalDate conflictIn = in.plusDays(1);
        LocalDate conflictOut = out.plusDays(1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.createOnlineBooking(vipGuest, "R101", conflictIn, conflictOut, 2)
        );
        assertTrue(ex.getMessage().contains("not available"));
    }

    // ==========================================
    // 5. Staff Desk Booking (Guest ID separation)
    // ==========================================

    @Test
    void testStaffDeskBookingSavesGuestIdNotStaffId() {
        LocalDate in = LocalDate.now().plusDays(10);
        LocalDate out = in.plusDays(12);

        Reservation deskRes = reservationService.createDeskBooking(staffUser, "G01", "R101", in, out, 2);

        assertNotNull(deskRes);
        assertEquals("G01", deskRes.getGuestId(), "Must save guest ID, not staff member's ID");
        assertEquals(BookingType.DESK, deskRes.getBookingType());
        assertTrue(deskRes instanceof DeskReservation);
    }

    // ==========================================
    // 6. Security & IDOR Cancellation Tests
    // ==========================================

    @Test
    void testGuestCannotCancelAnotherGuestsBooking() {
        LocalDate in = LocalDate.now().plusDays(5);
        LocalDate out = in.plusDays(7);

        Reservation res = reservationService.createOnlineBooking(regularGuest, "R101", in, out, 2);

        assertThrows(SecurityException.class, () ->
                reservationService.cancelByGuest(vipGuest, res.getId())
        );
    }

    @Test
    void testGuestCanCancelTheirOwnUpcomingOnlineBooking() {
        LocalDate in = LocalDate.now().plusDays(5);
        LocalDate out = in.plusDays(7);

        Reservation res = reservationService.createOnlineBooking(regularGuest, "R101", in, out, 2);
        reservationService.cancelByGuest(regularGuest, res.getId());

        Reservation reloaded = reservationService.findById(res.getId());
        assertEquals(BookingStatus.CANCELLED, reloaded.getStatus());
    }

    @Test
    void testStaffCanCancelAnyActiveBooking() {
        LocalDate in = LocalDate.now().plusDays(5);
        LocalDate out = in.plusDays(7);

        Reservation res = reservationService.createOnlineBooking(regularGuest, "R101", in, out, 2);
        reservationService.cancelByStaff(adminUser, res.getId());

        Reservation reloaded = reservationService.findById(res.getId());
        assertEquals(BookingStatus.CANCELLED, reloaded.getStatus());
    }

    // ==========================================
    // 7. Check-in Flow Test
    // ==========================================

    @Test
    void testCheckInGuestByStaff() {
        LocalDate in = LocalDate.now();
        LocalDate out = in.plusDays(2);

        Reservation res = reservationService.createOnlineBooking(regularGuest, "R101", in, out, 2);
        assertFalse(res.isCheckedIn());

        reservationService.checkInGuest(staffUser, res.getId());
        Reservation reloaded = reservationService.findById(res.getId());
        assertTrue(reloaded.isCheckedIn());
    }

    @Test
    void testCheckInBeforeArrivalDateThrowsException() {
        LocalDate in = LocalDate.now().plusDays(14);
        LocalDate out = in.plusDays(16);

        Reservation res = reservationService.createOnlineBooking(regularGuest, "R101", in, out, 2);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                reservationService.checkInGuest(staffUser, res.getId())
        );
        assertEquals("Check-in opens on the arrival date.", ex.getMessage());
    }

    // ==========================================
    // 8. User-Friendly Date Parsing
    // ==========================================

    @Test
    void testFriendlyDateParseError() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                ReservationService.parseDate("invalid-date", "Check-in")
        );
        assertEquals("Check-in date is not valid. Please use the date picker.", ex.getMessage());
    }
}
