package com.expensetracker.dao;

import com.expensetracker.model.Expense;
import com.expensetracker.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ExpenseDAO — all database operations for the Expense entity.
 * Uses PreparedStatements throughout to prevent SQL injection.
 */
public class ExpenseDAO {

    private static final Logger LOG = Logger.getLogger(ExpenseDAO.class.getName());

    // ── SQL Constants ─────────────────────────────────────────────────────────
    private static final String SQL_INSERT =
        "INSERT INTO expenses (user_id, category_id, title, amount, expense_date, notes, payment_mode) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
        "UPDATE expenses SET category_id=?, title=?, amount=?, expense_date=?, " +
        "notes=?, payment_mode=? WHERE id=? AND user_id=?";

    private static final String SQL_DELETE =
        "DELETE FROM expenses WHERE id=? AND user_id=?";

    private static final String SQL_FIND_BY_ID =
        "SELECT e.*, c.name AS cat_name, c.color_hex " +
        "FROM expenses e JOIN categories c ON e.category_id = c.id " +
        "WHERE e.id=? AND e.user_id=?";

    private static final String SQL_FIND_ALL =
        "SELECT e.*, c.name AS cat_name, c.color_hex " +
        "FROM expenses e JOIN categories c ON e.category_id = c.id " +
        "WHERE e.user_id=? ORDER BY e.expense_date DESC, e.created_at DESC";

    private static final String SQL_FIND_BY_MONTH =
        "SELECT e.*, c.name AS cat_name, c.color_hex " +
        "FROM expenses e JOIN categories c ON e.category_id = c.id " +
        "WHERE e.user_id=? AND YEAR(e.expense_date)=? AND MONTH(e.expense_date)=? " +
        "ORDER BY e.expense_date DESC";

    private static final String SQL_FIND_BY_CATEGORY =
        "SELECT e.*, c.name AS cat_name, c.color_hex " +
        "FROM expenses e JOIN categories c ON e.category_id = c.id " +
        "WHERE e.user_id=? AND e.category_id=? ORDER BY e.expense_date DESC";

    private static final String SQL_SEARCH =
        "SELECT e.*, c.name AS cat_name, c.color_hex " +
        "FROM expenses e JOIN categories c ON e.category_id = c.id " +
        "WHERE e.user_id=? AND (e.title LIKE ? OR e.notes LIKE ?) " +
        "ORDER BY e.expense_date DESC LIMIT 50";

    private static final String SQL_MONTHLY_TOTAL =
        "SELECT COALESCE(SUM(amount), 0) FROM expenses " +
        "WHERE user_id=? AND YEAR(expense_date)=? AND MONTH(expense_date)=?";

    // ── CREATE ────────────────────────────────────────────────────────────────
    public int create(Expense e) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt   (1, e.getUserId());
            ps.setInt   (2, e.getCategoryId());
            ps.setString(3, e.getTitle());
            ps.setBigDecimal(4, e.getAmount());
            ps.setDate  (5, Date.valueOf(e.getExpenseDate()));
            ps.setString(6, e.getNotes());
            ps.setString(7, e.getPaymentMode());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // ── READ (single) ─────────────────────────────────────────────────────────
    public Expense findById(int id, int userId) throws SQLException {
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

    // ── READ (all for user) ───────────────────────────────────────────────────
    public List<Expense> findAll(int userId) throws SQLException {
        List<Expense> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── READ (by month) ───────────────────────────────────────────────────────
    public List<Expense> findByMonth(int userId, int year, int month) throws SQLException {
        List<Expense> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_MONTH)) {
            ps.setInt(1, userId);
            ps.setInt(2, year);
            ps.setInt(3, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── READ (by category) ────────────────────────────────────────────────────
    public List<Expense> findByCategory(int userId, int categoryId) throws SQLException {
        List<Expense> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_CATEGORY)) {
            ps.setInt(1, userId);
            ps.setInt(2, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────
    public List<Expense> search(int userId, String keyword) throws SQLException {
        List<Expense> list = new ArrayList<>();
        String like = "%" + keyword + "%";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SEARCH)) {
            ps.setInt   (1, userId);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean update(Expense e) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setInt       (1, e.getCategoryId());
            ps.setString    (2, e.getTitle());
            ps.setBigDecimal(3, e.getAmount());
            ps.setDate      (4, Date.valueOf(e.getExpenseDate()));
            ps.setString    (5, e.getNotes());
            ps.setString    (6, e.getPaymentMode());
            ps.setInt       (7, e.getId());
            ps.setInt       (8, e.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean delete(int id, int userId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── MONTHLY TOTAL ─────────────────────────────────────────────────────────
    public BigDecimal getMonthlyTotal(int userId, int year, int month) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_MONTHLY_TOTAL)) {
            ps.setInt(1, userId);
            ps.setInt(2, year);
            ps.setInt(3, month);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        }
        return BigDecimal.ZERO;
    }

    // ── ROW MAPPER ────────────────────────────────────────────────────────────
    private Expense mapRow(ResultSet rs) throws SQLException {
        Expense e = new Expense();
        e.setId          (rs.getInt   ("id"));
        e.setUserId      (rs.getInt   ("user_id"));
        e.setCategoryId  (rs.getInt   ("category_id"));
        e.setCategoryName(rs.getString("cat_name"));
        e.setCategoryColor(rs.getString("color_hex"));
        e.setTitle       (rs.getString("title"));
        e.setAmount      (rs.getBigDecimal("amount"));
        Date d = rs.getDate("expense_date");
        if (d != null) e.setExpenseDate(d.toLocalDate());
        e.setNotes       (rs.getString("notes"));
        e.setPaymentMode (rs.getString("payment_mode"));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) e.setCreatedAt(ca.toLocalDateTime());
        return e;
    }
}
