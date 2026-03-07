package com.revpay.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class RevenueReportResponse {

    private BigDecimal dailyRevenue;
    private BigDecimal weeklyRevenue;
    private BigDecimal monthlyRevenue;

}