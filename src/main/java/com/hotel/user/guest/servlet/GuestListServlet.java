package com.hotel.user.guest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/guests")
public class GuestListServlet extends GuestServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("guestList", guestService.listGuests(currentUser(request)));
            view(request, response, "manage");
        } catch (SecurityException e) {
            response.sendError(403, e.getMessage());
        }
    }
}
