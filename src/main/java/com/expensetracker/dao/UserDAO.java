package com.expensetracker.dao;

import com.expensetracker.model.User;
import com.expensetracker.util.DBConnection;

import java.sql.*;

/**
 * UserDAO — authentication and profile operations.
 */
public class UserDAO {

    private static final String SQL_INSERT =
        "INSERT INTO users (name, email, password, monthly_budget) VALUES (?, ?, ?, ?)";
    private static final String SQL_FIND_BY_EMAIL =
        "SELECT * FROM users WHERE email=?";
    private static final String SQL_FIND_BY_ID =
        "SELECT * FROM users WHERE id=?";
    private static final String SQL_UPDATE_BUDGET =
        "UPDATE users SET monthly_budget=? WHERE id=?";
    private static final String SQL_EMAIL_EXISTS =
        "SELECT COUNT(*) FROM users WHERE email=?";

    public int create(User u) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString    (1, u.getName());
            ps.setString    (2, u.getEmail());
            ps.setString    (3, u.getPassword());
            ps.setBigDecimal(4, u.getMonthlyBudget());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    public User findByEmail(String email) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public User findById(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean emailExists(String email) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EMAIL_EXISTS)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean updateBudget(int userId, java.math.BigDecimal budget) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_BUDGET)) {
            ps.setBigDecimal(1, budget);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId           (rs.getInt       ("id"));
        u.setName         (rs.getString    ("name"));
        u.setEmail        (rs.getString    ("email"));
        u.setPassword     (rs.getString    ("password"));
        u.setMonthlyBudget(rs.getBigDecimal("monthly_budget"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) u.setCreatedAt(ts.toLocalDateTime());
        return u;
    }
}
