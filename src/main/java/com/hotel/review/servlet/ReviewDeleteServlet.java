package com.hotel.review.servlet;

import com.hotel.review.service.ReviewService;
import com.hotel.user.User;
import com.hotel.util.SessionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@WebServlet("/reviews/delete")
public class ReviewDeleteServlet extends HttpServlet {
    @Autowired
    private ReviewService service;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User actor = SessionUtil.getCurrentUser(req.getSession(false));
        if (actor == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            service.delete(actor, req.getParameter("id"));
            String destination = actor.canModerateReviews() ? "/admin/reviews?done=deleted" : "/reviews?done=deleted";
            resp.sendRedirect(req.getContextPath() + destination);
        } catch (SecurityException e) {
            resp.sendError(403, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(404, e.getMessage());
        }
    }
}

