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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller for guest booking cancellation (POST /bookings/cancel).
 * Enforces ownership check (IDOR protection) and cancellation policy.
 */
@WebServlet(urlPatterns = {"/bookings/cancel", "/reservation/cancel", "/reservations/cancel"})
public class CancelBookingServlet extends HttpServlet {

    @Autowired
    private ReservationService reservationService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String bookingId = request.getParameter("bookingId");
        try {
            reservationService.cancelByGuest(currentUser, bookingId);
            response.sendRedirect(request.getContextPath() + "/bookings?done=cancelled");
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            String msg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/bookings?error=" + msg);
        }
    }
}
