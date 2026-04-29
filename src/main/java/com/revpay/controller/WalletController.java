package com.revpay.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.revpay.dto.ApiResponse;
import com.revpay.service.WalletService;

@RestController
@RequestMapping("/wallet")
@PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS')") // Blanket: Must be authenticated
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * GET WALLET BALANCE
     */
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS') and @securityGuard.isUserMatching(authentication, #userId)")
    @GetMapping("/balance/{userId}")
    public ResponseEntity<ApiResponse<BigDecimal>> getBalance(@PathVariable Long userId) {

        BigDecimal balance = walletService.getBalance(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Wallet balance fetched successfully", balance)
        );
    }

    /**
     * ADD MONEY (DEPOSIT)
     */
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS') and @securityGuard.isUserMatching(authentication, #userId)")
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<Void>> addMoney(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam String transactionPin) {

        walletService.addMoney(userId, amount, transactionPin);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Money added to wallet successfully", null)
        );
    }

    /**
     * WITHDRAW MONEY
     */
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS') and @securityGuard.isUserMatching(authentication, #userId)")
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdrawMoney(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam String transactionPin) {

        walletService.withdrawMoney(userId, amount, transactionPin);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Money withdrawn from wallet successfully", null)
        );
    }
}