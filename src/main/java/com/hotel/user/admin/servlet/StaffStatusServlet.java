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

@WebServlet("/admin/staff/status")
public class StaffStatusServlet extends HttpServlet {
    @Autowired
    private StaffService staffService;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        boolean active = Boolean.parseBoolean(request.getParameter("active"));
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        try {
            staffService.setActive(currentUser, id, active);
            response.sendRedirect(request.getContextPath()+"/admin/staff?done=status");
            return;
        }catch (SecurityException e) {
            response.sendError(403, e.getMessage());
        }catch (IllegalArgumentException | IllegalStateException e){
            request.setAttribute("error", e.getMessage());
            request.setAttribute("staffList", staffService.listStaff(currentUser));
            request.getRequestDispatcher("/WEB-INF/views/admin/staff.jsp").forward(request, response);
        }
    }
}
