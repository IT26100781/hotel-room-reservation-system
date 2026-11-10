package com.hotel.user.guest.servlet;

import com.hotel.user.guest.model.Guest;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/profile/password")
public class GuestPasswordServlet extends GuestServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Guest guest = guestService.changePassword(currentUser(request), request.getParameter("currentPassword"), request.getParameter("newPassword"), request.getParameter("confirmPassword"));
            request.changeSessionId();
            SessionUtil.login(request.getSession(), guest);
            redirect(request, response, "/profile?done=password");
        } catch (SecurityException e) {
            response.sendError(403, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            profileError(request, response, "passwordError", e);
        }
    }
}
