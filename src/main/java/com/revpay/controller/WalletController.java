package com.revpay.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long userId) {

        BigDecimal balance = walletService.getBalance(userId);
        return ResponseEntity.ok(balance);
    }

    /**
     * ADD MONEY (DEPOSIT)
     */
    @PostMapping("/deposit")
    public ResponseEntity<String> addMoney(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam String transactionPin) {

        walletService.addMoney(userId, amount, transactionPin);

        return ResponseEntity.ok("Money added to wallet successfully");
    }

    /**
     * WITHDRAW MONEY
     */
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdrawMoney(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam String transactionPin) {

        walletService.withdrawMoney(userId, amount, transactionPin);

        return ResponseEntity.ok("Money withdrawn from wallet successfully");
    }
}