package com.revpay.service;

import com.revpay.dto.response.DashboardSummaryResponse;

public interface DashboardService {

    DashboardSummaryResponse getDashboardSummary(Long userId);
}