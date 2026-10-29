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
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/staff")
public class StaffListServlet extends HttpServlet {

    @Autowired
    private StaffService staffService;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
            List<Staff> staffList = staffService.listStaff(currentUser);
            request.setAttribute("staffList", staffList);
            request.getRequestDispatcher("/WEB-INF/views/admin/staff.jsp").forward(request, response);
        } catch (SecurityException e) {
            response.sendError(403, e.getMessage());
        }
    }

}
