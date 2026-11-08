package com.hotel.content.dao;

import com.hotel.content.model.HomeContent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class HomeContentDAO {
    private final JdbcTemplate jdbcTemplate;

    public HomeContentDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private HomeContent mapRow(ResultSet rs, int rowNum) throws SQLException {
        HomeContent content = new HomeContent();
        content.setHeroEyebrow(rs.getString("hero_eyebrow"));
        content.setHeroTitle(rs.getString("hero_title"));
        content.setHeroHighlight(rs.getString("hero_highlight"));
        content.setHeroText(rs.getString("hero_text"));
        content.setHeroImageUrl(rs.getString("hero_image_url"));
        content.setCtaTitle(rs.getString("cta_title"));
        content.setCtaHighlight(rs.getString("cta_highlight"));
        content.setCtaText(rs.getString("cta_text"));
        content.setAnnouncementText(rs.getString("announcement_text"));
        content.setAnnouncementVisible(rs.getBoolean("announcement_visible"));
        content.setUpdatedBy(rs.getString("updated_by"));
        content.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return content;

    }

    public HomeContent find() {
        String sql = "SELECT hero_eyebrow, hero_title, hero_highlight, hero_text, hero_image_url, "
                + "announcement_text, announcement_visible, cta_title, cta_highlight, cta_text, "
                + "updated_by, updated_at "
                + "FROM home_content WHERE id = 1";
        List<HomeContent> found = jdbcTemplate.query(sql, this::mapRow);
        return found.isEmpty() ? null : found.get(0);
    }

    public boolean update(HomeContent content) {
        String sql = "UPDATE home_content SET "
                + "hero_eyebrow = ?, hero_title = ?, hero_highlight = ?, hero_text = ?, hero_image_url = ?, "
                + "announcement_text = ?, announcement_visible = ?, "
                + "cta_title = ?, cta_highlight = ?, cta_text = ?, "
                + "updated_by = ? "
                + "WHERE id = 1";
        int rows = jdbcTemplate.update(sql, content.getHeroEyebrow(), content.getHeroTitle(), content.getHeroHighlight(),
                content.getHeroText(), content.getHeroImageUrl(), content.getAnnouncementText(), content.isAnnouncementVisible(),
                content.getCtaTitle(), content.getCtaHighlight(), content.getCtaText(), content.getUpdatedBy());
        return rows == 1;
    }
}
