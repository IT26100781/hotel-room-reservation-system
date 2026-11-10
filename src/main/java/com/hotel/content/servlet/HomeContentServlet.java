package com.hotel.content.servlet;

import com.hotel.content.model.HomeContent;
import com.hotel.content.service.HomeContentService;
import com.hotel.user.User;
import com.hotel.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@WebServlet("/admin/home")
public class HomeContentServlet extends HttpServlet {

    @Autowired
    private HomeContentService homeContentService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        try {
            request.setAttribute("home", homeContentService.getHomeContentForEdit(currentUser));
            request.getRequestDispatcher("/WEB-INF/views/admin/home.jsp").forward(request, response);
        } catch (SecurityException e) {
            response.sendError(403, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = SessionUtil.getCurrentUser(request.getSession(false));
        HomeContent content = new HomeContent();
        content.setHeroEyebrow(request.getParameter("heroEyebrow"));
        content.setHeroTitle(request.getParameter("heroTitle"));
        content.setHeroHighlight(request.getParameter("heroHighlight"));
        content.setHeroText(request.getParameter("heroText"));
        content.setHeroImageUrl(request.getParameter("heroImageUrl"));
        content.setAnnouncementText(request.getParameter("announcementText"));
        content.setCtaTitle(request.getParameter("ctaTitle"));
        content.setCtaHighlight(request.getParameter("ctaHighlight"));
        content.setCtaText(request.getParameter("ctaText"));
        content.setAnnouncementVisible("true".equals(request.getParameter("announcementVisible")));
        try {
            homeContentService.updateHomeContent(currentUser, content);
            response.sendRedirect(request.getContextPath() + "/admin/home?done=saved");
        } catch (SecurityException e) {
            response.sendError(403, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("home", content);
            request.getRequestDispatcher("/WEB-INF/views/admin/home.jsp").forward(request, response);
        }
    }


}
