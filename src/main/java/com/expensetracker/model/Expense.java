package com.expensetracker.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Expense — domain entity that maps to the `expenses` table.
 */
public class Expense {

    private int          id;
    private int          userId;
    private int          categoryId;
    private String       categoryName;   // joined field
    private String       categoryColor;  // joined field
    private String       title;
    private BigDecimal   amount;
    private LocalDate    expenseDate;
    private String       notes;
    private String       paymentMode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ── Constructors ─────────────────────────────────────────
    public Expense() {}

    public Expense(int userId, int categoryId, String title,
                   BigDecimal amount, LocalDate expenseDate,
                   String notes, String paymentMode) {
        this.userId      = userId;
        this.categoryId  = categoryId;
        this.title       = title;
        this.amount      = amount;
        this.expenseDate = expenseDate;
        this.notes       = notes;
        this.paymentMode = paymentMode;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public int getUserId()                      { return userId; }
    public void setUserId(int userId)           { this.userId = userId; }

    public int getCategoryId()                  { return categoryId; }
    public void setCategoryId(int categoryId)   { this.categoryId = categoryId; }

    public String getCategoryName()             { return categoryName; }
    public void setCategoryName(String v)       { this.categoryName = v; }

    public String getCategoryColor()            { return categoryColor; }
    public void setCategoryColor(String v)      { this.categoryColor = v; }

    public String getTitle()                    { return title; }
    public void setTitle(String title)          { this.title = title; }

    public BigDecimal getAmount()               { return amount; }
    public void setAmount(BigDecimal amount)    { this.amount = amount; }

    public LocalDate getExpenseDate()           { return expenseDate; }
    public void setExpenseDate(LocalDate d)     { this.expenseDate = d; }

    public String getNotes()                    { return notes; }
    public void setNotes(String notes)          { this.notes = notes; }

    public String getPaymentMode()              { return paymentMode; }
    public void setPaymentMode(String v)        { this.paymentMode = v; }

    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime v)   { this.createdAt = v; }

    public LocalDateTime getUpdatedAt()         { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v)   { this.updatedAt = v; }

    @Override
    public String toString() {
        return "Expense{id=" + id + ", title='" + title + "', amount=" + amount
             + ", date=" + expenseDate + '}';
    }
}
