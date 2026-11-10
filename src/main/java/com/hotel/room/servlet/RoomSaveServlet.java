package com.hotel.room.servlet;

import com.hotel.room.model.Room;
import com.hotel.room.model.RoomStatus;
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
 * Handles creation and updating of room records.
 * Invoked by the form at /admin/rooms/form.
 */
@WebServlet("/admin/rooms/save")
public class RoomSaveServlet extends HttpServlet {

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
        String type = request.getParameter("type");
        String roomNoStr = request.getParameter("roomNo");
        String roomName = request.getParameter("roomName");
        String rateStr = request.getParameter("rate");
        String capacityStr = request.getParameter("capacity");
        String roomSizeStr = request.getParameter("roomSize");
        String imageUrl = request.getParameter("imageUrl");
        String statusStr = request.getParameter("status");

        Room room = null;
        boolean isUpdate = (roomId != null && !roomId.trim().isEmpty());

        try {
            if (type == null || type.trim().isEmpty()) {
                throw new IllegalArgumentException("Please select a room type");
            }
            if (roomNoStr == null || roomNoStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Room number is required");
            }

            int roomNumber;
            try {
                roomNumber = Integer.parseInt(roomNoStr.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Room number must be a valid whole number");
            }

            if (rateStr == null || rateStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Rate per night is required");
            }
            double rate;
            try {
                rate = Double.parseDouble(rateStr.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Rate per night must be a valid number");
            }

            if (capacityStr == null || capacityStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Capacity is required");
            }
            int capacity;
            try {
                capacity = Integer.parseInt(capacityStr.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Capacity must be a valid whole number");
            }

            if (roomSizeStr == null || roomSizeStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Room size is required");
            }
            int roomSize;
            try {
                roomSize = Integer.parseInt(roomSizeStr.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Room size must be a valid whole number");
            }

            RoomStatus status = RoomStatus.fromString(statusStr);

            // Construct room instance using the single, exact factory method
            room = Room.create(type, roomId, roomName != null ? roomName.trim() : "", roomNumber, rate, capacity, roomSize, imageUrl != null ? imageUrl.trim() : "", status);

            if (isUpdate) {
                roomService.updateRoom(currentUser, room);
                response.sendRedirect(request.getContextPath() + "/admin/rooms?done=updated");
            } else {
                roomService.addRoom(currentUser, room);
                response.sendRedirect(request.getContextPath() + "/admin/rooms?done=added");
            }

        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            if (isUpdate && room == null) {
                room = roomService.getRoomById(roomId);
                request.setAttribute("room",room);
            }
            request.setAttribute("error", e.getMessage());
            request.setAttribute("room", room);
            request.getRequestDispatcher("/WEB-INF/views/room/form.jsp").forward(request, response);
        }
    }
}
