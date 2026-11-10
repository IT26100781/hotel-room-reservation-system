package com.hotel.content.service;

import com.hotel.content.dao.HomeContentDAO;
import com.hotel.content.model.HomeContent;
import com.hotel.user.User;
import org.springframework.stereotype.Service;

@Service
public class HomeContentService {
    private final HomeContentDAO homeContentDAO;

    public HomeContentService(HomeContentDAO homeContentDAO) {
        this.homeContentDAO = homeContentDAO;
    }

    public HomeContent getHomeContent() {
        return homeContentDAO.find();
    }

    public HomeContent getHomeContentForEdit(User currentUser) {
        if (currentUser == null || !currentUser.canEditSiteContent()) {
            throw new SecurityException("You can't edit the home page");
        }
        return homeContentDAO.find();
    }

    public void updateHomeContent(User currentUser, HomeContent content) {
        if (currentUser == null || !currentUser.canEditSiteContent()) {
            throw new SecurityException("You can't edit the home page");
        }
        content.setHeroEyebrow(requireText(content.getHeroEyebrow(), "Small heading", 50));
        content.setHeroTitle(requireText(content.getHeroTitle(), "Headline", 60));
        content.setHeroHighlight(requireText(content.getHeroHighlight(), "Highlighted words", 40));
        content.setHeroText(requireText(content.getHeroText(), "Intro text", 400));
        content.setHeroImageUrl(requireText(content.getHeroImageUrl(), "Hero image URL", 500));
        if (!content.getHeroImageUrl().startsWith("https://")) {
            throw new IllegalArgumentException("Hero image URL must start with https://");
        }
        content.setCtaTitle(requireText(content.getCtaTitle(), "Banner heading", 60));
        content.setCtaHighlight(requireText(content.getCtaHighlight(), "Banner highlighted words", 40));
        content.setCtaText(requireText(content.getCtaText(), "Banner content", 300));
        if (content.getAnnouncementText() == null) {
            content.setAnnouncementText("");
        }
        content.setAnnouncementText(content.getAnnouncementText().trim());
        if (content.getAnnouncementText().length() > 150) {
            throw new IllegalArgumentException("The Announcement must not exceed 150 characters");
        }
        if (content.isAnnouncementVisible() && content.getAnnouncementText().isEmpty()) {
            throw new IllegalArgumentException("The Announcement text must not be empty when it is switched on");
        }
        content.setUpdatedBy(currentUser.getName());

        boolean result = homeContentDAO.update(content);
        if (!result) {
            throw new IllegalStateException("Unable to save the content");
        }

    }


    // helpers
    private String requireText(String value, String fieldName, int maxLength) {
        if (value == null) {
            value = "";
        }
        value = value.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " cannot be longer than " + maxLength + " characters");
        }
        return value;
    }
}
