package com.hotel.user.admin.servlet;

import com.hotel.user.User;
import com.hotel.user.admin.model.Staff;
import com.hotel.user.admin.service.StaffService;
import com.hotel.util.SessionUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * A filter that restricts access to pages under the `/admin/*` URL path.
 *
 *Only logged-in staff/Admin users can access it
 *
 * admin/login and admin/setup are exceptions
 */
@WebFilter("/admin/*")
public class StaffAuthFilter extends HttpFilter {
    @Autowired
    private StaffService staffService;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request.getServletPath().equals("/admin/login") || request.getServletPath().equals("/admin/setup")) {
            chain.doFilter(request, response);
            return;
        }
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (!(currentUser instanceof Staff)) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        Staff freshStaff = staffService.refreshSessionUser(currentUser.getId());
        if (freshStaff == null || !freshStaff.isActive()) {
            SessionUtil.logout(request.getSession(false));
            response.sendRedirect(request.getContextPath() + "/admin/login?deactivated=1");
            return;
        }
        SessionUtil.login(request.getSession(false), freshStaff);
        chain.doFilter(request, response);
    }
}
