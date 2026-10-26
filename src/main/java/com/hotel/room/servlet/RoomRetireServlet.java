package com.hotel.room.servlet;

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
 * Retires a room so it is no longer bookable.
 */
@WebServlet("/admin/rooms/retire")
public class RoomRetireServlet extends HttpServlet {

    private final RoomService roomService = new RoomService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = SessionUtil.getCurrentUser(request.getSession());
        if (currentUser != null && !currentUser.canManageRooms()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only admins can manage rooms.");
            return;
        }

        String roomId = request.getParameter("roomId");
        if (roomId == null || roomId.trim().isEmpty()) {
            roomId = request.getParameter("id");
        }

        if (roomId != null && !roomId.trim().isEmpty()) {
            roomService.retireRoom(roomId.trim());
        }

        response.sendRedirect(request.getContextPath() + "/admin/rooms");
    }
}
