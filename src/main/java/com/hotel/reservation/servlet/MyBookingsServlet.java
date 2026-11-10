package com.hotel.reservation.servlet;

import com.hotel.payment.dao.PaymentDAO;
import com.hotel.payment.model.Payment;
import com.hotel.reservation.model.BookingDetails;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.service.ReservationService;
import com.hotel.user.User;
import com.hotel.user.guest.model.Guest;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Controller for guest bookings list page (GET /bookings).
 * Categorizes reservations into upcoming, completed, and cancelled lists.
 */
@WebServlet("/bookings")
public class MyBookingsServlet extends HttpServlet {

    @Autowired
    private ReservationService reservationService;

    @Autowired(required = false)
    private PaymentDAO paymentDAO;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (!(currentUser instanceof Guest)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<Reservation> reservations = reservationService.findByGuestId(currentUser.getId());
        List<BookingDetails> allDetails = reservationService.toDetails(reservations, currentUser);

        List<BookingDetails> upcoming = new ArrayList<>();
        List<BookingDetails> completed = new ArrayList<>();
        List<BookingDetails> cancelled = new ArrayList<>();

        Map<String, Object> paidAmounts = new HashMap<>();

        for (BookingDetails bd : allDetails) {
            Reservation r = bd.getReservation();
            String stateVal = r.getState().getValue();

            // Populate paid amounts from Payment module if recorded
            if (paymentDAO != null) {
                try {
                    Payment p = paymentDAO.forBooking(r.getId());
                    if (p != null) {
                        paidAmounts.put(r.getId(), p.getAmount());
                    }
                } catch (Exception ignored) {
                }
            }

            if ("completed".equalsIgnoreCase(stateVal)) {
                completed.add(bd);
            } else if ("cancelled".equalsIgnoreCase(stateVal) || "noshow".equalsIgnoreCase(stateVal)) {
                cancelled.add(bd);
            } else {
                // upcoming, arriving, inhouse
                upcoming.add(bd);
            }
        }

        request.setAttribute("upcoming", upcoming);
        request.setAttribute("completed", completed);
        request.setAttribute("cancelled", cancelled);
        request.setAttribute("paidAmounts", paidAmounts);
        request.setAttribute("minDate", LocalDate.now().toString());

        request.getRequestDispatcher("/WEB-INF/views/reservation/my-bookings.jsp").forward(request, response);
    }
}
