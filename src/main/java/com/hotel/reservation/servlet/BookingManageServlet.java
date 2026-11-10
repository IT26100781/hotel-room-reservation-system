package com.hotel.reservation.servlet;

import com.hotel.reservation.model.BookingDetails;
import com.hotel.reservation.model.BookingMetrics;
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
import java.util.List;

/**
 * Controller for staff booking management and check-in overview (GET /admin/bookings).
 * Protected by StaffAuthFilter.
 */
@WebServlet("/admin/bookings")
public class BookingManageServlet extends HttpServlet {

    @Autowired
    private ReservationService reservationService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (currentUser == null || !currentUser.canManageAnyBooking()) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        List<Reservation> allReservations = reservationService.findAll();
        List<BookingDetails> detailsList = reservationService.toDetails(allReservations, currentUser);
        BookingMetrics metrics = reservationService.getMetrics(detailsList);

        request.setAttribute("bookings", detailsList);
        request.setAttribute("metrics", metrics);

        request.getRequestDispatcher("/WEB-INF/views/reservation/manage.jsp").forward(request, response);
    }
}
