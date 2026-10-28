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

@WebServlet("/admin/staff/form")
public class StaffFormServlet extends HttpServlet {
    @Autowired
    private StaffService staffService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (!(currentUser.canManageStaffUsers() || currentUser.getId().equals(id))) {
            response.sendError(403, "you don’t have permission to manage staff");
            return;
        }
        if (id == null) {
            request.getRequestDispatcher("/WEB-INF/views/admin/staff-form.jsp").forward(request, response);
            return;
        }
        try{
            Staff staff = staffService.findStaffById(currentUser, id);
            if (staff == null) {
                response.sendRedirect(request.getContextPath()+"/admin/staff");
                return;
            }
            request.setAttribute("staff", staff);
            request.getRequestDispatcher("/WEB-INF/views/admin/staff-form.jsp").forward(request, response);
        }catch(SecurityException e){
            response.sendError(403,e.getMessage());
        }
    }
}
