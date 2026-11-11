package com.hotel.user.admin.servlet;

import com.hotel.content.service.HomeContentService;
import com.hotel.review.service.ReviewService;
import com.hotel.room.service.RoomService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@WebServlet("")
public class HomeServlet extends HttpServlet {
    @Autowired
    private HomeContentService homeContentService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private ReviewService reviewService;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("home", homeContentService.getHomeContent());
        request.setAttribute("rooms", roomService.searchRooms(null, null, "active", null, null));
        request.setAttribute("reviews",reviewService.allReviews());
        request.getRequestDispatcher("/WEB-INF/views/main/index.jsp").forward(request, response);
    }
}
