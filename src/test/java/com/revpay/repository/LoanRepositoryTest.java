package com.revpay.repository;

import com.revpay.model.Loan;
import com.revpay.model.User;
import com.revpay.model.enums.LoanStatus;
import com.revpay.model.enums.UserStatus;
import com.revpay.model.enums.UserType;
import com.revpay.model.enums.YesNoStatus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    // ===============================
    // Helper Methods
    // ===============================

    private User createBusinessUser() {

        User user = new User();
        user.setFullName("Business Owner");
        user.setEmail(UUID.randomUUID() + "@test.com");
        user.setPhone("9999999999");
        user.setPasswordHash("password");

        user.setUserType(UserType.BUSINESS);
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedAttempts(0);
        user.setIsLocked(YesNoStatus.NO);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    private Loan createLoan(User business, LoanStatus status, BigDecimal amount) {

        Loan loan = new Loan();

        loan.setBusiness(business);
        loan.setAmount(amount);
        loan.setEmi(BigDecimal.valueOf(500));
        loan.setInterestRate(BigDecimal.valueOf(10));
        loan.setTenureMonths(12);
        loan.setPurpose("Business Expansion");

        loan.setStatus(status);
        loan.setCreatedAt(LocalDateTime.now());

        loan.setDocumentName("loan-doc.pdf");
        loan.setDocumentUploaded(YesNoStatus.YES);

        return loanRepository.save(loan);
    }

    // ===============================
    // Tests
    // ===============================

    @Test
    public void testFindByBusiness() {

        User business = createBusinessUser();

        createLoan(business, LoanStatus.PENDING, BigDecimal.valueOf(10000));

        List<Loan> loans = loanRepository.findByBusiness(business);

        assertEquals(1, loans.size());
    }

    @Test
    public void testFindByBusinessAndStatus() {

        User business = createBusinessUser();

        createLoan(business, LoanStatus.APPROVED, BigDecimal.valueOf(20000));

        List<Loan> loans =
                loanRepository.findByBusinessAndStatus(business, LoanStatus.APPROVED);

        assertEquals(1, loans.size());
    }
}