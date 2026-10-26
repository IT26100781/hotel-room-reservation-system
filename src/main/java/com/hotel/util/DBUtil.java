package com.hotel.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// * Shared database connection utility.
// * Reads environment variables if provided, otherwise falls back to defaults
// * matching application.properties.
public final class DBUtil {

    private static final String DB_PORT = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
    private static final String DB_USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "root";

    private static final String URL = "jdbc:mysql://localhost:" + DB_PORT
            + "/hotel_reservation?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
        }
    }

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }
}
