package com.revpay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.model.User;
import com.revpay.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // Get wallet for a given user
    Optional<Wallet> findByUser(User user);

    // Check if wallet already exists for a user
    boolean existsByUser(User user);
}