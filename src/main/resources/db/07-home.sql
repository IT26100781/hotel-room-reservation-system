-- Owner: Admin component
-- One row (id = 1) with the text an admin can change on the guest home page.

CREATE TABLE IF NOT EXISTS home_content (
                                            id                   INT          NOT NULL,
                                            hero_eyebrow         VARCHAR(50)  NOT NULL,
    hero_title           VARCHAR(60)  NOT NULL,
    hero_highlight       VARCHAR(40)  NOT NULL,
    hero_text            VARCHAR(400) NOT NULL,
    hero_image_url       VARCHAR(500) NOT NULL,
    announcement_text    VARCHAR(150),
    announcement_visible BOOLEAN      NOT NULL DEFAULT FALSE,
    cta_title            VARCHAR(60)  NOT NULL,
    cta_highlight        VARCHAR(40)  NOT NULL,
    cta_text             VARCHAR(300) NOT NULL,
    updated_by           VARCHAR(100),
    updated_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_home_single_row CHECK (id = 1)
    );

INSERT IGNORE INTO home_content (id, hero_eyebrow, hero_title, hero_highlight, hero_text, hero_image_url,
                                 cta_title, cta_highlight, cta_text) VALUES
(1, 'Hill Country · Sri Lanka', 'Stay where time', 'slows down.',
 'Twenty-four rooms and suites above the tea terraces of Nuwara Eliya. Mountain mornings, unhurried dinners and a front desk that remembers your name.',
 'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=1200&q=80',
 'Your window seat is', 'waiting.',
 'Create a free account to book in seconds, manage your stays and leave a verified review after you check out. VIP guests enjoy 10% off every stay.');