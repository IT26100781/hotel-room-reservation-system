package com.hotel.user.admin.servlet;

import com.hotel.user.Role;
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

@WebServlet("/admin/staff/save")
public class StaffSaveServlet extends HttpServlet {
    @Autowired
    private StaffService staffService;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (!(currentUser.canManageStaffUsers() || currentUser.getId().equals(id))) {
            response.sendError(403, "You are not allowed to perform this action");
            return;
        }
        Staff staff =null;
        try{
            if (id == null) {
                if ( Role.ADMIN.name().equals(role)) {
                    staffService.addAdmin(currentUser, name, email, password);
                    response.sendRedirect(request.getContextPath()+"/admin/staff?done=added");
                    return;
                } else {
                    staffService.addStaff(currentUser, name, email, password);
                    response.sendRedirect(request.getContextPath()+"/admin/staff?done=added");
                    return;
                }

            }
            staff = staffService.findStaffById(currentUser, id);
            if (staff == null) {
                response.sendRedirect(request.getContextPath()+"/admin/staff");
                return;
            }
            if(staff.getRole() == Role.STAFF) {
                staffService.updateStaffDetails(currentUser,id,name,email);
                response.sendRedirect(request.getContextPath()+"/admin/staff?done=updated");
                return;
            } else if (staff.getRole() == Role.ADMIN) {
                staffService.updateAdminDetails(currentUser,id,name,email);
                response.sendRedirect(request.getContextPath()+"/admin/staff?done=updated");
                return;
            }

        } catch (SecurityException e) {
            response.sendError(403, e.getMessage());
        }catch (IllegalArgumentException | IllegalStateException e){
            request.setAttribute("error", e.getMessage());
            request.setAttribute("staff", staff);
            request.getRequestDispatcher("/WEB-INF/views/admin/staff-form.jsp").forward(request, response);
        }
    }
}
