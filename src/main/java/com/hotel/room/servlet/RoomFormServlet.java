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

import java.io.IOException;

/**
 * Serves the Add/Edit room form view.
 * Corresponds to prototype/room/form.html.
 */
@WebServlet(urlPatterns = {"/admin/rooms/form", "/add-room", "/update-room"})
public class RoomFormServlet extends HttpServlet {

    private final RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = SessionUtil.getCurrentUser(request.getSession());
        if (currentUser != null && !currentUser.canManageRooms()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can manage rooms.");
            return;
        }

        String roomId = request.getParameter("id");
        if (roomId == null || roomId.trim().isEmpty()) {
            roomId = request.getParameter("roomId");
        }

        if (roomId != null && !roomId.trim().isEmpty()) {
            Room room = roomService.getRoomById(roomId.trim());
            if (room != null) {
                request.setAttribute("room", room);
                request.setAttribute("isEdit", true);
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/room/form.jsp").forward(request, response);
    }
}
