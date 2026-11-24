package com.expensetracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DBConnection — thin connection factory.
 * Replace with HikariCP / JNDI DataSource in production.
 */
public class DBConnection {

    private static final Logger LOG = Logger.getLogger(DBConnection.class.getName());

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL    =
        "jdbc:mysql://localhost:3306/expense_tracker"
      + "?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true";
    private static final String USER   = "root";
    private static final String PASS   = "Gtny@1234";   // ← change this

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            LOG.log(Level.SEVERE, "MySQL JDBC driver not found", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBConnection() {}

    /** Returns a new connection from the driver manager. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /** Quietly closes a connection (null-safe). */
    public static void close(Connection conn) {
        if (conn != null) {
            try { conn.close(); }
            catch (SQLException e) { LOG.log(Level.WARNING, "Failed to close connection", e); }
        }
    }
}
