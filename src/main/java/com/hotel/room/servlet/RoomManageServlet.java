package com.hotel.room.servlet;

import com.hotel.room.model.Room;
import com.hotel.room.service.RoomService;
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
 * Controller for Staff/Admin room management page.
 * Only mapped under /admin/rooms so it is protected by StaffAuthFilter.
 */
@WebServlet("/admin/rooms")
public class RoomManageServlet extends HttpServlet {

    @Autowired
    private RoomService roomService;

    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (currentUser == null || !currentUser.canManageRooms()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to manage rooms.");
            return;
        }

        String statusFilter = request.getParameter("status");
        String typeFilter = request.getParameter("type");
        String search = request.getParameter("search");

        List<Room> allRooms = roomService.getAllRooms();
        // Calculate metrics in a single pass over all rooms (1 query)
        RoomService.RoomMetrics metrics = roomService.getMetrics(allRooms);

        List<Room> displayedRooms;
        if ((statusFilter != null && !statusFilter.isEmpty())
                || (typeFilter != null && !typeFilter.isEmpty())
                || (search != null && !search.isEmpty())) {
            displayedRooms = roomService.searchRooms(search, typeFilter, statusFilter, null, null);
        } else {
            displayedRooms = allRooms;
        }

        request.setAttribute("rooms", displayedRooms);
        request.setAttribute("totalCount", metrics.getTotal());
        request.setAttribute("activeCount", metrics.getActive());
        request.setAttribute("maintenanceCount", metrics.getMaintenance());
        request.setAttribute("retiredCount", metrics.getRetired());

        request.getRequestDispatcher("/WEB-INF/views/room/manage.jsp").forward(request, response);
    }
}
