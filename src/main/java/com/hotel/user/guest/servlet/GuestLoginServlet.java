package com.hotel.user.guest.servlet;

import com.hotel.user.guest.model.Guest;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class GuestLoginServlet extends GuestServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (currentUser(request) instanceof Guest) {
            redirect(request, response, "/account");
            return;
        }
        view(request, response, "login");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Guest guest = guestService.login(request.getParameter("email"), request.getParameter("password"));
            HttpSession session = request.getSession();
            request.changeSessionId();
            SessionUtil.login(session, guest);
            redirect(request, response, "/account");
        } catch (IllegalArgumentException | SecurityException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("email", request.getParameter("email"));
            view(request, response, "login");
        }
    }
}
