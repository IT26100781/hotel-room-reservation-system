package com.hotel.review.servlet;

import com.hotel.review.service.ReviewService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;

@WebServlet("/reviews")
public class ReviewListServlet extends HttpServlet {
    @Autowired private ReviewService service;
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var reviews = service.allReviews();
        req.setAttribute("reviews", reviews);
        req.setAttribute("averageRating", reviews.stream().mapToInt(r -> r.getRating()).average().orElse(0.0));
        req.getRequestDispatcher("/WEB-INF/views/review/list.jsp").forward(req, resp);
    }
}
