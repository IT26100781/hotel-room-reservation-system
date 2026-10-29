package com.hotel.user.guest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class GuestRegisterServlet extends GuestServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        view(request, response, "register");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String password = request.getParameter("password");
            if (password == null || !password.equals(request.getParameter("confirmPassword"))) throw new IllegalArgumentException("Passwords do not match");
            guestService.register(request.getParameter("name"), request.getParameter("email"), password);
            redirect(request, response, "/login?created=1");
        } catch (IllegalArgumentException | IllegalStateException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("name", request.getParameter("name"));
            request.setAttribute("email", request.getParameter("email"));
            view(request, response, "register");
        }
    }
}
