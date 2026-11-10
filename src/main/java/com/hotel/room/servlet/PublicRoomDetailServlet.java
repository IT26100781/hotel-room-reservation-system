package com.hotel.room.servlet;

import com.hotel.room.model.Room;
import com.hotel.room.service.RoomService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Public room detail page for guests.
 * Corresponds to /rooms/detail.
 */
@WebServlet("/rooms/detail")
public class PublicRoomDetailServlet extends HttpServlet {

    @Autowired
    private RoomService roomService;

    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String id = request.getParameter("id");
        if (id == null || id.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/rooms");
            return;
        }

        Room room = roomService.getRoomById(id.trim());
        if (room == null || room.isRetired()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Room not found");
            return;
        }

        request.setAttribute("room", room);
        request.getRequestDispatcher("/WEB-INF/views/room/detail.jsp").forward(request, response);
    }
}
