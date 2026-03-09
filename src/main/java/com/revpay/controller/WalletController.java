package com.revpay.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revpay.dto.ApiResponse;
import com.revpay.service.WalletService;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * GET WALLET BALANCE
     */
    @GetMapping("/balance/{userId}")
    public ResponseEntity<ApiResponse<BigDecimal>> getBalance(@PathVariable Long userId) {

        BigDecimal balance = walletService.getBalance(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Wallet balance fetched successfully",
                        balance
                )
        );
    }

    /**
     * ADD MONEY (DEPOSIT)
     */
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<Void>> addMoney(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam String transactionPin) {

        walletService.addMoney(userId, amount, transactionPin);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Money added to wallet successfully",
                        null
                )
        );
    }

    /**
     * WITHDRAW MONEY
     */
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdrawMoney(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam String transactionPin) {

        walletService.withdrawMoney(userId, amount, transactionPin);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Money withdrawn from wallet successfully",
                        null
                )
        );
    }
}