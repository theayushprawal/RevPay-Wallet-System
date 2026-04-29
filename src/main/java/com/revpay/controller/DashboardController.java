package com.revpay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.DashboardSummaryResponse;
import com.revpay.service.DashboardService;
import com.revpay.model.User;
import com.revpay.repository.UserRepository;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository; // Added to verify identity

    public DashboardController(DashboardService dashboardService, UserRepository userRepository) {
        this.dashboardService = dashboardService;
        this.userRepository = userRepository;
    }

    /**
     * GET DASHBOARD SUMMARY
     * Must be logged in as PERSONAL or BUSINESS, and can only request their own ID.
     */
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS')")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboardSummary(
            @RequestParam Long userId) {

        verifyUserIdentity(userId); // <-- IDOR PROTECTION: Blocks users from spying on other wallets

        DashboardSummaryResponse summary = dashboardService.getDashboardSummary(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Dashboard summary fetched successfully",
                        summary
                )
        );
    }

    // ==========================================
    // SECURITY UTILITY METHOD
    // ==========================================
    private void verifyUserIdentity(Long requestedUserId) {
        // 1. Get the loginId (email/phone) from the JWT context
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Fetch the actual user from the DB
        User loggedInUser = userRepository.findByEmailOrPhone(loggedInUsername, loggedInUsername)
                .orElseThrow(() -> new SecurityException("Unauthorized: Invalid token identity"));

        // 3. Compare the IDs
        if (!loggedInUser.getUserId().equals(requestedUserId)) {
            throw new SecurityException("Unauthorized: You do not have permission to view this dashboard.");
        }
    }
}