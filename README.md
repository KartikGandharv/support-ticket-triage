# 🧠 Support Ticket Triage System

## 🚀 Overview

This is a **full-stack application** that analyzes and categorizes support tickets using rule-based AI logic.
It automatically assigns **priority** and **category** to incoming tickets and stores them for further processing.

The goal of this project is to simulate how real-world support systems reduce manual effort in ticket handling.

---

## 🛠 Tech Stack

### Backend

* Java + Spring Boot
* Spring Data JPA
* PostgreSQL

### Frontend

* React.js
* Fetch API

### DevOps

* Docker
* Docker Compose

---

## ⚙️ Features

* 📩 Submit support tickets
* 🤖 Automatic categorization (BUG, BILLING, FEATURE)
* 🚦 Priority detection (LOW, MEDIUM, HIGH)
* 📋 View all submitted tickets
* 🧠 Rule-based AI logic for analysis
* 🔗 REST APIs for communication

---

## 🧠 AI Logic (Rule-Based)

The system analyzes ticket text using simple keyword matching:

| Keywords        | Category | Priority |
| --------------- | -------- | -------- |
| payment, refund | BILLING  | HIGH     |
| error, bug      | BUG      | MEDIUM   |
| others          | FEATURE  | LOW      |

---

## 📁 Project Structure

support-ticket-triage/
│
├── backend/        # Spring Boot application
├── frontend/       # React application
├── docker-compose.yml
└── README.md

---

## ▶️ Run Locally

### 🔹 Backend

cd backend
mvn clean install
mvn spring-boot:run

---

### 🔹 Frontend

cd frontend
npm install
npm start

---

## 🐳 Run with Docker

docker-compose up --build

---

## 📡 API Endpoints

| Method | Endpoint | Description       |
| ------ | -------- | ----------------- |
| POST   | /tickets | Create new ticket |
| GET    | /tickets | Get all tickets   |

---

## 📸 Screenshots (Optional)

*Add UI screenshots here for better presentation*

---

## 🎯 Future Improvements

* Add JWT Authentication
* Pagination & Filtering
* Advanced AI/ML-based classification
* Role-based access control
* Dashboard analytics

---

## 👨‍💻 Author

**Kartik Gandharv**

---

## ⭐ Contribute

Feel free to fork this repo and improve it!

---
