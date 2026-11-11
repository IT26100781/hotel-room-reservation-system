package com.hotel.user.admin.servlet;

import com.hotel.reservation.model.BookingDetails;
import com.hotel.reservation.service.ReservationService;
import com.hotel.room.service.RoomService;
import com.hotel.user.User;
import com.hotel.user.admin.model.Staff;
import com.hotel.user.admin.service.StaffService;
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
 * The AdminDashboardServlet class handles the logic for the admin dashboard
 * only logged-in staff/admin users can access
 */
@WebServlet("/admin")
public class AdminDashboardServlet extends HttpServlet {
    @Autowired
    private StaffService staffService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private ReservationService reservationService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        if (currentUser.canViewStaffUsers()) {
            List<Staff> staffList = staffService.listStaff(currentUser);
            request.setAttribute("staffCount", staffList.size());
            int activeStaffCount = 0;
            for (Staff staff : staffList) {
                if (staff.isActive()) activeStaffCount++;
            }
            request.setAttribute("activeStaffCount", activeStaffCount);
        }
        request.setAttribute("roomCount", roomService.getMetrics(roomService.getAllRooms()).getTotal());
        request.setAttribute("activeRoomCount", roomService.getMetrics(roomService.getAllRooms()).getActive());
        if (currentUser.canManageAnyBooking()) {
            List<BookingDetails> reservationList = reservationService.toDetails(reservationService.findAll(), currentUser);
            request.setAttribute("arrivalsToday", reservationService.getMetrics(reservationList).getArrivingToday());
            request.setAttribute("pendingPayments", reservationService.getMetrics(reservationList).getPaymentPending());
        }
        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}
