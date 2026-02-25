package com.revpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.model.SecurityQuestion;

@Repository
public interface SecurityQuestionRepository
        extends JpaRepository<SecurityQuestion, Long> {
}