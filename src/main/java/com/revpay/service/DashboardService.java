package com.revpay.service;

import com.revpay.dto.DashboardSummaryResponse;

public interface DashboardService {

    DashboardSummaryResponse getDashboardSummary(Long userId);
}