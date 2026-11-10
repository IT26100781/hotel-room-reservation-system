package com.hotel.reservation.servlet;

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

@WebServlet("/reservations/cancel")
public class CancelBookingServlet extends HttpServlet {

    @Autowired
    private ReservationService reservationService;

    public void setReservationService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = SessionUtil.getCurrentUser(request.getSession(false));
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String bookingId = request.getParameter("bookingId");

        try {
            if (bookingId == null || bookingId.isBlank()) {
                throw new IllegalArgumentException("Booking identifier is missing.");
            }

            reservationService.cancelBooking(user, bookingId.trim());

            if (user.canManageAnyBooking()) {
                response.sendRedirect(request.getContextPath() + "/admin/bookings?done=cancelled");
            } else {
                response.sendRedirect(request.getContextPath() + "/bookings?done=cancelled");
            }

        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            request.getSession().setAttribute("flashError", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/bookings");
        }
    }
}
