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

@WebServlet("/admin/staff/delete")
public class StaffDeleteServlet extends HttpServlet {
    @Autowired
    private StaffService staffService;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        try {
            staffService.deleteStaff(currentUser,id);
            response.sendRedirect(request.getContextPath()+"/admin/staff?done=deleted");
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

