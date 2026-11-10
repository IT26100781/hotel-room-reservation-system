// src/main/java/com/hotel/reservation/service/ReservationService.java
package com.hotel.reservation.service;

import com.hotel.reservation.dao.ReservationDAO;
import com.hotel.reservation.model.*;
import com.hotel.room.model.Room;
import com.hotel.room.service.RoomService;
import com.hotel.user.User;
import com.hotel.user.guest.model.Guest;
import com.hotel.user.guest.service.GuestService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Business-logic layer for the reservation module.
 *
 * Responsibilities:
 * <ul>
 *   <li>Thread-safe BK-prefix ID generation (BK01, BK02 …)</li>
 *   <li>Availability checking (overlap detection)</li>
 *   <li>Capacity and room-status validation</li>
 *   <li>Dynamic pricing: {@code room.calculateTotalRate(nights) × (1 - guest.getDiscount())}</li>
 *   <li>IDOR protection on cancel / view / date changes</li>
 *   <li>Cross-module queries for Payment and Review</li>
 * </ul>
 */
@Service
public class ReservationService {

    private final ReservationDAO reservationDAO;
    private final RoomService roomService;
    private final GuestService guestService;

    /** Guard for thread-safe sequential ID generation. */
    private final Object idLock = new Object();

    public ReservationService(ReservationDAO reservationDAO,
                              RoomService roomService,
                              GuestService guestService) {
        this.reservationDAO = reservationDAO;
        this.roomService = roomService;
        this.guestService = guestService;
    }

    // =====================================================================
    //  ID generation
    // =====================================================================

    /**
     * Generates the next booking ID in the sequence BK01, BK02 … BK100 etc.
     * Thread-safe via synchronized block on {@code idLock}.
     */
    private String nextId() {
        synchronized (idLock) {
            int next = reservationDAO.findMaxIdNumber() + 1;
            return String.format("BK%02d", next);
        }
    }

    // =====================================================================
    //  Date parsing (friendly error messages)
    // =====================================================================

