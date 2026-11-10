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

/**
 * Serves the Add/Edit room form view.
 * Only mapped under /admin/rooms/form so it is protected by StaffAuthFilter.
 */
@WebServlet("/admin/rooms/form")
public class RoomFormServlet extends HttpServlet {

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

        String roomId = request.getParameter("id");
        if (roomId != null && !roomId.trim().isEmpty()) {
            Room room = roomService.getRoomById(roomId.trim());
            if (room != null) {
                request.setAttribute("room", room);
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/room/form.jsp").forward(request, response);
    }
}
