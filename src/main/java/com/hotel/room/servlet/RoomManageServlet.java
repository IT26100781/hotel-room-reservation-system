package com.hotel.room.servlet;

import com.hotel.room.model.Room;
import com.hotel.room.service.RoomService;
import com.hotel.user.User;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Staff/Admin room management page.
 * Corresponds to prototype/room/manage.html.
 */
@WebServlet(urlPatterns = {"/admin/rooms", "/room-list"})
public class RoomManageServlet extends HttpServlet {

    private RoomService roomService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
        if (context != null) {
            this.roomService = context.getBean(RoomService.class);
        }
    }

    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = SessionUtil.getCurrentUser(request.getSession());
        if (currentUser != null && !currentUser.canManageRooms()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can manage rooms.");
            return;
        }

        String statusFilter = request.getParameter("status");
        String typeFilter = request.getParameter("type");
        String search = request.getParameter("search");

        List<Room> rooms;
        if ((statusFilter != null && !statusFilter.isEmpty())
                || (typeFilter != null && !typeFilter.isEmpty())
                || (search != null && !search.isEmpty())) {
            rooms = roomService.searchRooms(search, typeFilter, statusFilter, null, null);
        } else {
            rooms = roomService.getAllRooms();
        }

        request.setAttribute("rooms", rooms);
        request.setAttribute("totalCount", roomService.getTotalCount());
        request.setAttribute("activeCount", roomService.getActiveCount());
        request.setAttribute("maintenanceCount", roomService.getMaintenanceCount());
        request.setAttribute("retiredCount", roomService.getRetiredCount());

        request.getRequestDispatcher("/WEB-INF/views/room/manage.jsp").forward(request, response);
    }
}
