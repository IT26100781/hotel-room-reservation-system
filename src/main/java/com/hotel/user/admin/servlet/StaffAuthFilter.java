package com.hotel.user.admin.servlet;

import com.hotel.user.User;
import com.hotel.user.admin.model.Staff;
import com.hotel.util.SessionUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/admin/*")
public class StaffAuthFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request.getServletPath().equals("/admin/login") || request.getServletPath().equals("/admin/setup")) {
            chain.doFilter(request, response);
            return;
        }
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        if (!(currentUser instanceof Staff)){
            response.sendRedirect(request.getContextPath()+"/admin/login");
            return;
        }
        chain.doFilter(request, response);
    }
}
