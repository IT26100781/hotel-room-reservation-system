package com.hotel.user.admin.servlet;

import com.hotel.user.User;
import com.hotel.user.admin.service.StaffService;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@WebServlet("/admin/password")
public class StaffPasswordServlet extends HttpServlet {
    @Autowired
    private StaffService staffService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/admin/password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        try {
            staffService.changeOwnPassword(currentUser, currentPassword, newPassword, confirmPassword);
            request.changeSessionId();
            response.sendRedirect(request.getContextPath() + "/admin?done=password");

        } catch (IllegalArgumentException | IllegalStateException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/password.jsp").forward(request, response);
        }

    }
}
