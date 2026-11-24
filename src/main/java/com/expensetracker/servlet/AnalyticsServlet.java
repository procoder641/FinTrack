package com.expensetracker.servlet;

import com.expensetracker.dao.CategoryDAO;
import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.model.Category;
import com.expensetracker.model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * AnalyticsServlet — powers the dashboard charts.
 *
 * GET /api/analytics?type=summary&year=YYYY&month=MM  → category breakdown
 * GET /api/analytics?type=trend                       → 12-month line chart
 * GET /api/analytics?type=overview&year=YYYY&month=MM → totals card
 */
@WebServlet("/api/analytics")
public class AnalyticsServlet extends HttpServlet {

    private final CategoryDAO catDao = new CategoryDAO();
    private final ExpenseDAO  expDao = new ExpenseDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) { resp.setStatus(401); resp.getWriter().print("{\"error\":\"Unauthorized\"}"); return; }

        String type = req.getParameter("type");
        LocalDate now = LocalDate.now();
        int year  = parseIntOrDefault(req.getParameter("year"),  now.getYear());
        int month = parseIntOrDefault(req.getParameter("month"), now.getMonthValue());

        try {
            switch (type == null ? "summary" : type) {
                case "summary" -> {
                    List<Category> cats = catDao.getMonthlySummary(user.getId(), year, month);
                    JSONArray arr = new JSONArray();
                    for (Category c : cats) {
                        arr.put(new JSONObject()
                            .put("id",          c.getId())
                            .put("name",        c.getName())
                            .put("color",       c.getColorHex())
                            .put("icon",        c.getIcon())
                            .put("budgetLimit", c.getBudgetLimit())
                            .put("totalSpent",  c.getTotalSpent())
                            .put("txnCount",    c.getTxnCount()));
                    }
                    resp.getWriter().print(arr);
                }
                case "trend" -> {
                    List<String[]> trend = catDao.getMonthlyTrend(user.getId());
                    JSONArray labels = new JSONArray();
                    JSONArray values = new JSONArray();
                    for (String[] row : trend) { labels.put(row[0]); values.put(Double.parseDouble(row[1])); }
                    resp.getWriter().print(new JSONObject().put("labels", labels).put("values", values));
                }
                case "overview" -> {
                    var total   = expDao.getMonthlyTotal(user.getId(), year, month);
                    var budget  = user.getMonthlyBudget();
                    var remain  = budget.subtract(total);
                    resp.getWriter().print(new JSONObject()
                        .put("monthlyTotal",   total)
                        .put("monthlyBudget",  budget)
                        .put("remaining",      remain)
                        .put("percentUsed",    budget.compareTo(java.math.BigDecimal.ZERO) > 0
                            ? total.divide(budget, 4, java.math.RoundingMode.HALF_UP)
                                   .multiply(new java.math.BigDecimal("100")).intValue()
                            : 0));
                }
                default -> { resp.setStatus(400); resp.getWriter().print("{\"error\":\"Unknown type\"}"); }
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private int parseIntOrDefault(String val, int def) {
        try { return val != null ? Integer.parseInt(val) : def; }
        catch (NumberFormatException e) { return def; }
    }
}
