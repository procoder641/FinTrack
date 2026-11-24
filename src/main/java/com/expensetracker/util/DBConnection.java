package com.expensetracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {

    private static final Logger LOG =
            Logger.getLogger(DBConnection.class.getName());

    private static final String DRIVER =
            "com.mysql.cj.jdbc.Driver";

    private static final String URL =
            System.getenv("DB_URL");

    private static final String USER =
            System.getenv("DB_USER");

    private static final String PASS =
            System.getenv("DB_PASSWORD");

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            LOG.log(Level.SEVERE, "MySQL JDBC driver not found", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (URL == null || USER == null || PASS == null) {
            throw new SQLException("Database environment variables are missing.");
        }

        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOG.log(Level.WARNING, "Failed to close connection", e);
            }
        }
    }
}