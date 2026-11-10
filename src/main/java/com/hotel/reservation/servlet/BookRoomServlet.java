package com.hotel.reservation.servlet;

import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.service.ReservationService;
import com.hotel.room.model.Room;
import com.hotel.room.service.RoomService;
import com.hotel.user.User;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/reservation/book")
public class BookRoomServlet extends HttpServlet {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomService roomService;

    public void setReservationService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = SessionUtil.getCurrentUser(request.getSession(false));
        if (user == null) {
            String roomId = request.getParameter("roomId");
            response.sendRedirect(request.getContextPath() + "/login?returnUrl=" 
                    + request.getContextPath() + "/reservation/book?roomId=" + (roomId != null ? roomId : ""));
            return;
        }

        String roomId = request.getParameter("roomId");
        if (roomId != null && !roomId.isBlank()) {
            Room room = roomService.getRoomById(roomId.trim());
            request.setAttribute("room", room);
        }

        request.getRequestDispatcher("/WEB-INF/views/reservation/book.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = SessionUtil.getCurrentUser(request.getSession(false));
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String roomId = request.getParameter("roomId");
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");
        String guestsStr = request.getParameter("guests");

        try {
            if (checkInStr == null || checkInStr.isBlank() || checkOutStr == null || checkOutStr.isBlank()) {
                throw new IllegalArgumentException("Check-in and check-out dates are required.");
            }

            LocalDate checkIn = LocalDate.parse(checkInStr.trim());
            LocalDate checkOut = LocalDate.parse(checkOutStr.trim());
            int guests = (guestsStr != null && !guestsStr.isBlank()) ? Integer.parseInt(guestsStr.trim()) : 1;

            Reservation reservation = reservationService.createBooking(user, roomId, checkIn, checkOut, guests);

            // Post/Redirect/Get pattern
            response.sendRedirect(request.getContextPath() + "/reservation/confirmation?id=" + reservation.getId());

        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            if (roomId != null && !roomId.isBlank()) {
                request.setAttribute("room", roomService.getRoomById(roomId.trim()));
            }
            request.getRequestDispatcher("/WEB-INF/views/reservation/book.jsp").forward(request, response);
        }
    }
}
