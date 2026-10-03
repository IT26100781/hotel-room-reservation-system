package com.hotel.util;

import com.hotel.user.User;
import jakarta.servlet.http.HttpSession;

/**
 * Helpers for the logged-in user stored in the HttpSession.
 * Always get the current user from here, never from a URL parameter.
 */
public final class SessionUtil {

    public static final String CURRENT_USER = "currentUser";

    private SessionUtil() {
    }

    public static User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute(CURRENT_USER);
    }

    public static boolean isLoggedIn(HttpSession session) {
        return getCurrentUser(session) != null;
    }

    public static void login(HttpSession session, User user) {
        session.setAttribute(CURRENT_USER, user);
    }

    public static void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
}
