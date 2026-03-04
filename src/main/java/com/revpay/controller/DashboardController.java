package com.revpay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(
            @RequestParam Long userId) {

        return ResponseEntity.ok(
                dashboardService.getDashboardSummary(userId)
        );
    }
}