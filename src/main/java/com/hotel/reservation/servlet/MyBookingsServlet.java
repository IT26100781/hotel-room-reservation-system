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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/bookings")
public class MyBookingsServlet extends HttpServlet {

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

        try {
            List<Reservation> allBookings = reservationService.getGuestBookings(user, user.getId());
            LocalDate today = LocalDate.now();

            List<Reservation> upcoming = new ArrayList<>();
            List<Reservation> completed = new ArrayList<>();
            List<Reservation> cancelled = new ArrayList<>();

            for (Reservation r : allBookings) {
                if (r.isCancelled()) {
                    cancelled.add(r);
                } else if (r.isCompleted(today)) {
                    completed.add(r);
                } else {
                    upcoming.add(r);
                }
            }

            request.setAttribute("upcomingBookings", upcoming);
            request.setAttribute("completedBookings", completed);
            request.setAttribute("cancelledBookings", cancelled);

            request.getRequestDispatcher("/WEB-INF/views/reservation/my-bookings.jsp").forward(request, response);

        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }
}
