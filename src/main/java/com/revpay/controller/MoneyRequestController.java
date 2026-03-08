package com.revpay.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> createRequest(
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

        return ResponseEntity.ok("Money request created successfully");
    }

    /**
     * ACCEPT MONEY REQUEST
     * Receiver accepts the request
     */
    @PostMapping("/accept")
    public ResponseEntity<String> acceptRequest(
            @RequestParam Long requestId,
            @RequestParam String transactionPin) {

        moneyRequestService.acceptRequest(requestId, transactionPin);

        return ResponseEntity.ok("Money request accepted");
    }

    /**
     * DECLINE MONEY REQUEST
     * Receiver declines the request
     */
    @PostMapping("/decline")
    public ResponseEntity<String> declineRequest(
            @RequestParam Long requestId) {

        moneyRequestService.declineRequest(requestId);

        return ResponseEntity.ok("Money request declined");
    }

    /**
     * CANCEL MONEY REQUEST
     * Sender cancels the request
     */
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelRequest(
            @RequestParam Long requestId) {

        moneyRequestService.cancelRequest(requestId);

        return ResponseEntity.ok("Money request cancelled");
    }
}