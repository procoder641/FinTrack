💰 FinTrack – Smart Expense Tracker

A full-stack personal expense management web application built using Java Servlets, JDBC, MySQL, HTML5, CSS3, and JavaScript. FinTrack helps users manage daily expenses, track spending patterns, set budgets, and analyze their financial activities through interactive dashboards.

🚀 Live Demo

Live Application: https://fintrack-production-9afa8444.up.railway.app

✨ Features

- User Authentication — Secure registration and login with BCrypt password hashing and session management.
- Dashboard — Real-time KPI cards, 12-month spending trends, and category-wise charts.
- Expense Management — Add, edit, delete, search, filter, and paginate expenses.
- Category Management — Custom categories with colors, icons, and budget limits.
- Analytics — Interactive charts, category breakdowns, and budget utilization insights.
- Database Procedures — MySQL stored procedures for monthly summaries and spending trends.
- Responsive UI — Compatible with desktop and mobile browsers.

🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Jakarta Servlets |
| Database | MySQL 8.0, JDBC |
| Frontend | HTML5, CSS3, JavaScript (ES6+) |
| Charts | Chart.js 4.4 |
| Build Tool | Apache Maven |
| Server | Apache Tomcat 10.1 |
| Security | BCrypt, HTTP Sessions |
| Containerization | Docker |
| Deployment | Railway |

🐳 Docker & Deployment

- Created a multi-stage Dockerfile for application deployment.
- Used Maven to build the application into a WAR file.
- Used Apache Tomcat 10.1 with Java 17 to run the application.
- Configured the application for container-based deployment.
- Deployed the live application on Railway.

📁 Project Structure

FinTrack/
├── Dockerfile
├── pom.xml
├── sql/
│   └── schema.sql
├── WebContent/
│   ├── WEB-INF/
│   │   └── web.xml
│   └── pages/
│       ├── login.html
│       ├── dashboard.html
│       ├── expenses.html
│       ├── categories.html
│       └── analytics.html
└── src/main/java/com/expensetracker/
    ├── model/
    ├── dao/
    ├── servlet/
    └── util/

⚙️ Local Setup & Installation

Prerequisites

- Java 17+
- Apache Maven 3.8+
- MySQL 8.0+
- Apache Tomcat 10.x

Step 1 – Clone Repository

git clone https://github.com/procoder641/FinTrack.git
cd FinTrack

Step 2 – Database Setup

Import the database schema:

mysql -u root -p < sql/schema.sql

Step 3 – Configure Database

Configure your MySQL connection details in:

"src/main/java/com/expensetracker/util/DBConnection.java"

Step 4 – Build Application

mvn clean package

Step 5 – Deploy on Tomcat

Copy the generated WAR file from the "target" directory into Tomcat's "webapps" folder and start Tomcat.

Step 6 – Access Live Application

[Open FinTrack](https://fintrack-production-9afa8444.up.railway.app)


🗄️ Database Design

- "users" – User authentication and monthly budget information.
- "categories" – User-specific expense categories.
- "expenses" – Expense transaction records.
- "sp_monthly_category_summary" – Monthly category-wise expense summary.
- "sp_monthly_trend" – Monthly spending trend data.

📸 Application Pages

Page| Description
Login & Registration| User authentication
Dashboard| Financial overview and charts
Expenses| Expense CRUD, search and filters
Categories| Category and budget management
Analytics| Spending trends and insights

👨‍💻 Author

Developed as a full-stack Java web application demonstrating practical implementation of Java Servlets, JDBC, MySQL, database operations, frontend development, and Docker-based deployment.
