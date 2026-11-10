package com.hotel.reservation.service;

import com.hotel.reservation.dao.ReservationDAO;
import com.hotel.reservation.model.BookingStatus;
import com.hotel.reservation.model.DateRange;
import com.hotel.reservation.model.PaymentStatus;
import com.hotel.reservation.model.Reservation;
import com.hotel.room.model.Room;
import com.hotel.room.service.RoomService;
import com.hotel.user.User;
import com.hotel.user.guest.model.Guest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationDAO reservationDAO;
    private final RoomService roomService;

    public ReservationService(ReservationDAO reservationDAO, RoomService roomService) {
        this.reservationDAO = reservationDAO;
        this.roomService = roomService;
    }

    /**
     * Generates a thread-safe identifier matching the BK01 / BK1001 project convention.
     */
    public synchronized String nextId() {
        int largest = 0;
        for (Reservation r : reservationDAO.findAll()) {
            if (r.getId() != null && r.getId().matches("BK[0-9]+")) {
                try {
                    largest = Math.max(largest, Integer.parseInt(r.getId().substring(2)));
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("BK%02d", largest + 1);
    }

    /**
     * Core business method to create a booking.
     * Enforces permission checks, active room status, room occupancy, date intervals,
     * double-booking prevention, and polymorphic pricing.
     */
    @Transactional
    public synchronized Reservation createBooking(User currentUser, String roomId, LocalDate checkIn, LocalDate checkOut, int guestCount) {
        if (currentUser == null) {
            throw new SecurityException("You must be logged in to make a reservation.");
        }
        if (!currentUser.canBookForThemselves() && !currentUser.canManageAnyBooking()) {
            throw new SecurityException("Your account is not authorized to create bookings.");
        }

        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("Room selection is required.");
        }

        DateRange dateRange = new DateRange(checkIn, checkOut);
        if (dateRange.getCheckIn().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }

        Room room = roomService.getRoomById(roomId.trim());
        if (room == null || !room.isActive()) {
            throw new IllegalArgumentException("Selected room is currently unavailable or inactive.");
        }

        if (guestCount < 1 || guestCount > room.getCapacity()) {
            throw new IllegalArgumentException("Guest count exceeds the room capacity of " + room.getCapacity() + ".");
        }

        // Prevent Double-Booking Race Condition
        if (reservationDAO.hasOverlap(room.getRoomId(), checkIn, checkOut)) {
            throw new IllegalStateException("Room " + room.getRoomNumber() + " is already booked for these dates.");
        }

        // Polymorphic stay pricing calculation
        long nights = dateRange.getNights();
        double baseTotal = room.calculateTotalRate((int) nights);

        // Polymorphic guest discount (RegularGuest: 0%, VipGuest: 10%)
        double discountRate = (currentUser instanceof Guest guest) ? guest.getDiscount() : 0.0;
        double finalAmount = Math.round((baseTotal * (1.0 - discountRate)) * 100.0) / 100.0;

        Reservation reservation = new Reservation.Builder()
                .id(nextId())
                .guestId(currentUser.getId())
                .roomId(room.getRoomId())
                .dateRange(dateRange)
                .guests(guestCount)
                .totalAmount(finalAmount)
                .status(BookingStatus.ACTIVE)
                .paymentStatus(PaymentStatus.PENDING)
                .checkedIn(false)
                .build();

        if (!reservationDAO.save(reservation)) {
            throw new IllegalStateException("Could not save the reservation. Please try again.");
        }

        return reservation;
    }

    public Reservation getBookingById(User currentUser, String bookingId) {
        if (currentUser == null) {
            throw new SecurityException("Please log in to view booking details.");
        }
        Reservation r = reservationDAO.findById(bookingId);
        if (r == null) {
            return null;
        }

        // IDOR ownership check
        if (!currentUser.canManageAnyBooking() && !r.getGuestId().equals(currentUser.getId())) {
            throw new SecurityException("You do not have permission to view this booking.");
        }

        enrichWithRoomDetails(r);
        return r;
    }

    @Transactional
    public void cancelBooking(User currentUser, String bookingId) {
        if (currentUser == null) {
            throw new SecurityException("Authentication required.");
        }
        Reservation reservation = reservationDAO.findById(bookingId);
        if (reservation == null) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }

        if (!reservation.canBeCancelledBy(currentUser)) {
            throw new SecurityException("You are not authorized to cancel this booking.");
        }

        reservation.cancel();
        if (!reservationDAO.updateStatus(bookingId, BookingStatus.CANCELLED)) {
            throw new IllegalStateException("Failed to cancel booking " + bookingId);
        }
    }

    @Transactional
    public void checkInGuest(User currentUser, String bookingId) {
        if (currentUser == null || !currentUser.canCheckInGuests()) {
            throw new SecurityException("Only front desk staff can check in guests.");
        }
        Reservation reservation = reservationDAO.findById(bookingId);
        if (reservation == null) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }
        reservation.markCheckedIn();
        reservationDAO.updateCheckedIn(bookingId, true);
    }

    @Transactional
    public void updatePaymentStatus(String bookingId, PaymentStatus status) {
        reservationDAO.updatePaymentStatus(bookingId, status);
    }

    // ---- Downstream Integration APIs ----

    /**
     * Used by the Payment module to list active bookings requiring payment.
     */
    public List<Reservation> getPendingPaymentBookings() {
        List<Reservation> list = new ArrayList<>();
        for (Reservation r : reservationDAO.findAll()) {
            if (r.isActive() && r.isPendingPayment()) {
                enrichWithRoomDetails(r);
                list.add(r);
            }
        }
        return list;
    }

    /**
     * Used by the Review/Feedback module to ensure verified reviews.
     */
    public boolean hasCompletedStay(String guestId, String bookingId) {
        Reservation r = reservationDAO.findById(bookingId);
        if (r == null || !r.getGuestId().equals(guestId)) {
            return false;
        }
        return r.isCompleted(LocalDate.now());
    }

    public List<Reservation> getGuestBookings(User currentUser, String guestId) {
        if (currentUser == null) {
            throw new SecurityException("Authentication required.");
        }
        if (!currentUser.canManageAnyBooking() && !currentUser.getId().equals(guestId)) {
            throw new SecurityException("Cannot view another guest's bookings.");
        }
        List<Reservation> list = reservationDAO.findByGuestId(guestId);
        list.forEach(this::enrichWithRoomDetails);
        return list;
    }

    public List<Reservation> getAllBookings(User currentUser) {
        if (currentUser == null || !currentUser.canManageAnyBooking()) {
            throw new SecurityException("Access denied: Staff permission required.");
        }
        List<Reservation> list = reservationDAO.findAll();
        list.forEach(this::enrichWithRoomDetails);
        return list;
    }

    public int getArrivalsTodayCount() {
        LocalDate today = LocalDate.now();
        int count = 0;
        for (Reservation r : reservationDAO.findAll()) {
            if (r.isArrivingToday(today)) count++;
        }
        return count;
    }

    public int getPendingBookingsCount() {
        return getPendingPaymentBookings().size();
    }

    private void enrichWithRoomDetails(Reservation r) {
        Room room = roomService.getRoomById(r.getRoomId());
        if (room != null) {
            r.setRoomName(room.getRoomName());
            r.setRoomNumber(room.getRoomNumber());
        }
    }
}
