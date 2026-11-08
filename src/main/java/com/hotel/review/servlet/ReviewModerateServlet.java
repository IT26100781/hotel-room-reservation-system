package com.hotel.review.servlet;

import com.hotel.review.service.ReviewService;
import com.hotel.user.User;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;

@WebServlet("/admin/reviews")
public class ReviewModerateServlet extends HttpServlet {
    @Autowired private ReviewService service;
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User actor = SessionUtil.getCurrentUser(req.getSession(false));
        if (actor == null || !actor.canModerateReviews()) { resp.sendError(403); return; }
        var reviews = service.allReviews();
        req.setAttribute("reviews", reviews);
        req.setAttribute("averageRating", reviews.stream().mapToInt(r -> r.getRating()).average().orElse(0.0));
        req.setAttribute("verifiedCount", reviews.stream().filter(r -> r.isVerified()).count());
        req.getRequestDispatcher("/WEB-INF/views/review/moderate.jsp").forward(req, resp);
    }
}
