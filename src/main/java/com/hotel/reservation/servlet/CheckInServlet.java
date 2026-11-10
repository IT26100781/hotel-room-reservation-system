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
 * Controller for staff checking in a guest (POST /admin/bookings/checkin).
 * Supports direct transition to payment recording if requested.
 */
@WebServlet("/admin/bookings/checkin")
public class CheckInServlet extends HttpServlet {

    @Autowired
    private ReservationService reservationService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (currentUser == null || !currentUser.canCheckInGuests()) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        String bookingId = request.getParameter("bookingId");
        String next = request.getParameter("next");

        try {
            reservationService.checkInGuest(currentUser, bookingId);

            if ("payment".equalsIgnoreCase(next)) {
                response.sendRedirect(request.getContextPath() + "/admin/payments/record?bookingId=" + bookingId);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/bookings?done=checkedin");
            }
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            String msg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/admin/bookings?error=" + msg);
        }
    }
}
