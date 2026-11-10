package com.hotel.user.guest.servlet;

import com.hotel.user.guest.model.Guest;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/profile")
public class GuestProfileServlet extends GuestServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("guest", guestService.findGuestById(currentUser(request), currentUser(request).getId()));
        view(request, response, "profile");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Guest guest = guestService.updateGuestDetails(currentUser(request), currentUser(request).getId(), request.getParameter("name"), request.getParameter("email"));
            SessionUtil.login(request.getSession(), guest);
            redirect(request, response, "/profile?done=details");
        } catch (SecurityException e) {
            response.sendError(403, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            profileError(request, response, "error", e);
        }
    }
}
