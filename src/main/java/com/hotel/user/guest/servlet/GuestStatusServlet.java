package com.hotel.user.guest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/guests/status")
public class GuestStatusServlet extends GuestServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            guestService.setActive(currentUser(request), request.getParameter("id"), booleanParameter(request, "active"));
            redirect(request, response, "/admin/guests?done=status");
        } catch (SecurityException e) {
            response.sendError(403, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            managementError(request, response, e);
        }
    }
}
