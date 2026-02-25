package com.revpay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Login / identity checks
    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    // Used for login via email OR phone
    Optional<User> findByEmailOrPhone(String email, String phone);

    // Uniqueness checks during registration
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}