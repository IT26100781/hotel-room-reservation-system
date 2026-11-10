package com.hotel.reservation.servlet;

import com.hotel.reservation.service.ReservationService;
import com.hotel.room.model.Room;
import com.hotel.room.service.RoomService;
import com.hotel.user.User;
import com.hotel.user.guest.model.Guest;
import com.hotel.user.guest.service.GuestService;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for creating front-desk bookings for guests (GET/POST /admin/bookings/new).
 * Allows staff to select an existing guest and create a DESK reservation.
 */
@WebServlet("/admin/bookings/new")
public class BookingNewServlet extends HttpServlet {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private GuestService guestService;

    @Autowired
    private RoomService roomService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (currentUser == null || !currentUser.canManageAnyBooking()) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        populateFormAttributes(request, currentUser);
        request.getRequestDispatcher("/WEB-INF/views/reservation/new.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (currentUser == null || !currentUser.canManageAnyBooking()) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        String guestId = request.getParameter("guestId");
        String roomId = request.getParameter("roomId");
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");
        String guestsStr = request.getParameter("guests");

        try {
            LocalDate checkIn = ReservationService.parseDate(checkInStr, "Check-in");
            LocalDate checkOut = ReservationService.parseDate(checkOutStr, "Check-out");
            int guests = 1;
            if (guestsStr != null && !guestsStr.isBlank()) {
                guests = Integer.parseInt(guestsStr.trim());
            }

            reservationService.createDeskBooking(currentUser, guestId, roomId, checkIn, checkOut, guests);
            response.sendRedirect(request.getContextPath() + "/admin/bookings?done=created");
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            request.setAttribute("error", e.getMessage());
            populateFormAttributes(request, currentUser);
            request.getRequestDispatcher("/WEB-INF/views/reservation/new.jsp").forward(request, response);
        }
    }

    private void populateFormAttributes(HttpServletRequest request, User currentUser) {
        List<Guest> guestList = guestService.listGuests(currentUser);
        List<Room> rooms = roomService.getAllRooms().stream().filter(Room::isActive).toList();
        request.setAttribute("guestList", guestList);
        request.setAttribute("rooms", rooms);
        request.setAttribute("minDate", LocalDate.now().toString());
    }
}
