package com.hotel.reservation.servlet;

import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.service.ReservationService;
import com.hotel.user.User;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@WebServlet("/reservation/confirmation")
public class BookingConfirmationServlet extends HttpServlet {

    @Autowired
    private ReservationService reservationService;

    public void setReservationService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = SessionUtil.getCurrentUser(request.getSession(false));
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String id = request.getParameter("id");
        if (id == null || id.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/bookings");
            return;
        }

        try {
            Reservation reservation = reservationService.getBookingById(user, id.trim());
            if (reservation == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Booking not found.");
                return;
            }

            request.setAttribute("booking", reservation);
            request.getRequestDispatcher("/WEB-INF/views/reservation/confirmation.jsp").forward(request, response);

        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }
}
