# RevPay - Digital Wallet & Business Payments Platform

## Project Description

RevPay is a high-integrity, monolithic financial web application engineered to bridge the gap between personal asset management and business fiscal operations.

It enables users to execute secure digital payments, manage multi-tier wallets, and access business-grade credit facilities.

The platform is designed with a **Security-First mindset**, featuring Role-Based Access Control (RBAC), real-time transaction auditing, and a robust notification engine to ensure transparency and trust in every exchange.

---

# Table of Contents

* Features
* Architecture Overview
* Repository Structure
* Installation
* Usage & Configuration
* Example API Endpoints
* Testing & QA

---

# Features

## Personal Account Suite

### Wallet Operations

* Simulated **Add Funds** from linked cards
* **Withdraw funds** to bank accounts
* Real-time wallet balance updates

### P2P Ecosystem

* Send or request money via:

  * Username
  * Email
  * Account ID
* Real-time balance validation before transaction processing

### Transaction Management

* Complete transaction history
* Advanced filtering options:

  * Transaction type
  * Date
  * Amount
* Export transaction reports to **CSV/PDF**

### Notification Engine

* Low balance alerts
* Card update notifications
* Payment and request updates

---

## Business Account Suite (Extended)

### Merchant Invoicing

* Generate **itemized invoices**
* Automatic **tax calculations**
* Customer notification system

### Credit Facilities

Full **Business Loan Lifecycle Management**

* Loan Application
* Document Upload
* Approval Workflow
* Repayment Tracking

### Business Analytics

Dedicated business dashboard displaying:

* Revenue trends
* Outstanding invoice aging
* Customer transaction metrics

---

# Architecture Overview

RevPay follows a **Layered Monolithic Architecture**, ensuring strict separation of concerns and high maintainability.

## Presentation Layer

* Responsive **Thymeleaf templates**
* **Bootstrap 5 UI framework**

## Business Logic Layer

* Domain-driven services implemented in `com.revpay.service.impl`
* Ensures **ACID-compliant financial transaction handling**

## Persistence Layer

* **Spring Data JPA**
* **Hibernate ORM**
* Supports **H2 / PostgreSQL database**

## Security Layer

* **Spring Security**
* **BCrypt password hashing**
* **Session-based authentication**
* **Role-Based Access Control (RBAC)**

---

# Repository Structure

```
revpay-app/
├── src/
│   ├── main/
│   │   ├── java/com/revpay/
│   │   │   ├── config/          # Security & App Configurations
│   │   │   ├── controller/      # REST & Web Controllers
│   │   │   ├── dto/             # Data Transfer Objects & Validation
│   │   │   ├── model/           # JPA Entities & Enums
│   │   │   ├── repository/      # Data Access Layer
│   │   │   └── service/         # Business Logic Interfaces & Implementations
│   │
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf UI Templates
│   │       └── application.properties
│
│   └── test/                    # JUnit 5 & Mockito Test Suite
│
├── docs/                        # Architecture & Security Documentation
├── logs/                        # Transaction & System Audit Logs
├── pom.xml                      # Maven Dependencies
└── README.md
```

---

# Installation

## Prerequisites

* Java **JDK 17**
* **Maven 3.8+**
* **Git**

---

## Setup Instructions

### 1️ Clone the Repository

```
git clone https://github.com/RevPay-Team/RevPay.git
cd RevPay
```

### 2️ Configure Environment

The application runs on **port 8081** by default.

Ensure this port is available on your local machine.

### 3️ Build the Project

```
mvn clean install
```

---

#  Usage

## Run the Application

Start the embedded Spring Boot server:

```
mvn spring-boot:run
```

Once the application starts, open the dashboard:

```
http://localhost:8081/
```

---

#  Environment Variables

| Variable               | Description                  | Default |
| ---------------------- | ---------------------------- | ------- |
| SERVER_PORT            | Web server port              | 8081    |
| SPRING_PROFILES_ACTIVE | Environment profile          | dev     |
| LOGGING_LEVEL_REVPAY   | Logging level for audit logs | INFO    |

---

#  Example API Endpoints

## Send Transaction

### Endpoint

```
POST /api/transactions/send
```

### Request Body

```
{
  "recipientId": 102,
  "amount": 250.00,
  "note": "Payment for Invoice #445"
}
```

### Response

```
{
  "transactionId": "TXN-99012",
  "status": "SUCCESS",
  "timestamp": "2026-03-10T14:30:00"
}
```

---

#  Testing & QA

RevPay maintains high-quality code through **automated testing using JUnit 5, Mockito, and JaCoCo.**

### Run All Tests

```
mvn test
```

### Generate Coverage Report

```
mvn jacoco:report
```

##  Test Artifacts

* **Unit Test Coverage:** 90%+ on `service.impl`
* **Surefire Reports:** `target/surefire-reports/`
* **JaCoCo Coverage Dashboard:**

Open the following file in your browser:

```
target/site/jacoco/index.html
```
