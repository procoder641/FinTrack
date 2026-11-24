# 💰 Smart Expense Tracker System

A full-stack expense management web application built with **Java Servlets, JDBC, MySQL, HTML5, CSS3, and JavaScript**.

---

## 🚀 Features

- **User Authentication** — Secure register/login with BCrypt password hashing and session management
- **Dashboard** — Real-time KPI cards, 12-month trend line chart, category donut chart
- **Expense CRUD** — Add, edit, delete, search, and filter expenses with pagination
- **Category Management** — Custom categories with colors, icons, and budget limits
- **Analytics Page** — Bar charts, category breakdowns, budget utilization insights
- **Stored Procedures** — MySQL stored procedures for monthly summary and trend data
- **Responsive UI** — Works on desktop and mobile browsers

---

## 🛠 Tech Stack

| Layer      | Technology                          |
|------------|-------------------------------------|
| Backend    | Java 17, Jakarta Servlets (Tomcat 10)|
| Database   | MySQL 8.0, JDBC, Stored Procedures  |
| Frontend   | HTML5, CSS3, JavaScript (ES6+)      |
| Charts     | Chart.js 4.4                        |
| Build Tool | Apache Maven                        |
| Security   | BCrypt (jBCrypt), HTTP Sessions     |

---

## 📁 Project Structure

```
SmartExpenseTracker/
├── pom.xml                          # Maven build config
├── sql/
│   └── schema.sql                   # DB schema + stored procedures + seed data
├── WebContent/
│   ├── WEB-INF/web.xml
│   └── pages/
│       ├── login.html               # Auth page
│       ├── dashboard.html           # Main dashboard
│       ├── expenses.html            # Expense CRUD
│       ├── categories.html          # Category management
│       └── analytics.html           # Advanced analytics
└── src/main/java/com/expensetracker/
    ├── model/         # User, Expense, Category POJOs
    ├── dao/           # Data Access Objects (JDBC)
    ├── servlet/       # REST-style HTTP Servlets
    └── util/          # DBConnection utility
```

---

## ⚙️ Setup & Installation

### Prerequisites
- Java 17+
- Apache Maven 3.8+
- MySQL 8.0+
- Apache Tomcat 10.x

### Step 1 — Database Setup
```sql
-- In MySQL Workbench or terminal:
mysql -u root -p < sql/schema.sql
```

### Step 2 — Configure DB Password
Edit `src/main/java/com/expensetracker/util/DBConnection.java`:
```java
private static final String PASS = "your_mysql_password";
```

### Step 3 — Build
```bash
mvn clean package
```

### Step 4 — Deploy
Copy `target/SmartExpenseTracker.war` to Tomcat's `webapps/` folder and start Tomcat.

### Step 5 — Access
```
http://localhost:8080/SmartExpenseTracker/pages/login.html
```

**Demo credentials:** `demo@expense.com` / `demo1234`

---

## 📸 Pages

| Page         | Description                              |
|--------------|------------------------------------------|
| Login        | Animated auth page with register toggle  |
| Dashboard    | KPI cards, charts, recent transactions   |
| Expenses     | Full CRUD table with search & filter     |
| Categories   | Color-coded category cards with budgets  |
| Analytics    | Trend, donut, bar charts + insights      |

---

## 🗃️ Database Design

- `users` — Authentication + monthly budget
- `categories` — Per-user categories with color/icon/budget
- `expenses` — Core transaction records
- `sp_monthly_category_summary` — Stored procedure for dashboard analytics
- `sp_monthly_trend` — Stored procedure for 12-month trend data

---

## 👨‍💻 Author

Built as a full-stack Java project demonstrating JDBC, Servlet architecture, and responsive frontend design.
