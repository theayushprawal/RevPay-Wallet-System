package com.revpay.controller;

import java.math.BigDecimal;
import java.util.List;

import com.revpay.model.MoneyRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revpay.dto.ApiResponse;
import com.revpay.service.MoneyRequestService;

@RestController
@RequestMapping("/money-requests")
public class MoneyRequestController {

    private final MoneyRequestService moneyRequestService;

    public MoneyRequestController(MoneyRequestService moneyRequestService) {
        this.moneyRequestService = moneyRequestService;
    }

    /**
     * CREATE MONEY REQUEST
     * Sender creates a request for receiver
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createRequest(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String note) {

        moneyRequestService.createRequest(
                senderId,
                receiverId,
                amount,
                note
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Money request created successfully",
                        null
                )
        );
    }

    /**
     * ACCEPT MONEY REQUEST
     * Receiver accepts the request
     */
    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<Void>> acceptRequest(
            @RequestParam Long requestId,
            @RequestParam String transactionPin) {

        moneyRequestService.acceptRequest(requestId, transactionPin);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Money request accepted",
                        null
                )
        );
    }

    /**
     * DECLINE MONEY REQUEST
     * Receiver declines the request
     */
    @PostMapping("/decline")
    public ResponseEntity<ApiResponse<Void>> declineRequest(
            @RequestParam Long requestId) {

        moneyRequestService.declineRequest(requestId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Money request declined",
                        null
                )
        );
    }

    /**
     * CANCEL MONEY REQUEST
     * Sender cancels the request
     */
    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelRequest(
            @RequestParam Long requestId) {

        moneyRequestService.cancelRequest(requestId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Money request cancelled",
                        null
                )
        );
    }

    /**
     * GET INCOMING REQUESTS
     */
    @GetMapping("/incoming/{userId}")
    public ResponseEntity<ApiResponse<List<MoneyRequest>>> getIncomingRequests(@PathVariable Long userId) {
        List<MoneyRequest> requests = moneyRequestService.getIncomingRequests(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Incoming fetched", requests));
    }

    /**
     * GET OUTGOING REQUESTS
     */
    @GetMapping("/outgoing/{userId}")
    public ResponseEntity<ApiResponse<List<MoneyRequest>>> getOutgoingRequests(@PathVariable Long userId) {
        List<MoneyRequest> requests = moneyRequestService.getOutgoingRequests(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Outgoing fetched", requests));
    }
}