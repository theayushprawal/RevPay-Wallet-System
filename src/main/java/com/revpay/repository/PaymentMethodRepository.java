package com.revpay.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.model.PaymentMethod;
import com.revpay.model.User;
import com.revpay.model.enums.YesNoStatus;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    // Get all payment methods for a user
    List<PaymentMethod> findByUser(User user);

    // Get default payment method for a user
    Optional<PaymentMethod> findByUserAndIsDefault(User user, YesNoStatus isDefault);

    // Check if user already has a default payment method
    boolean existsByUserAndIsDefault(User user, YesNoStatus isDefault);
}