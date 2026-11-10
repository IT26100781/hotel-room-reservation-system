package com.hotel.reservation.servlet;

import com.hotel.reservation.service.ReservationService;
import com.hotel.room.model.Room;
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
 * Controller for room availability search (GET /reservation/search).
 * Checks dates and filters by capacity and room category.
 */
@WebServlet("/reservation/search")
public class RoomSearchServlet extends HttpServlet {

    @Autowired
    private ReservationService reservationService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("minDate", LocalDate.now().toString());

        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");
        String guestsStr = request.getParameter("guests");
        if (guestsStr == null || guestsStr.isBlank()) {
            guestsStr = request.getParameter("capacity");
        }
        String roomType = request.getParameter("type");

        int party = 2;
        if (guestsStr != null && !guestsStr.isBlank()) {
            try {
                party = Integer.parseInt(guestsStr.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        request.setAttribute("party", party);

        // Only search when check-in and check-out dates are supplied
        if (checkInStr != null && !checkInStr.isBlank() && checkOutStr != null && !checkOutStr.isBlank()) {
            try {
                LocalDate checkIn = ReservationService.parseDate(checkInStr, "Check-in");
                LocalDate checkOut = ReservationService.parseDate(checkOutStr, "Check-out");

                if (checkIn.isBefore(LocalDate.now())) {
                    request.setAttribute("error", "Check-in date cannot be in the past.");
                } else {
                    List<Room> freeRooms = reservationService.searchAvailableRooms(checkIn, checkOut, party, roomType);
                    request.setAttribute("rooms", freeRooms);
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                request.setAttribute("error", e.getMessage());
            } catch (Exception e) {
                request.setAttribute("error", "Unable to complete search. Please check your dates.");
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/reservation/search.jsp").forward(request, response);
    }
}
