package com.hotel.room.servlet;

import com.hotel.room.model.*;
import com.hotel.room.service.RoomService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/add-room")
public class AddRoomServlet extends HttpServlet {
    private RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("add-room.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String roomId = request.getParameter("roomId");
        String roomType = request.getParameter("roomType");
        String roomName = request.getParameter("roomName");
        int roomNumber = Integer.parseInt(request.getParameter("roomNumber"));
        double baseRate = Double.parseDouble(request.getParameter("baseRate"));
        int capacity = Integer.parseInt(request.getParameter("capacity"));
        int roomSize = Integer.parseInt(request.getParameter("roomSize"));
        String imageUrl = request.getParameter("imageUrl");
        String status = request.getParameter("status");

        Room room = null;
        switch (roomType) {
            case "StandardRoom":
                room = new StandardRoom(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
                break;
            case "DeluxeRoom":
                room = new DeluxeRoom(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
                break;
            case "SuiteRoom":
                room = new SuiteRoom(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
                break;
        }

        if (room != null && roomService.addRoom(room)) {
            response.sendRedirect("room-list");
        } else {
            request.setAttribute("errorMessage", "Failed to add room!");
            request.getRequestDispatcher("add-room.jsp").forward(request, response);
        }
    }
}