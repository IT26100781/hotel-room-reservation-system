package com.hotel.room.servlet;

import com.hotel.room.model.Room;
import com.hotel.room.service.RoomService;
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
 * Public room listing for guests.
 * Corresponds to prototype/room/list.html.
 */
@WebServlet("/rooms")
public class PublicRoomListServlet extends HttpServlet {

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

        String type = request.getParameter("type");
        String maxPriceStr = request.getParameter("maxPrice");
        String capacityStr = request.getParameter("capacity");

        Double maxPrice = null;
        try {
            if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
                maxPrice = Double.parseDouble(maxPriceStr);
            }
        } catch (NumberFormatException ignored) {}

        Integer capacity = null;
        try {
            if (capacityStr != null && !capacityStr.isEmpty()) {
                capacity = Integer.parseInt(capacityStr);
            }
        } catch (NumberFormatException ignored) {}

        // Guests only see active rooms
        List<Room> rooms = roomService.searchRooms(null, type, "active", maxPrice, capacity);

        request.setAttribute("rooms", rooms);
        request.getRequestDispatcher("/WEB-INF/views/room/list.jsp").forward(request, response);
    }
}
