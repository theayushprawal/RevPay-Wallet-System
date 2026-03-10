package com.revpay.repository;

import com.revpay.model.Loan;
import com.revpay.model.RepaymentSchedule;
import com.revpay.model.User;
import com.revpay.model.enums.LoanStatus;
import com.revpay.model.enums.UserStatus;
import com.revpay.model.enums.UserType;
import com.revpay.model.enums.YesNoStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RepaymentScheduleRepositoryTest {

    @Autowired
    private RepaymentScheduleRepository repaymentScheduleRepository;

    @Autowired
    private EntityManager entityManager;

    private User createUser(){
        User user=new User();
        user.setFullName("Loan User");
        user.setEmail("loanuser@test.com");
        user.setPhone("9111111111");
        user.setPasswordHash("pass");
        user.setTransactionPinHash("1234");
        user.setUserType(UserType.BUSINESS);
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedAttempts(0);
        user.setIsLocked(YesNoStatus.NO);
        user.setCreatedAt(LocalDateTime.now());
        entityManager.persist(user);
        return user;
    }

    private Loan createLoan(User user){
        Loan loan=new Loan();
        loan.setBusiness(user);
        loan.setAmount(new BigDecimal("50000"));
        loan.setEmi(new BigDecimal("5000"));
        loan.setInterestRate(new BigDecimal("10"));
        loan.setTenureMonths(12);
        loan.setPurpose("Business Expansion");
        loan.setStatus(LoanStatus.APPROVED);
        loan.setCreatedAt(LocalDateTime.now());
        entityManager.persist(loan);
        return loan;
    }

    @Test
    public void testFindByLoan(){
        User user=createUser();
        Loan loan=createLoan(user);

        RepaymentSchedule r1=new RepaymentSchedule();
        r1.setLoan(loan);
        r1.setBalancePayment(new BigDecimal("45000"));
        r1.setEmi(new BigDecimal("5000"));
        r1.setPaymentDate(LocalDateTime.now());
        r1.setStatus("PENDING");
        r1.setRemarks("First EMI");
        entityManager.persist(r1);

        RepaymentSchedule r2=new RepaymentSchedule();
        r2.setLoan(loan);
        r2.setBalancePayment(new BigDecimal("40000"));
        r2.setEmi(new BigDecimal("5000"));
        r2.setPaymentDate(LocalDateTime.now());
        r2.setStatus("PENDING");
        r2.setRemarks("Second EMI");
        entityManager.persist(r2);

        List<RepaymentSchedule> list=repaymentScheduleRepository.findByLoan(loan);

        Assert.assertEquals(2,list.size());
    }
}