package com.expensetracker.servlet;

import com.expensetracker.dao.CategoryDAO;
import com.expensetracker.model.Category;
import com.expensetracker.model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * CategoryServlet — CRUD for categories.
 *
 * GET    /api/categories
 * POST   /api/categories
 * PUT    /api/categories
 * DELETE /api/categories?id=N
 */
@WebServlet("/api/categories")
public class CategoryServlet extends HttpServlet {

    private final CategoryDAO dao = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        User user = sessionUser(req, resp); if (user == null) return;
        try {
            JSONArray arr = new JSONArray();
            dao.findAll(user.getId()).forEach(c -> arr.put(new JSONObject()
                .put("id",          c.getId())
                .put("name",        c.getName())
                .put("colorHex",    c.getColorHex())
                .put("icon",        c.getIcon())
                .put("budgetLimit", c.getBudgetLimit())));
            resp.getWriter().print(arr);
        } catch (Exception e) { resp.setStatus(500); resp.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}"); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        User user = sessionUser(req, resp); if (user == null) return;
        try {
            JSONObject b = body(req);
            Category c = new Category(user.getId(),
                b.getString("name"),
                b.optString("colorHex", "#6C63FF"),
                b.optString("icon", "tag"),
                b.has("budgetLimit") ? new BigDecimal(b.getString("budgetLimit")) : null);
            int id = dao.create(c);
            resp.getWriter().print(new JSONObject().put("success", true).put("id", id));
        } catch (Exception e) { resp.setStatus(400); resp.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}"); }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        User user = sessionUser(req, resp); if (user == null) return;
        try {
            JSONObject b = body(req);
            Category c = new Category();
            c.setId(b.getInt("id")); c.setUserId(user.getId());
            c.setName(b.getString("name"));
            c.setColorHex(b.optString("colorHex", "#6C63FF"));
            c.setIcon(b.optString("icon", "tag"));
            c.setBudgetLimit(b.has("budgetLimit") ? new BigDecimal(b.getString("budgetLimit")) : null);
            resp.getWriter().print(new JSONObject().put("success", dao.update(c)));
        } catch (Exception e) { resp.setStatus(400); resp.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}"); }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        User user = sessionUser(req, resp); if (user == null) return;
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            resp.getWriter().print(new JSONObject().put("success", dao.delete(id, user.getId())));
        } catch (Exception e) { resp.setStatus(400); resp.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}"); }
    }

    private User sessionUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        User u = (s != null) ? (User) s.getAttribute("user") : null;
        if (u == null) { resp.setStatus(401); resp.getWriter().print("{\"error\":\"Unauthorized\"}"); }
        return u;
    }

    private JSONObject body(HttpServletRequest req) throws IOException {
        return new JSONObject(req.getReader().lines().collect(Collectors.joining()));
    }
}
