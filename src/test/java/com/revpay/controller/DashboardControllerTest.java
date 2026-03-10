package com.revpay.controller;

import com.revpay.dto.DashboardSummaryResponse;
import com.revpay.service.DashboardService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    private DashboardSummaryResponse summary;

    @BeforeEach
    void setUp() {

        summary = new DashboardSummaryResponse();
        summary.setWalletBalance(BigDecimal.valueOf(5000));
        summary.setRecentTransactions(new ArrayList<>());
        summary.setPendingRequests(new ArrayList<>());
        summary.setUnreadNotifications(3L);
    }

    // ================= SUCCESS CASE =================

    @Test
    void getDashboardSummarySuccess() throws Exception {

        Mockito.when(dashboardService.getDashboardSummary(1L))
                .thenReturn(summary);

        mockMvc.perform(get("/dashboard/summary")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.walletBalance").value(5000))
                .andExpect(jsonPath("$.data.unreadNotifications").value(3));
    }

    // ================= MISSING USER ID =================

    @Test
    void getDashboardSummaryMissingUserId() throws Exception {

        mockMvc.perform(get("/dashboard/summary"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Server Error"));
    }

    // ================= INVALID USER ID =================

    @Test
    void getDashboardSummaryInvalidUserId() throws Exception {

        mockMvc.perform(get("/dashboard/summary")
                        .param("userId", "abc"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Server Error"));
    }

    // ================= NULL RESPONSE =================

    @Test
    void getDashboardSummaryNullResponse() throws Exception {

        Mockito.when(dashboardService.getDashboardSummary(anyLong()))
                .thenReturn(null);

        mockMvc.perform(get("/dashboard/summary")
                        .param("userId", "1"))
                .andExpect(status().isOk());
    }

    // ================= SERVICE EXCEPTION =================

    @Test
    void getDashboardSummaryServiceException() throws Exception {

        Mockito.when(dashboardService.getDashboardSummary(anyLong()))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/dashboard/summary")
                        .param("userId", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Server Error"));
    }
}