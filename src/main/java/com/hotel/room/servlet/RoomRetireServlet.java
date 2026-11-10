package com.hotel.room.servlet;

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
 * Retires a room so it is no longer bookable.
 * Only mapped under /admin/rooms/retire.
 */
@WebServlet("/admin/rooms/retire")
public class RoomRetireServlet extends HttpServlet {

    @Autowired
    private RoomService roomService;

    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (currentUser == null || !currentUser.canManageRooms()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to manage rooms.");
            return;
        }

        String roomId = request.getParameter("roomId");
        if (roomId == null || roomId.trim().isEmpty()) {
            roomId = request.getParameter("id");
        }

        try {
            if (roomId != null && !roomId.trim().isEmpty()) {
                roomService.retireRoom(currentUser, roomId.trim());
            }
            response.sendRedirect(request.getContextPath() + "/admin/rooms?done=retired");
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            response.sendRedirect(request.getContextPath() + "/admin/rooms?error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}
