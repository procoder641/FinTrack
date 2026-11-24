-- ============================================================
--  Smart Expense Tracker System — Database Schema
--  Author : [Your Name]
--  DB     : MySQL 8.0+
-- ============================================================

 -- CREATE DATABASE IF NOT EXISTS expense_tracker
  -- CHARACTER SET utf8mb4
 -- COLLATE utf8mb4_unicode_ci;

-- USE expense_tracker;

-- ────────────────────────────────────────────────────────────
--  USERS
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id          INT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,          -- BCrypt hash
    monthly_budget DECIMAL(12,2) DEFAULT 0.00,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_users_email (email)
) ENGINE=InnoDB;

-- ────────────────────────────────────────────────────────────
--  CATEGORIES
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS categories (
    id          INT          NOT NULL AUTO_INCREMENT,
    user_id     INT          NOT NULL,
    name        VARCHAR(80)  NOT NULL,
    color_hex   CHAR(7)      NOT NULL DEFAULT '#6C63FF',
    icon        VARCHAR(50)  NOT NULL DEFAULT 'tag',
    budget_limit DECIMAL(12,2) DEFAULT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_user_category (user_id, name),
    CONSTRAINT fk_cat_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_cat_user (user_id)
) ENGINE=InnoDB;

-- ────────────────────────────────────────────────────────────
--  EXPENSES
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS expenses (
    id           INT            NOT NULL AUTO_INCREMENT,
    user_id      INT            NOT NULL,
    category_id  INT            NOT NULL,
    title        VARCHAR(200)   NOT NULL,
    amount       DECIMAL(12,2)  NOT NULL CHECK (amount > 0),
    expense_date DATE           NOT NULL,
    notes        TEXT           DEFAULT NULL,
    payment_mode ENUM('cash','card','upi','netbanking','other')
                               NOT NULL DEFAULT 'cash',
    created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
                                        ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_exp_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_exp_cat FOREIGN KEY (category_id)
        REFERENCES categories(id) ON DELETE RESTRICT,
    INDEX idx_exp_user_date  (user_id, expense_date),
    INDEX idx_exp_category   (category_id),
    INDEX idx_exp_date       (expense_date)
) ENGINE=InnoDB;

-- ────────────────────────────────────────────────────────────
--  STORED PROCEDURES
-- ────────────────────────────────────────────────────────────
DELIMITER $$

-- Monthly summary per category
CREATE PROCEDURE sp_monthly_category_summary(
    IN p_user_id INT,
    IN p_year    INT,
    IN p_month   INT
)
BEGIN
    SELECT
        c.id            AS category_id,
        c.name          AS category_name,
        c.color_hex,
        c.icon,
        c.budget_limit,
        COALESCE(SUM(e.amount), 0) AS total_spent,
        COUNT(e.id)                AS txn_count
    FROM categories c
    LEFT JOIN expenses e
        ON  e.category_id = c.id
        AND e.user_id     = p_user_id
        AND YEAR(e.expense_date)  = p_year
        AND MONTH(e.expense_date) = p_month
    WHERE c.user_id = p_user_id
    GROUP BY c.id, c.name, c.color_hex, c.icon, c.budget_limit
    ORDER BY total_spent DESC;
END$$

-- Last 12-month trend
CREATE PROCEDURE sp_monthly_trend(IN p_user_id INT)
BEGIN
    SELECT
        DATE_FORMAT(expense_date, '%Y-%m') AS month_label,
        SUM(amount)                        AS total
    FROM expenses
    WHERE user_id = p_user_id
      AND expense_date >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
    GROUP BY month_label
    ORDER BY month_label;
END$$

DELIMITER ;

-- ────────────────────────────────────────────────────────────
--  SEED DATA  (demo user  — password: demo1234)
-- ────────────────────────────────────────────────────────────
INSERT INTO users (name, email, password, monthly_budget) VALUES
('Demo User', 'demo@expense.com',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lHHi',
 25000.00);

INSERT INTO categories (user_id, name, color_hex, icon, budget_limit) VALUES
(1, 'Food & Dining',   '#FF6B6B', 'utensils',      6000),
(1, 'Transport',       '#4ECDC4', 'car',           3000),
(1, 'Shopping',        '#45B7D1', 'shopping-bag',  5000),
(1, 'Entertainment',   '#96CEB4', 'film',          2000),
(1, 'Health',          '#FFEAA7', 'heart-pulse',   2000),
(1, 'Utilities',       '#DDA0DD', 'zap',           3000),
(1, 'Education',       '#98D8C8', 'book-open',     2000),
(1, 'Others',          '#B0B0B0', 'more-horizontal',2000);

-- Sample expenses (current month)
INSERT INTO expenses (user_id, category_id, title, amount, expense_date, payment_mode, notes) VALUES
(1, 1, 'Zomato Order',        450.00, CURDATE(),                         'upi',  'Dinner'),
(1, 2, 'Ola Cab to Office',   280.00, CURDATE() - INTERVAL 1 DAY,       'upi',  NULL),
(1, 1, 'Grocery - Big Bazaar',1850.00,CURDATE() - INTERVAL 2 DAY,       'card', 'Monthly groceries'),
(1, 3, 'Amazon Purchase',     1299.00,CURDATE() - INTERVAL 3 DAY,       'card', 'Keyboard'),
(1, 5, 'Pharmacy',            380.00, CURDATE() - INTERVAL 4 DAY,       'cash', NULL),
(1, 6, 'Electricity Bill',    1240.00,CURDATE() - INTERVAL 5 DAY,       'netbanking','Monthly'),
(1, 4, 'Movie Ticket - PVR',  600.00, CURDATE() - INTERVAL 6 DAY,       'card', NULL),
(1, 7, 'Udemy Course',        499.00, CURDATE() - INTERVAL 7 DAY,       'card', 'Java course'),
(1, 2, 'Petrol',              800.00, CURDATE() - INTERVAL 8 DAY,       'cash', NULL),
(1, 1, 'Swiggy',              320.00, CURDATE() - INTERVAL 9 DAY,       'upi',  NULL);
