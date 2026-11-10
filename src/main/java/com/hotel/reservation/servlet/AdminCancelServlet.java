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
 * Controller for staff booking cancellation (POST /admin/bookings/cancel).
 * Releases the room while keeping record for audit/payment reconciliation.
 */
@WebServlet("/admin/bookings/cancel")
public class AdminCancelServlet extends HttpServlet {

    @Autowired
    private ReservationService reservationService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (currentUser == null || !currentUser.canManageAnyBooking()) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        String bookingId = request.getParameter("bookingId");
        try {
            reservationService.cancelByStaff(currentUser, bookingId);
            response.sendRedirect(request.getContextPath() + "/admin/bookings?done=cancelled");
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            String msg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/admin/bookings?error=" + msg);
        }
    }
}
