package com.hotel.user.guest.servlet;

import com.hotel.user.User;
import com.hotel.user.guest.model.Guest;
import com.hotel.user.guest.service.GuestService;
import com.hotel.util.SessionUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;

@WebFilter(urlPatterns = {"/account", "/profile", "/profile/*", "/logout"})
public class GuestAuthFilter extends HttpFilter {
    @Autowired
    private GuestService guestService;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        User user = SessionUtil.getCurrentUser(request.getSession(false));
        if (!(user instanceof Guest)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Guest guest = guestService.findGuestById(user, user.getId());
        if (guest == null || !guest.isActive()) {
            SessionUtil.logout(request.getSession(false));
            response.sendRedirect(request.getContextPath() + "/login?deactivated=1");
            return;
        }
        SessionUtil.login(request.getSession(), guest);
        chain.doFilter(request, response);
    }
}
