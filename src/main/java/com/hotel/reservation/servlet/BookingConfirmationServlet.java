package com.hotel.reservation.servlet;

import com.hotel.reservation.model.BookingDetails;
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

/**
 * Controller for displaying booking confirmation page.
 * GET /reservation/confirmation?id=BK01
 */
@WebServlet("/reservation/confirmation")
public class BookingConfirmationServlet extends HttpServlet {

    @Autowired
    private ReservationService reservationService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String id = request.getParameter("id");
        if (id == null || id.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/bookings");
            return;
        }

        Reservation reservation = reservationService.findById(id.trim());
        if (reservation == null) {
            response.sendRedirect(request.getContextPath() + "/bookings?error=Reservation+not+found");
            return;
        }

        // Authorization / IDOR Protection: only owner or staff can view
        if (!reservation.canBeViewedBy(currentUser)) {
            response.sendRedirect(request.getContextPath() + "/bookings?error=Access+denied");
            return;
        }

        BookingDetails details = reservationService.toDetails(reservation, currentUser);
        if (details == null) {
            response.sendRedirect(request.getContextPath() + "/bookings?error=Reservation+details+unavailable");
            return;
        }

        request.setAttribute("b", details);
        request.getRequestDispatcher("/WEB-INF/views/reservation/confirmation.jsp").forward(request, response);
    }
}
