package com.expensetracker.servlet;

import com.expensetracker.dao.CategoryDAO;
import com.expensetracker.dao.UserDAO;
import com.expensetracker.model.Category;
import com.expensetracker.model.User;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * AuthServlet — handles login, registration, and logout.
 *
 * POST /api/auth?action=login    → { email, password }
 * POST /api/auth?action=register → { name, email, password, budget }
 * POST /api/auth?action=logout
 * GET  /api/auth?action=me       → current user info
 */
@WebServlet("/api/auth")
public class AuthServlet extends HttpServlet {

    private final UserDAO     userDao = new UserDAO();
    private final CategoryDAO catDao  = new CategoryDAO();

    // Default categories for new users
    private static final String[][] DEFAULT_CATS = {
        {"Food & Dining", "#FF6B6B", "utensils"},
        {"Transport",     "#4ECDC4", "car"},
        {"Shopping",      "#45B7D1", "shopping-bag"},
        {"Entertainment", "#96CEB4", "film"},
        {"Health",        "#FFEAA7", "heart-pulse"},
        {"Utilities",     "#DDA0DD", "zap"},
        {"Education",     "#98D8C8", "book-open"},
        {"Others",        "#B0B0B0", "more-horizontal"}
    };

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getParameter("action");
        String body   = req.getReader().lines().collect(Collectors.joining());
        JSONObject json = body.isBlank() ? new JSONObject() : new JSONObject(body);

        try {
            switch (action == null ? "" : action) {
                case "login"    -> handleLogin   (req, resp, json);
                case "register" -> handleRegister(req, resp, json);
                case "logout"   -> handleLogout  (req, resp);
                default -> { resp.setStatus(400); resp.getWriter().print("{\"error\":\"Unknown action\"}"); }
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) { resp.setStatus(401); resp.getWriter().print("{\"auth\":false}"); return; }
        resp.getWriter().print(new JSONObject()
            .put("auth",          true)
            .put("id",            user.getId())
            .put("name",          user.getName())
            .put("email",         user.getEmail())
            .put("monthlyBudget", user.getMonthlyBudget()));
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp, JSONObject body)
            throws Exception {
        String email    = body.getString("email").trim().toLowerCase();
        String password = body.getString("password");
        User user = userDao.findByEmail(email);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            resp.setStatus(401);
            resp.getWriter().print("{\"error\":\"Invalid credentials\"}");
            return;
        }
        HttpSession session = req.getSession(true);
        session.setAttribute("user", user);
        session.setMaxInactiveInterval(3600 * 8); // 8 hours
        resp.getWriter().print(new JSONObject()
            .put("success", true)
            .put("name",    user.getName())
            .put("id",      user.getId()));
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp, JSONObject body)
            throws Exception {
        String email = body.getString("email").trim().toLowerCase();
        if (userDao.emailExists(email)) {
            resp.setStatus(409);
            resp.getWriter().print("{\"error\":\"Email already registered\"}");
            return;
        }
        String hashed = BCrypt.hashpw(body.getString("password"), BCrypt.gensalt(10));
        User user = new User(
            body.getString("name").trim(),
            email,
            hashed,
            new BigDecimal(body.optString("budget", "0"))
        );
        int userId = userDao.create(user);
        user.setId(userId);

        // Seed default categories
        for (String[] cat : DEFAULT_CATS) {
            Category c = new Category(userId, cat[0], cat[1], cat[2], null);
            catDao.create(c);
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("user", user);
        resp.setStatus(201);
        resp.getWriter().print(new JSONObject().put("success", true).put("id", userId));
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        resp.getWriter().print("{\"success\":true}");
    }
}
