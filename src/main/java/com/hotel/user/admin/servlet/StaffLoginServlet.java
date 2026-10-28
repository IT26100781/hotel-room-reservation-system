package com.hotel.user.admin.servlet;

import com.hotel.user.User;
import com.hotel.user.admin.model.Staff;
import com.hotel.user.admin.service.StaffService;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@WebServlet("/admin/login")
public class StaffLoginServlet extends HttpServlet {
    @Autowired
    private StaffService staffService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean hasAnyAdmin = staffService.hasAnyAdminRole();
        if (!hasAnyAdmin) {
            response.sendRedirect(request.getContextPath() + "/admin/setup");
            return;
        }

        boolean isLoggedIn = SessionUtil.isLoggedIn(request.getSession(false));
        if (isLoggedIn) {
            User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
            if (currentUser instanceof Staff ) {
                response.sendRedirect(request.getContextPath() + "/admin");
                return;
            }
        }
        request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            User user = staffService.login(email, password);
            HttpSession session = request.getSession();
            request.changeSessionId();
            SessionUtil.login(session, user);
            response.sendRedirect(request.getContextPath()+"/admin");
            return;

        } catch (IllegalArgumentException | SecurityException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);

        }
    }

}
