package com.revpay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.DashboardSummaryResponse;
import com.revpay.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboardSummary(
            @RequestParam Long userId) {

        DashboardSummaryResponse summary =
                dashboardService.getDashboardSummary(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Dashboard summary fetched successfully",
                        summary
                )
        );
    }
}