package com.hotel.review.servlet;

import com.hotel.review.model.Review;
import com.hotel.review.service.ReviewService;
import com.hotel.user.User;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;

@WebServlet("/reviews/form")
public class ReviewFormServlet extends HttpServlet {
    @Autowired private ReviewService service;
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = SessionUtil.getCurrentUser(req.getSession(false));
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        try { service.requireActiveGuest(user); }
        catch (SecurityException e) { resp.sendError(403, e.getMessage()); return; }
        String id = req.getParameter("id");
        if (id != null && !id.isBlank()) {
            Review review = service.find(id);
            if (review == null) { resp.sendError(404); return; }
            if (!review.getGuestId().equals(user.getId())) { resp.sendError(403); return; }
            req.setAttribute("review", review);
        }
        req.getRequestDispatcher("/WEB-INF/views/review/write.jsp").forward(req, resp);
    }
}
