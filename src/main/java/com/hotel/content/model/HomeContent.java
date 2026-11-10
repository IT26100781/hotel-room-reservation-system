package com.hotel.content.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HomeContent {
    private String heroEyebrow;
    private String heroTitle;
    private String heroHighlight;
    private String heroText;
    private String heroImageUrl;
    private String announcementText;
    private String ctaTitle;
    private String ctaHighlight;
    private String ctaText;
    private String updatedBy;
    private boolean announcementVisible;
    private LocalDateTime updatedAt;

    public HomeContent() {

    }
    // getters and setters

    public String getHeroEyebrow() {
        return heroEyebrow;
    }

    public void setHeroEyebrow(String heroEyebrow) {
        this.heroEyebrow = heroEyebrow;
    }

    public String getHeroTitle() {
        return heroTitle;
    }

    public void setHeroTitle(String heroTitle) {
        this.heroTitle = heroTitle;
    }

    public String getHeroHighlight() {
        return heroHighlight;
    }

    public void setHeroHighlight(String heroHighlight) {
        this.heroHighlight = heroHighlight;
    }

    public String getHeroText() {
        return heroText;
    }

    public void setHeroText(String heroText) {
        this.heroText = heroText;
    }

    public String getHeroImageUrl() {
        return heroImageUrl;
    }

    public void setHeroImageUrl(String heroImageUrl) {
        this.heroImageUrl = heroImageUrl;
    }

    public String getAnnouncementText() {
        return announcementText;
    }

    public void setAnnouncementText(String announcementText) {
        this.announcementText = announcementText;
    }

    public boolean isAnnouncementVisible() {
        return announcementVisible;
    }

    public void setAnnouncementVisible(boolean announcementVisible) {
        this.announcementVisible = announcementVisible;
    }

    public String getCtaTitle() {
        return ctaTitle;
    }

    public void setCtaTitle(String ctaTitle) {
        this.ctaTitle = ctaTitle;
    }

    public String getCtaHighlight() {
        return ctaHighlight;
    }

    public void setCtaHighlight(String ctaHighlight) {
        this.ctaHighlight = ctaHighlight;
    }

    public String getCtaText() {
        return ctaText;
    }

    public void setCtaText(String ctaText) {
        this.ctaText = ctaText;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAtText() {
        if (updatedAt == null) {
            return "";
        }
        return updatedAt.format(DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.ENGLISH));
    }

}
