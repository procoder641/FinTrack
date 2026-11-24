package com.expensetracker.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class User {

    private int           id;
    private String        name;
    private String        email;
    private String        password;          // BCrypt hash — never expose in JSON
    private BigDecimal    monthlyBudget;
    private LocalDateTime createdAt;

    public User() {}

    public User(String name, String email, String password, BigDecimal monthlyBudget) {
        this.name          = name;
        this.email         = email;
        this.password      = password;
        this.monthlyBudget = monthlyBudget;
    }

    public int getId()                           { return id; }
    public void setId(int id)                    { this.id = id; }

    public String getName()                      { return name; }
    public void setName(String name)             { this.name = name; }

    public String getEmail()                     { return email; }
    public void setEmail(String email)           { this.email = email; }

    public String getPassword()                  { return password; }
    public void setPassword(String password)     { this.password = password; }

    public BigDecimal getMonthlyBudget()         { return monthlyBudget; }
    public void setMonthlyBudget(BigDecimal v)   { this.monthlyBudget = v; }

    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime v)    { this.createdAt = v; }
}
