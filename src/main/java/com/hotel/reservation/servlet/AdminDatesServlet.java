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
import java.time.LocalDate;

/**
 * Controller for staff modifying reservation dates (POST /admin/bookings/dates).
 * Allows modifying dates as long as room is available and booking is unpaid/not checked in.
 */
@WebServlet("/admin/bookings/dates")
public class AdminDatesServlet extends HttpServlet {

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
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");

        try {
            LocalDate checkIn = ReservationService.parseDate(checkInStr, "Check-in");
            LocalDate checkOut = ReservationService.parseDate(checkOutStr, "Check-out");

            reservationService.changeDatesByStaff(currentUser, bookingId, checkIn, checkOut);
            response.sendRedirect(request.getContextPath() + "/admin/bookings?done=dates");
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            String msg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/admin/bookings?error=" + msg);
        }
    }
}
