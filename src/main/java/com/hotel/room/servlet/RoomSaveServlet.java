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
 * Handles creation and updating of room records.
 * Invoked by the form at prototype/room/form.html.
 */
@WebServlet("/admin/rooms/save")
public class RoomSaveServlet extends HttpServlet {

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
        String roomType = request.getParameter("type");
        if (roomType == null || roomType.isEmpty()) {
            roomType = request.getParameter("roomType");
        }
        if (roomType == null || roomType.isEmpty()) {
            roomType = "StandardRoom";
        }

        String roomNoStr = request.getParameter("roomNo");
        if (roomNoStr == null || roomNoStr.isEmpty()) {
            roomNoStr = request.getParameter("roomNumber");
        }

        String rateStr = request.getParameter("rate");
        if (rateStr == null || rateStr.isEmpty()) {
            rateStr = request.getParameter("baseRate");
        }

        String capacityStr = request.getParameter("capacity");
        String roomSizeStr = request.getParameter("roomSize");
        String roomName = request.getParameter("roomName");
        String imageUrl = request.getParameter("imageUrl");
        String status = request.getParameter("status");

        // Parse numeric inputs safely
        int roomNumber = 0;
        try {
            if (roomNoStr != null) {
                roomNumber = Integer.parseInt(roomNoStr.trim());
            }
        } catch (NumberFormatException ignored) {}

        double baseRate = 0.0;
        try {
            if (rateStr != null) {
                baseRate = Double.parseDouble(rateStr.trim());
            }
        } catch (NumberFormatException ignored) {}

        int capacity = 2;
        try {
            if (capacityStr != null) {
                capacity = Integer.parseInt(capacityStr.trim());
            }
        } catch (NumberFormatException ignored) {}

        int roomSize = 0;
        try {
            if (roomSizeStr != null && !roomSizeStr.trim().isEmpty()) {
                roomSize = Integer.parseInt(roomSizeStr.trim());
            }
        } catch (NumberFormatException ignored) {}

        // Fallbacks for display fields
        if (roomName == null || roomName.trim().isEmpty()) {
            String displayTypeName = roomType.replace("Room", "");
            roomName = displayTypeName + " " + (roomNumber > 0 ? roomNumber : "");
        }

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            String lowerType = roomType.toLowerCase();
            if (lowerType.contains("suite")) {
                imageUrl = "https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=700&q=80";
                if (roomSize == 0) roomSize = 62;
            } else if (lowerType.contains("deluxe")) {
                imageUrl = "https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=700&q=80";
                if (roomSize == 0) roomSize = 44;
            } else {
                imageUrl = "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?auto=format&fit=crop&w=700&q=80";
                if (roomSize == 0) roomSize = 32;
            }
        }

        if (status == null || status.trim().isEmpty()) {
            status = "active";
        }

        // Check if updating or adding
        boolean isUpdate = false;
        if (roomId != null && !roomId.trim().isEmpty()) {
            Room existing = roomService.getRoomById(roomId.trim());
            if (existing != null) {
                isUpdate = true;
            }
        } else {
            roomId = "R" + roomNumber;
        }

        Room room = roomService.createRoomInstance(roomType, roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);

        boolean success;
        if (isUpdate) {
            success = roomService.updateRoom(room);
        } else {
            success = roomService.addRoom(room);
        }

        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/rooms");
        } else {
            request.setAttribute("errorMessage", "Failed to save room. Check for duplicate room numbers or invalid rates.");
            request.setAttribute("room", room);
            request.getRequestDispatcher("/WEB-INF/views/room/form.jsp").forward(request, response);
        }
    }
}
