package com.hotel.user.admin.servlet;

import com.hotel.user.admin.service.StaffService;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@WebServlet("/admin/setup")
public class StaffSetupServlet extends HttpServlet {
    @Autowired
    private StaffService staffService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean hasAnyAdmin = staffService.hasAnyAdminRole();
        if (hasAnyAdmin) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        boolean isLoggedIn = SessionUtil.isLoggedIn(request.getSession(false));
        if (isLoggedIn) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/admin/setup.jsp").forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        if( !password.equals(confirmPassword) ) {
            request.setAttribute("error", "Passwords do not match");
            request.getRequestDispatcher("/WEB-INF/views/admin/setup.jsp").forward(request, response);
            return;
        }
        try {
            staffService.createFirstAdmin(username, email, password);
            response.sendRedirect(request.getContextPath() + "/admin/login?created=1");
            return;
        }catch (IllegalArgumentException | IllegalStateException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/setup.jsp").forward(request, response);
        }
    }
}