    /**
     * Parses a date string (yyyy-MM-dd) with a user-friendly error message
     * instead of exposing raw Java parse exceptions.
     *
     * @param value the date string from the form
     * @param field human label like "Check-in" or "Check-out"
     * @return the parsed LocalDate
     * @throws IllegalArgumentException with a friendly message on parse failure
     */
    public static LocalDate parseDate(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " date is required.");
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(field + " date is not valid. Please use the date picker.");
        }
    }

    // =====================================================================
    //  Online booking (guest self-service)
    // =====================================================================

    /**
     * Creates an ONLINE booking for the currently logged-in guest.
     *
     * Validates:
     * <ol>
     *   <li>User is a Guest and can book for themselves</li>
     *   <li>Room exists and is ACTIVE</li>
     *   <li>Guest count ≤ room capacity</li>
     *   <li>No overlapping active booking for the room</li>
     * </ol>
     *
     * @return the saved Reservation with its generated ID
     */
    public synchronized Reservation createOnlineBooking(User currentUser,
                                                        String roomId,
                                                        LocalDate checkIn,
                                                        LocalDate checkOut,
                                                        int guestCount) {
        // -- auth: only guests can self-book --
        if (currentUser == null || !currentUser.canBookForThemselves()) {
            throw new SecurityException("Please sign in as a guest to make a booking.");
        }
        if (!(currentUser instanceof Guest)) {
            throw new SecurityException("Only registered guests can book online.");
        }

        // -- validate room --
        Room room = requireActiveRoom(roomId);
        validateCapacity(room, guestCount);

        // -- validate dates and availability --
        new DateRange(checkIn, checkOut); // validates checkOut > checkIn
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }
        if (reservationDAO.hasOverlap(roomId, checkIn, checkOut)) {
            throw new IllegalArgumentException("This room is not available for the selected dates.");
        }

        // -- build and save --
        Reservation r = Reservation.builder()
                .online()
                .guestId(currentUser.getId())
                .roomId(roomId)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .guests(guestCount)
                .build();
        r.setId(nextId());

        if (!reservationDAO.save(r)) {
            throw new IllegalStateException("Unable to save the booking. Please try again.");
        }
        return r;
    }

    // =====================================================================
    //  Desk booking (staff creates for a guest)
    // =====================================================================

    /**
     * Creates a DESK booking — staff picks the guest from a dropdown.
     * The {@code guestId} parameter is the guest's ID, NOT the staff's.
     *
     * @param currentUser the logged-in staff member
     * @param guestId     the guest the booking is for
     */
    public synchronized Reservation createDeskBooking(User currentUser,
                                                      String guestId,
                                                      String roomId,
                                                      LocalDate checkIn,
                                                      LocalDate checkOut,
                                                      int guestCount) {
        // -- auth: only staff can create desk bookings --
        if (currentUser == null || !currentUser.canManageAnyBooking()) {
            throw new SecurityException("Only staff can create desk bookings.");
        }

        // -- validate guest exists --
        Guest guest = guestService.findGuestById(currentUser, guestId);
        if (guest == null) {
            throw new IllegalArgumentException("Guest not found.");
        }
        if (!guest.isActive()) {
            throw new IllegalArgumentException("This guest account is inactive.");
        }

        // -- validate room --
        Room room = requireActiveRoom(roomId);
        validateCapacity(room, guestCount);

        // -- validate dates and availability --
        new DateRange(checkIn, checkOut);
        if (reservationDAO.hasOverlap(roomId, checkIn, checkOut)) {
            throw new IllegalArgumentException("This room is not available for the selected dates.");
        }

        // -- build and save --
        Reservation r = Reservation.builder()
                .desk()
                .guestId(guestId)       // the GUEST's id, not the staff's
                .roomId(roomId)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .guests(guestCount)
                .build();
        r.setId(nextId());

        if (!reservationDAO.save(r)) {
            throw new IllegalStateException("Unable to save the booking. Please try again.");
        }
        return r;
    }

    // =====================================================================
    //  Quote (price check without saving)
    // =====================================================================

    /**
     * Builds a {@link BookingDetails} quote for the book.jsp "Check price" flow.
     * Does NOT persist anything.
     */
    public BookingDetails quote(User currentUser, String roomId,
                                LocalDate checkIn, LocalDate checkOut, int guestCount) {
        if (!(currentUser instanceof Guest guest)) {
            throw new SecurityException("Please sign in as a guest.");
        }
        Room room = requireActiveRoom(roomId);
        validateCapacity(room, guestCount);
        new DateRange(checkIn, checkOut);

        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }
        if (reservationDAO.hasOverlap(roomId, checkIn, checkOut)) {
            throw new IllegalArgumentException("This room is not available for the selected dates.");
        }

        // Build a transient reservation for the quote (no ID, not saved)
        Reservation r = Reservation.builder()
                .online()
                .guestId(guest.getId())
                .roomId(roomId)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .guests(guestCount)
                .build();
        return new BookingDetails(r, room, guest);
    }

    // =====================================================================
    //  Cancellation
    // =====================================================================

    /**
     * Guest cancels their own online booking (IDOR-protected).
     */
    public void cancelByGuest(User currentUser, String bookingId) {
        if (currentUser == null || !currentUser.canBookForThemselves()) {
            throw new SecurityException("Please sign in to cancel your booking.");
        }
        Reservation r = requireBooking(bookingId);
        if (!r.isOwnedBy(currentUser)) {
            throw new SecurityException("You can only cancel your own bookings.");
        }
        if (!r.isGuestCancellable()) {
            throw new IllegalStateException("This booking cannot be cancelled. " + r.getCancellationPolicy());
        }
        reservationDAO.cancel(bookingId);
    }

    /**
     * Staff cancels any booking.
     */
    public void cancelByStaff(User currentUser, String bookingId) {
        if (currentUser == null || !currentUser.canManageAnyBooking()) {
            throw new SecurityException("Only staff can cancel bookings from the admin panel.");
        }
        Reservation r = requireBooking(bookingId);
        if (!r.isStaffCancellable()) {
            throw new IllegalStateException("This booking cannot be cancelled (already cancelled or checked in).");
        }
        reservationDAO.cancel(bookingId);
    }

    // =====================================================================
    //  Check-in
    // =====================================================================

    /**
     * Staff checks a guest in.
     */
    public void checkInGuest(User currentUser, String bookingId) {
        if (currentUser == null || !currentUser.canCheckInGuests()) {
            throw new SecurityException("Only staff can check in guests.");
        }
        Reservation r = requireBooking(bookingId);
        if (r.getStatus() != BookingStatus.ACTIVE) {
            throw new IllegalStateException("Cannot check in a cancelled booking.");
        }
        if (r.isCheckedIn()) {
            throw new IllegalStateException("Guest is already checked in.");
        }
        reservationDAO.checkIn(bookingId);
    }

    // =====================================================================
    //  Date changes
    // =====================================================================

    /**
     * Guest changes their own booking dates (IDOR-protected).
     */
    public void changeDatesByGuest(User currentUser, String bookingId,
                                   LocalDate newCheckIn, LocalDate newCheckOut) {
        if (currentUser == null) {
            throw new SecurityException("Please sign in.");
        }
        Reservation r = requireBooking(bookingId);
        if (!r.isOwnedBy(currentUser)) {
            throw new SecurityException("You can only change your own bookings.");
        }
        if (!r.isGuestDatesEditable()) {
            throw new IllegalStateException("Dates cannot be changed for this booking.");
        }
        changeDates(r, newCheckIn, newCheckOut);
    }

    /**
     * Staff changes dates on any booking.
     */
    public void changeDatesByStaff(User currentUser, String bookingId,
                                   LocalDate newCheckIn, LocalDate newCheckOut) {
        if (currentUser == null || !currentUser.canManageAnyBooking()) {
            throw new SecurityException("Only staff can change booking dates.");
        }
        Reservation r = requireBooking(bookingId);
        if (!r.isStaffDatesEditable()) {
            throw new IllegalStateException("Dates cannot be changed for this booking.");
        }
        changeDates(r, newCheckIn, newCheckOut);
    }

    /** Shared date-change logic with overlap re-check. */
    private void changeDates(Reservation r, LocalDate newCheckIn, LocalDate newCheckOut) {
        new DateRange(newCheckIn, newCheckOut); // validates
        if (reservationDAO.hasOverlap(r.getRoomId(), newCheckIn, newCheckOut, r.getId())) {
            throw new IllegalArgumentException("The room is not available for the new dates.");
        }
        reservationDAO.updateDates(r.getId(), newCheckIn, newCheckOut);
    }

    // =====================================================================
    //  Query helpers
    // =====================================================================

    /** Find a single booking by ID, or null. */
    public Reservation findById(String id) {
        return reservationDAO.findById(id);
    }

    /** All bookings for a guest. */
    public List<Reservation> findByGuestId(String guestId) {
        return reservationDAO.findByGuestId(guestId);
    }

    /** All bookings in the system (for staff manage page). */
    public List<Reservation> findAll() {
        return reservationDAO.findAll();
    }

    /**
     * Wraps a list of reservations into BookingDetails, loading the Room and Guest for each.
     * Skips any booking whose room or guest has been deleted.
     */
    public List<BookingDetails> toDetails(List<Reservation> reservations, User viewer) {
        List<BookingDetails> list = new ArrayList<>();
        for (Reservation r : reservations) {
            Room room = roomService.getRoomById(r.getRoomId());
            if (room == null) continue;
            Guest guest;
            try {
                guest = guestService.findGuestById(viewer, r.getGuestId());
            } catch (SecurityException e) {
                // viewer doesn't have permission — skip or use a placeholder
                continue;
            }
            if (guest == null) continue;
            list.add(new BookingDetails(r, room, guest));
        }
        return list;
    }

    /**
     * Wraps a single reservation into BookingDetails.
     */
    public BookingDetails toDetails(Reservation r, User viewer) {
        Room room = roomService.getRoomById(r.getRoomId());
        Guest guest;
        try {
            guest = guestService.findGuestById(viewer, r.getGuestId());
        } catch (SecurityException e) {
            // If the viewer is the guest themselves, findGuestById should work via owns()
            return null;
        }
        if (room == null || guest == null) return null;
        return new BookingDetails(r, room, guest);
    }

    // =====================================================================
    //  Dashboard metrics (manage.jsp)
    // =====================================================================

    /**
     * Computes dashboard statistics in a single pass over all bookings.
     */
    public BookingMetrics getMetrics(List<BookingDetails> allDetails) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);

        int arrivingToday = 0, checkedInToday = 0, inHouse = 0,
            paymentPending = 0, noShowsThisWeek = 0;

        for (BookingDetails bd : allDetails) {
            Reservation r = bd.getReservation();
            String state = r.getState().getValue();

            switch (state) {
                case "arriving" -> {
                    arrivingToday++;
                    if (r.getPaymentStatus() == PaymentStatus.PENDING) paymentPending++;
                }
                case "inhouse" -> {
                    inHouse++;
                    if (r.getCheckIn().equals(today)) checkedInToday++;
                    if (r.getPaymentStatus() == PaymentStatus.PENDING) paymentPending++;
                }
                case "upcoming" -> {
                    if (r.getPaymentStatus() == PaymentStatus.PENDING) paymentPending++;
                }
                case "noshow" -> {
                    if (!r.getCheckIn().isBefore(weekStart)) noShowsThisWeek++;
                }
            }
        }
        return new BookingMetrics(arrivingToday, checkedInToday, inHouse,
                paymentPending, noShowsThisWeek);
    }

    // =====================================================================
    //  Room search (availability)
    // =====================================================================

    /**
     * Returns all active rooms that are free for the given dates,
     * optionally filtered by capacity and room type.
     */
    public List<Room> searchAvailableRooms(LocalDate checkIn, LocalDate checkOut,
                                           int minCapacity, String roomType) {
        new DateRange(checkIn, checkOut); // validates
        List<String> freeIds = reservationDAO.findFreeRoomIds(checkIn, checkOut);
        List<Room> allRooms = roomService.getAllRooms();
        List<Room> results = new ArrayList<>();

        for (Room room : allRooms) {
            if (!room.isActive()) continue;
            if (!freeIds.contains(room.getRoomId())) continue;
            if (room.getCapacity() < minCapacity) continue;
            if (roomType != null && !roomType.isBlank()) {
                String type = room.getTypeDisplayName().toLowerCase();
                if (!type.equalsIgnoreCase(roomType.trim())) continue;
            }
            results.add(room);
        }
        return results;
    }

    // =====================================================================
    //  Cross-module APIs
    // =====================================================================

    /** For Payment module: all active bookings with pending payment. */
    public List<Reservation> getPendingPaymentBookings() {
        return reservationDAO.findPendingPayment();
    }

    /** For Review module: has the guest completed a stay for this booking? */
    public boolean hasCompletedStay(String guestId, String bookingId) {
        return reservationDAO.hasCompletedStay(guestId, bookingId);
    }

    // =====================================================================
    //  Private helpers
    // =====================================================================

    /** Loads a room and checks it is ACTIVE. */
    private Room requireActiveRoom(String roomId) {
        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room ID is required.");
        }
        Room room = roomService.getRoomById(roomId.trim());
        if (room == null) {
            throw new IllegalArgumentException("Room not found.");
        }
        if (!room.isActive()) {
            throw new IllegalArgumentException("This room is not available for booking.");
        }
        return room;
    }

    /** Validates guest count against room capacity. */
    private void validateCapacity(Room room, int guestCount) {
        if (guestCount < 1) {
            throw new IllegalArgumentException("At least 1 guest is required.");
        }
        if (guestCount > room.getCapacity()) {
            throw new IllegalArgumentException(
                    "This room holds up to " + room.getCapacity() + " guests.");
        }
    }

    /** Loads a booking or throws. */
    private Reservation requireBooking(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Booking ID is required.");
        }
        Reservation r = reservationDAO.findById(id.trim());
        if (r == null) {
            throw new IllegalArgumentException("Booking not found.");
        }
        return r;
    }
}
