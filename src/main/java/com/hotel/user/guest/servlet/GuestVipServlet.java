package com.hotel.user.guest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/guests/vip")
public class GuestVipServlet extends GuestServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            guestService.setVip(currentUser(request), request.getParameter("id"), booleanParameter(request, "vip"));
            redirect(request, response, "/admin/guests?done=vip");
        } catch (SecurityException e) {
            response.sendError(403, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            managementError(request, response, e);
        }
    }
}
