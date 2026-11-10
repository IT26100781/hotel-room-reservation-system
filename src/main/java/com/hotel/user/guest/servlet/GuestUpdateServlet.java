package com.hotel.user.guest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/guests/update")
public class GuestUpdateServlet extends GuestServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            guestService.updateGuestDetails(currentUser(request), request.getParameter("id"), request.getParameter("name"), request.getParameter("email"));
            redirect(request, response, "/admin/guests?done=updated");
        } catch (SecurityException e) {
            response.sendError(403, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            managementError(request, response, e);
        }
    }
}
