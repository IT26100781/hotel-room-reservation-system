package com.hotel.reservation.servlet;

import com.hotel.reservation.model.BookingDetails;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.service.ReservationService;
import com.hotel.room.model.Room;
import com.hotel.room.service.RoomService;
import com.hotel.user.User;
import com.hotel.user.guest.model.Guest;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Controller for online guest booking flow:
 * GET  /reservation/book -> Displays the booking form and handles the "Check price" quote preview.
 * POST /reservation/book -> Confirms and saves the online reservation, redirecting to confirmation.
 */
@WebServlet("/reservation/book")
public class BookRoomServlet extends HttpServlet {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomService roomService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (!(currentUser instanceof Guest)) {
            if (currentUser != null && currentUser.canManageAnyBooking()) {
                // If staff member accidentally visits guest booking, guide them to desk booking
                response.sendRedirect(request.getContextPath() + "/admin/bookings/new");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String roomId = request.getParameter("roomId");
        if (roomId == null || roomId.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/reservation/search");
            return;
        }

        Room room = roomService.getRoomById(roomId.trim());
        if (room == null || !room.isActive()) {
            response.sendRedirect(request.getContextPath() + "/reservation/search?error=Room+not+available");
            return;
        }

        request.setAttribute("room", room);
        request.setAttribute("minDate", LocalDate.now().toString());

        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");
        String guestsStr = request.getParameter("guests");

        // "Check price" flow: if dates are submitted via GET, compute a quote
        if (checkInStr != null && !checkInStr.isBlank() && checkOutStr != null && !checkOutStr.isBlank()) {
            try {
                LocalDate checkIn = ReservationService.parseDate(checkInStr, "Check-in");
                LocalDate checkOut = ReservationService.parseDate(checkOutStr, "Check-out");
                int guests = 1;
                if (guestsStr != null && !guestsStr.isBlank()) {
                    guests = Integer.parseInt(guestsStr.trim());
                }
                BookingDetails quote = reservationService.quote(currentUser, roomId, checkIn, checkOut, guests);
                request.setAttribute("quote", quote);
            } catch (IllegalArgumentException | IllegalStateException e) {
                request.setAttribute("error", e.getMessage());
            } catch (Exception e) {
                request.setAttribute("error", "Unable to calculate quote. Please check your dates.");
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/reservation/book.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (!(currentUser instanceof Guest)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

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

            Reservation reservation = reservationService.createOnlineBooking(currentUser, roomId, checkIn, checkOut, guests);
            response.sendRedirect(request.getContextPath() + "/reservation/confirmation?id=" + reservation.getId());
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            Room room = roomService.getRoomById(roomId != null ? roomId.trim() : "");
            request.setAttribute("room", room);
            request.setAttribute("minDate", LocalDate.now().toString());
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/reservation/book.jsp").forward(request, response);
        }
    }
}
