# RevPay - Production-Grade Digital Wallet & Business Payments Platform

## Project Description

RevPay is a high-integrity financial web application engineered to bridge the gap between personal asset management and business fiscal operations.

Built with a **Security-First** and **High-Concurrency** mindset, the platform handles secure digital payments, multi-tier wallets, and business-grade credit facilities. It features stateless JWT authentication, database logic to prevent double-spending, and a highly optimized persistence layer to ensure transparency, speed, and trust in every exchange.

---

# Table of Contents

* [Features](#features)
* [Architecture Overview](#architecture-overview)
* [Repository Structure](#repository-structure)
* [Installation](#installation)
* [Usage & Configuration](#usage--configuration)
* [Example API Endpoints](#example-api-endpoints)
* [Testing & QA](#testing--qa)

---

# Features

## Personal Account Suite

### Wallet Operations
* **Bank-Grade Atomicity:** ACID-compliant wallet debit/credit operations using Spring `@Transactional`.
* **Concurrency Protection:** Implemented Pessimistic Write Locking to completely eliminate race conditions and double-spending vulnerabilities.
* Simulated Add Funds from linked cards and Withdrawals to bank accounts.

### P2P Ecosystem
* Send or request money via Username, Email, or Account ID.
* 4-digit Transaction PIN verification for secure fund transfers.

### Transaction Management
* Complete transaction history with advanced filtering (Type, Date, Amount).
* Export transaction reports to **CSV/PDF**.

### Notification Engine
* Centralized notification system for low balance alerts, invoice payments, and transaction receipts.

---

## Business Account Suite

### Merchant Invoicing
* Generate itemized invoices with automatic tax calculations.
* Integrated "Pay Invoice" functionality linked directly to customer wallets.

### Credit Facilities
* Full Business Loan Lifecycle Management (Application, Document Upload, Approval Workflow, EMI Repayment).

### Business Analytics
* Dedicated business dashboard displaying revenue trends, outstanding invoice aging, and customer transaction metrics.
* Highly optimized repository-level aggregation queries.

---

# Architecture Overview

RevPay follows a **Layered Monolithic Architecture**, ensuring strict separation of concerns and high maintainability.

## Presentation Layer
* Responsive **Thymeleaf templates** and **Bootstrap 5 UI framework**.

## Business Logic Layer
* Domain-driven services implemented in `com.revpay.service.impl`.
* Ensures **ACID-compliant financial transaction handling**.

## Persistence Layer
* **Spring Data JPA** & **Hibernate ORM**.
* **Database Optimized:** Eradicated N+1 query issues using `FetchType.LAZY` and `@EntityGraph`.
* Supports **Oracle** (Production) and **H2 / PostgreSQL** (Development).

## Security & Exception Layer
* **Stateless JWT (JSON Web Tokens)** for secure, scalable API authentication.
* **Spring Security** with **BCrypt password hashing**.
* **Account Lockout Mechanism** (locks account after 3 failed attempts).
* **Global Exception Handler (`@ControllerAdvice`)** to standardize JSON error responses and prevent internal stack trace leaks.

---

# Repository Structure

```text
revpay-app/
├── src/
│   ├── main/
│   │   ├── java/com/revpay/
│   │   │   ├── config/          # Security, JWT Util & App Configurations
│   │   │   ├── controller/      # REST & Web Controllers
│   │   │   ├── dto/             # Data Transfer Objects & Validation
│   │   │   ├── exception/       # GlobalExceptionHandler & Custom Exceptions
│   │   │   ├── model/           # JPA Entities & Enums
│   │   │   ├── repository/      # Data Access Layer (w/ Pessimistic Locks)
│   │   │   └── service/         # Business Logic Interfaces & Implementations
│   │
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf UI Templates
│   │       └── application.properties
│
│   └── test/                    # JUnit 5 & Mockito Test Suite (JWT Mocking)
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
* **Oracle DB / PostgreSQL (or rely on embedded H2 for local testing)**

---

## Setup Instructions

### 1️ Clone the Repository

```
git clone repoURL
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

| Variable               | Description                   | Default            |
|------------------------|-------------------------------|--------------------|
| SERVER_PORT            | Web server port               | 8081               |
| SPRING_PROFILES_ACTIVE | Environment profile           | dev                |
| JWT_SECRET             | Secret key for signing JWTs   | (Must be provided) |
| JWT_EXPIRATION         | Token expiration time (ms)    | 86400000           |
| LOGGING_LEVEL_REVPAY   | Logging level for audit logs  | INFO               |

---

#  Example API Endpoints

## Send Transaction (JWT Protected)

### Endpoint

```
POST /api/transactions/send
```

### Headers

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR...
Content-Type: application/json
```

### Request Body

```
{
  "recipientIdentifier": "merchant@mail.com",
  "amount": 250.00,
  "transactionPin": "1234",
  "note": "Payment for Invoice #445"
}
```

### Response

```
{
  "transactionId": "TXN-99012",
  "status": "SUCCESS",
  "timestamp": "2026-04-10T14:30:00"
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

* **Unit Test Coverage:** 90%+ on `service.impl` (Includes mocked JWT and Transactional boundaries).
* **Surefire Reports:** `target/surefire-reports/`
* **JaCoCo Coverage Dashboard:**

Open the following file in your browser:

```
target/site/jacoco/index.html
```
