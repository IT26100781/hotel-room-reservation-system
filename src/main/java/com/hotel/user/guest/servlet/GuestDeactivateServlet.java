package com.hotel.user.guest.servlet;

import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/profile/deactivate")
public class GuestDeactivateServlet extends GuestServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            guestService.deactivateAccount(currentUser(request));
            SessionUtil.logout(request.getSession(false));
            redirect(request, response, "/login?deactivated=1");
        } catch (SecurityException e) {
            response.sendError(403, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            profileError(request, response, "error", e);
        }
    }
}
