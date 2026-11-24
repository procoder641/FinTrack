package com.expensetracker.dao;

import com.expensetracker.model.Category;
import com.expensetracker.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CategoryDAO — CRUD + analytics for Category entity.
 */
public class CategoryDAO {

    private static final String SQL_INSERT =
        "INSERT INTO categories (user_id, name, color_hex, icon, budget_limit) " +
        "VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
        "UPDATE categories SET name=?, color_hex=?, icon=?, budget_limit=? " +
        "WHERE id=? AND user_id=?";
    private static final String SQL_DELETE =
        "DELETE FROM categories WHERE id=? AND user_id=?";
    private static final String SQL_FIND_ALL =
        "SELECT * FROM categories WHERE user_id=? ORDER BY name";
    private static final String SQL_FIND_BY_ID =
        "SELECT * FROM categories WHERE id=? AND user_id=?";
    private static final String SP_MONTHLY_SUMMARY =
        "{CALL sp_monthly_category_summary(?, ?, ?)}";
    private static final String SP_MONTHLY_TREND =
        "{CALL sp_monthly_trend(?)}";

    // ── CREATE ────────────────────────────────────────────────
    public int create(Category c) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt       (1, c.getUserId());
            ps.setString    (2, c.getName());
            ps.setString    (3, c.getColorHex());
            ps.setString    (4, c.getIcon());
            if (c.getBudgetLimit() != null)
                ps.setBigDecimal(5, c.getBudgetLimit());
            else
                ps.setNull(5, Types.DECIMAL);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // ── READ ──────────────────────────────────────────────────
    public List<Category> findAll(int userId) throws SQLException {
        List<Category> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Category findById(int id, int userId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── UPDATE ────────────────────────────────────────────────
    public boolean update(Category c) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getColorHex());
            ps.setString(3, c.getIcon());
            if (c.getBudgetLimit() != null)
                ps.setBigDecimal(4, c.getBudgetLimit());
            else
                ps.setNull(4, Types.DECIMAL);
            ps.setInt(5, c.getId());
            ps.setInt(6, c.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ────────────────────────────────────────────────
    public boolean delete(int id, int userId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── ANALYTICS: monthly summary (calls stored procedure) ───
    public List<Category> getMonthlySummary(int userId, int year, int month) throws SQLException {
        List<Category> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(SP_MONTHLY_SUMMARY)) {
            cs.setInt(1, userId);
            cs.setInt(2, year);
            cs.setInt(3, month);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Category c = new Category();
                    c.setId          (rs.getInt       ("category_id"));
                    c.setName        (rs.getString    ("category_name"));
                    c.setColorHex    (rs.getString    ("color_hex"));
                    c.setIcon        (rs.getString    ("icon"));
                    c.setBudgetLimit (rs.getBigDecimal("budget_limit"));
                    c.setTotalSpent  (rs.getBigDecimal("total_spent"));
                    c.setTxnCount    (rs.getInt       ("txn_count"));
                    list.add(c);
                }
            }
        }
        return list;
    }

    // ── ANALYTICS: 12-month trend (calls stored procedure) ────
    public List<String[]> getMonthlyTrend(int userId) throws SQLException {
        List<String[]> trend = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(SP_MONTHLY_TREND)) {
            cs.setInt(1, userId);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    trend.add(new String[]{
                        rs.getString    ("month_label"),
                        rs.getBigDecimal("total").toPlainString()
                    });
                }
            }
        }
        return trend;
    }

    // ── ROW MAPPER ────────────────────────────────────────────
    private Category mapRow(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId         (rs.getInt       ("id"));
        c.setUserId     (rs.getInt       ("user_id"));
        c.setName       (rs.getString    ("name"));
        c.setColorHex   (rs.getString    ("color_hex"));
        c.setIcon       (rs.getString    ("icon"));
        c.setBudgetLimit(rs.getBigDecimal("budget_limit"));
        return c;
    }
}
