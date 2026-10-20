package com.hotel.room.servlet;

import com.hotel.room.model.*;
import com.hotel.room.service.RoomService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("update-room")
public class UpdateRoomServlet extends HttpServlet {
    private RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String roomId = request.getParameter("roomId");
        if (roomId != null && !roomId.trim().isEmpty()) {
            for (Room room : roomService.getAllRooms()) {
                if (room.getRoomId().equalsIgnoreCase(roomId)) {
                    request.setAttribute("room", room);
                    break;
                }
            }
        }
        request.getRequestDispatcher("edit-room.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String roomId = request.getParameter("roomId");

        if ("delete".equalsIgnoreCase(action)) {
            roomService.deleteRoom(roomId);
        } else {
            String roomType = request.getParameter("roomType");
            String roomName = request.getParameter("roomName");
            int roomNumber = Integer.parseInt(request.getParameter("roomNumber"));
            double baseRate = Double.parseDouble(request.getParameter("baseRate"));
            int capacity = Integer.parseInt(request.getParameter("capacity"));
            int roomSize = Integer.parseInt(request.getParameter("roomSize"));
            String imageUrl = request.getParameter("imageUrl");
            String status = request.getParameter("status");

            Room updatedRoom = null;
            switch (roomType) {
                case "StandardRoom":
                    updatedRoom = new StandardRoom(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
                    break;
                case "DeluxeRoom":
                    updatedRoom = new DeluxeRoom(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
                    break;
                case "SuiteRoom":
                    updatedRoom = new SuiteRoom(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
                    break;
            }

            if (updatedRoom != null) {
                roomService.updateRoom(updatedRoom);
            }
        }

        response.sendRedirect("room-list");
    }
}