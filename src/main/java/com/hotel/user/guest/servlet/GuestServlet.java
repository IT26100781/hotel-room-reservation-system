package com.hotel.user.guest.servlet;

import com.hotel.user.User;
import com.hotel.user.guest.service.GuestService;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;

/** Shared view and session helpers; business rules belong to GuestService. */
abstract class GuestServlet extends HttpServlet {
    @Autowired
    protected GuestService guestService;

    protected User currentUser(HttpServletRequest request) {
        return SessionUtil.getCurrentUser(request.getSession(false));
    }

    protected void view(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/guest/" + page + ".jsp").forward(request, response);
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }

    protected void profileError(HttpServletRequest request, HttpServletResponse response, String attribute, Exception error) throws ServletException, IOException {
        request.setAttribute(attribute, error.getMessage());
        request.setAttribute("guest", guestService.findGuestById(currentUser(request), currentUser(request).getId()));
        view(request, response, "profile");
    }

    protected void managementError(HttpServletRequest request, HttpServletResponse response, Exception error) throws ServletException, IOException {
        request.setAttribute("error", error.getMessage());
        request.setAttribute("guestList", guestService.listGuests(currentUser(request)));
        view(request, response, "manage");
    }

    protected boolean booleanParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (!"true".equals(value) && !"false".equals(value)) throw new IllegalArgumentException("Invalid " + name + " value");
        return Boolean.parseBoolean(value);
    }
}
