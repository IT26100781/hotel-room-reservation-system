package com.hotel.review.servlet;

import com.hotel.review.service.ReviewService;
import com.hotel.user.User;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;

@WebServlet("/reviews/save")
public class ReviewSaveServlet extends HttpServlet {
    @Autowired private ReviewService service;
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User actor = SessionUtil.getCurrentUser(req.getSession(false));
        if (actor == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        try {
            int rating = Integer.parseInt(req.getParameter("rating"));
            String id = req.getParameter("id");
            if (id == null || id.isBlank()) service.create(actor, req.getParameter("bookingId"), rating, req.getParameter("text"));
            else service.update(actor, id, rating, req.getParameter("text"));
            resp.sendRedirect(req.getContextPath() + "/reviews?done=saved");
        } catch (SecurityException e) { resp.sendError(403, e.getMessage()); }
        catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("reviewText", req.getParameter("text"));
            req.setAttribute("reviewRating", req.getParameter("rating"));
            req.setAttribute("bookingId", req.getParameter("bookingId"));
            String id = req.getParameter("id");
            if (id != null && !id.isBlank()) req.setAttribute("review", service.find(id));
            req.getRequestDispatcher("/WEB-INF/views/review/write.jsp").forward(req, resp);
        }
    }
}

