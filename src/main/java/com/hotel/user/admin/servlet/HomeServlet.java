package com.hotel.user.admin.servlet;

import com.hotel.content.service.HomeContentService;
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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("home", homeContentService.getHomeContent());
        request.getRequestDispatcher("/WEB-INF/views/main/index.jsp").forward(request, response);
    }
}
