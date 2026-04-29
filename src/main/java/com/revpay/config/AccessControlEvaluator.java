package com.revpay.config;

import com.revpay.model.*;
import com.revpay.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("securityGuard")
public class AccessControlEvaluator {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final NotificationRepository notificationRepository;
    private final MoneyRequestRepository moneyRequestRepository;

    public AccessControlEvaluator(UserRepository userRepository, LoanRepository loanRepository, InvoiceRepository invoiceRepository, PaymentMethodRepository paymentMethodRepository, NotificationRepository notificationRepository, MoneyRequestRepository moneyRequestRepository) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.notificationRepository = notificationRepository;
        this.moneyRequestRepository = moneyRequestRepository;
    }

    /**
     * Checks if the currently authenticated user matches the requested userId.
     */
    public boolean isUserMatching(Authentication authentication, Long requestedUserId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // The principal is the email or phone we extracted from the JWT in JwtFilter
        String currentLoginId = authentication.getName();

        // Using findByEmailOrPhone to handle both login types safely
        User user = userRepository.findByEmailOrPhone(currentLoginId, currentLoginId).orElse(null);

        if (user == null) {
            return false;
        }

        return user.getUserId().equals(requestedUserId);
    }

    /**
     * Checks if the currently authenticated business owns the requested loan.
     */
    public boolean isLoanOwner(Authentication authentication, Long loanId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String currentLoginId = authentication.getName();
        User user = userRepository.findByEmailOrPhone(currentLoginId, currentLoginId).orElse(null);

        if (user == null) {
            return false;
        }

        Loan loan = loanRepository.findById(loanId).orElse(null);

        // Return true only if the loan exists AND the business attached to the loan matches the logged-in user
        return loan != null && loan.getBusiness().getUserId().equals(user.getUserId());
    }

    /**
     * Checks if the currently authenticated business owns the requested invoice.
     */
    public boolean isInvoiceOwner(Authentication authentication, Long invoiceId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String currentLoginId = authentication.getName();
        User user = userRepository.findByEmailOrPhone(currentLoginId, currentLoginId).orElse(null);

        if (user == null) {
            return false;
        }

        Invoice invoice = invoiceRepository.findById(invoiceId).orElse(null);

        // Return true only if the invoice exists AND the business attached to it matches the logged-in user
        return invoice != null && invoice.getBusiness().getUserId().equals(user.getUserId());
    }

    /**
     * Checks if the currently authenticated user owns the specific payment method.
     */
    public boolean isPaymentMethodOwner(Authentication authentication, Long paymentMethodId) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        String currentLoginId = authentication.getName();
        User user = userRepository.findByEmailOrPhone(currentLoginId, currentLoginId).orElse(null);
        if (user == null) return false;

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId).orElse(null);

        // Ensure the payment method exists and belongs to the logged-in user
        return paymentMethod != null && paymentMethod.getUser().getUserId().equals(user.getUserId());
    }

    /**
     * Checks if the currently authenticated user owns the specific notification.
     */
    public boolean isNotificationOwner(Authentication authentication, Long notificationId) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        String currentLoginId = authentication.getName();
        User user = userRepository.findByEmailOrPhone(currentLoginId, currentLoginId).orElse(null);
        if (user == null) return false;

        Notification notification = notificationRepository.findById(notificationId).orElse(null);

        // Ensure the notification exists and belongs to the logged-in user
        return notification != null && notification.getUser().getUserId().equals(user.getUserId());
    }

    /**
     * Checks if the currently authenticated user is the RECEIVER of the money request.
     */
    public boolean isMoneyRequestReceiver(Authentication authentication, Long requestId) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        String currentLoginId = authentication.getName();
        User user = userRepository.findByEmailOrPhone(currentLoginId, currentLoginId).orElse(null);
        if (user == null) return false;

        MoneyRequest request = moneyRequestRepository.findById(requestId).orElse(null);

        // Ensure the request exists and the logged-in user is the one who was asked for money
        return request != null && request.getReceiver().getUserId().equals(user.getUserId());
    }

    /**
     * Checks if the currently authenticated user is the SENDER of the money request.
     */
    public boolean isMoneyRequestSender(Authentication authentication, Long requestId) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        String currentLoginId = authentication.getName();
        User user = userRepository.findByEmailOrPhone(currentLoginId, currentLoginId).orElse(null);
        if (user == null) return false;

        MoneyRequest request = moneyRequestRepository.findById(requestId).orElse(null);

        // Ensure the request exists and the logged-in user is the one who created it
        return request != null && request.getSender().getUserId().equals(user.getUserId());
    }
}