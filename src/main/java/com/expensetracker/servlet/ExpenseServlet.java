package com.expensetracker.servlet;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ExpenseServlet — handles CRUD for expenses via JSON API.
 *
 * GET    /api/expenses          → list all
 * GET    /api/expenses?id=N     → get one
 * GET    /api/expenses?month=YYYY-MM → filter by month
 * GET    /api/expenses?q=text   → search
 * POST   /api/expenses          → create
 * PUT    /api/expenses          → update
 * DELETE /api/expenses?id=N     → delete
 */
@WebServlet("/api/expenses")
public class ExpenseServlet extends HttpServlet {

    private final ExpenseDAO dao = new ExpenseDAO();

    // ── GET ───────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        User user = getSessionUser(req, resp); if (user == null) return;

        try (PrintWriter out = resp.getWriter()) {
            String id    = req.getParameter("id");
            String month = req.getParameter("month");   // "YYYY-MM"
            String q     = req.getParameter("q");
            String catId = req.getParameter("category");

            if (id != null) {
                Expense e = dao.findById(Integer.parseInt(id), user.getId());
                out.print(e != null ? toJson(e) : error("Not found"));
            } else if (month != null) {
                String[] parts = month.split("-");
                List<Expense> list = dao.findByMonth(user.getId(),
                    Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                out.print(toJsonArray(list));
            } else if (q != null && !q.isBlank()) {
                out.print(toJsonArray(dao.search(user.getId(), q.trim())));
            } else if (catId != null) {
                out.print(toJsonArray(dao.findByCategory(user.getId(), Integer.parseInt(catId))));
            } else {
                out.print(toJsonArray(dao.findAll(user.getId())));
            }
        } catch (SQLException | NumberFormatException e) {
            resp.setStatus(500);
            resp.getWriter().print(error(e.getMessage()));
        }
    }

    // ── POST ──────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        User user = getSessionUser(req, resp); if (user == null) return;

        try {
            JSONObject body = parseBody(req);
            Expense e = new Expense();
            e.setUserId     (user.getId());
            e.setCategoryId (body.getInt   ("categoryId"));
            e.setTitle      (body.getString("title").trim());
            e.setAmount     (new BigDecimal(body.getString("amount")));
            e.setExpenseDate(LocalDate.parse(body.getString("expenseDate")));
            e.setNotes      (body.optString("notes", null));
            e.setPaymentMode(body.optString("paymentMode", "cash"));

            int newId = dao.create(e);
            JSONObject res = new JSONObject();
            res.put("success", true).put("id", newId);
            resp.getWriter().print(res);
        } catch (Exception ex) {
            resp.setStatus(400);
            resp.getWriter().print(error(ex.getMessage()));
        }
    }

    // ── PUT ───────────────────────────────────────────────────
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        User user = getSessionUser(req, resp); if (user == null) return;

        try {
            JSONObject body = parseBody(req);
            Expense e = new Expense();
            e.setId         (body.getInt   ("id"));
            e.setUserId     (user.getId());
            e.setCategoryId (body.getInt   ("categoryId"));
            e.setTitle      (body.getString("title").trim());
            e.setAmount     (new BigDecimal(body.getString("amount")));
            e.setExpenseDate(LocalDate.parse(body.getString("expenseDate")));
            e.setNotes      (body.optString("notes", null));
            e.setPaymentMode(body.optString("paymentMode", "cash"));

            boolean ok = dao.update(e);
            resp.getWriter().print(new JSONObject().put("success", ok));
        } catch (Exception ex) {
            resp.setStatus(400);
            resp.getWriter().print(error(ex.getMessage()));
        }
    }

    // ── DELETE ────────────────────────────────────────────────
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        User user = getSessionUser(req, resp); if (user == null) return;

        try {
            int id = Integer.parseInt(req.getParameter("id"));
            boolean ok = dao.delete(id, user.getId());
            resp.getWriter().print(new JSONObject().put("success", ok));
        } catch (Exception ex) {
            resp.setStatus(400);
            resp.getWriter().print(error(ex.getMessage()));
        }
    }

    // ── Helpers ───────────────────────────────────────────────
    private User getSessionUser(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            resp.setStatus(401);
            resp.getWriter().print(error("Unauthorized"));
        }
        return user;
    }

    private JSONObject parseBody(HttpServletRequest req) throws IOException {
        String body = req.getReader().lines().collect(Collectors.joining());
        return new JSONObject(body);
    }

    private JSONObject toJson(Expense e) {
        return new JSONObject()
            .put("id",            e.getId())
            .put("categoryId",    e.getCategoryId())
            .put("categoryName",  e.getCategoryName())
            .put("categoryColor", e.getCategoryColor())
            .put("title",         e.getTitle())
            .put("amount",        e.getAmount())
            .put("expenseDate",   e.getExpenseDate().toString())
            .put("notes",         e.getNotes() != null ? e.getNotes() : "")
            .put("paymentMode",   e.getPaymentMode())
            .put("createdAt",     e.getCreatedAt() != null ? e.getCreatedAt().toString() : "");
    }

    private JSONArray toJsonArray(List<Expense> list) {
        JSONArray arr = new JSONArray();
        list.forEach(e -> arr.put(toJson(e)));
        return arr;
    }

    private String error(String msg) {
        return new JSONObject().put("error", msg).toString();
    }
}
