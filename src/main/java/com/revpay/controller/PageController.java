package com.revpay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "index"; // Load templates/index.html
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // templates/register.html
    }

    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard"; // templates/dashboard.html
    }

    @GetMapping("/transactions")
    public String transactionsPage() {
        return "transactions"; // templates/transactions.html
    }

    @GetMapping("/invoices")
    public String invoicesPage() {
        return "invoices"; // templates/invoices.html
    }

    @GetMapping("/loans")
    public String loansPage() {
        return "loans"; // templates/loans.html
    }

    @GetMapping("/notifications")
    public String notificationsPage() {
        return "notifications"; // templates/notifications.html
    }

    @GetMapping("/profile")
    public String profilePage() {
        return "profile"; // templates/profile.html
    }

    @GetMapping("/requests")
    public String requestsPage() {
        return "requests";
    }

    @GetMapping("/payment-methods")
    public String paymentMethodsPage() {
        return "payment-methods";
    }

    @GetMapping("/analytics")
    public String analyticsPage() {
        return "analytics"; // Tells Spring to look for analytics.html
    }
}
