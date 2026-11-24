package com.expensetracker.model;

import java.math.BigDecimal;

/**
 * Category — domain entity that maps to the `categories` table.
 */
public class Category {

    private int        id;
    private int        userId;
    private String     name;
    private String     colorHex;
    private String     icon;
    private BigDecimal budgetLimit;

    // Analytics fields (from stored procedure)
    private BigDecimal totalSpent;
    private int        txnCount;

    public Category() {}

    public Category(int userId, String name, String colorHex,
                    String icon, BigDecimal budgetLimit) {
        this.userId      = userId;
        this.name        = name;
        this.colorHex    = colorHex;
        this.icon        = icon;
        this.budgetLimit = budgetLimit;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }

    public int getUserId()                          { return userId; }
    public void setUserId(int userId)               { this.userId = userId; }

    public String getName()                         { return name; }
    public void setName(String name)                { this.name = name; }

    public String getColorHex()                     { return colorHex; }
    public void setColorHex(String colorHex)        { this.colorHex = colorHex; }

    public String getIcon()                         { return icon; }
    public void setIcon(String icon)                { this.icon = icon; }

    public BigDecimal getBudgetLimit()              { return budgetLimit; }
    public void setBudgetLimit(BigDecimal v)        { this.budgetLimit = v; }

    public BigDecimal getTotalSpent()               { return totalSpent; }
    public void setTotalSpent(BigDecimal v)         { this.totalSpent = v; }

    public int getTxnCount()                        { return txnCount; }
    public void setTxnCount(int v)                  { this.txnCount = v; }
}
