package com.revpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.model.BusinessProfile;

@Repository
public interface BusinessProfileRepository
        extends JpaRepository<BusinessProfile, Long> {
}