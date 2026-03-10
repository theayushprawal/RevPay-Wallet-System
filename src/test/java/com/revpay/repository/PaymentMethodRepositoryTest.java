package com.revpay.repository;

import com.revpay.model.PaymentMethod;
import com.revpay.model.User;
import com.revpay.model.enums.PaymentMethodType;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PaymentMethodRepositoryTest {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private EntityManager entityManager;

    private User createUser(String email,String phone){
        User user=new User();
        user.setFullName("Test User");
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash("pass");
        user.setTransactionPinHash("1234");
        user.setUserType(UserType.PERSONAL);
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedAttempts(0);
        user.setIsLocked(YesNoStatus.NO);
        user.setCreatedAt(LocalDateTime.now());
        entityManager.persist(user);
        return user;
    }

    private PaymentMethod createPaymentMethod(User user,YesNoStatus isDefault){
        PaymentMethod pm=new PaymentMethod();
        pm.setUser(user);
        pm.setMethodType(PaymentMethodType.CARD);
        pm.setDetailsEnc("encrypted");
        pm.setPaymentName("Visa Card");
        pm.setCvv("123");
        pm.setBillingAddress("Bangalore");
        pm.setLast4("1234");
        pm.setExpiryDate(LocalDate.of(2030,12,31));
        pm.setIsDefault(isDefault);
        pm.setIfscCode("HDFC0001234");
        entityManager.persist(pm);
        return pm;
    }

    @Test
    public void testFindByUser(){
        User user=createUser("user1@test.com","9000000001");
        createPaymentMethod(user,YesNoStatus.NO);
        createPaymentMethod(user,YesNoStatus.YES);

        List<PaymentMethod> list=paymentMethodRepository.findByUser(user);

        Assert.assertEquals(2,list.size());
    }

    @Test
    public void testFindByUserAndIsDefault(){
        User user=createUser("user2@test.com","9000000002");
        createPaymentMethod(user,YesNoStatus.NO);
        createPaymentMethod(user,YesNoStatus.YES);

        Optional<PaymentMethod> result=
                paymentMethodRepository.findByUserAndIsDefault(user,YesNoStatus.YES);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(YesNoStatus.YES,result.get().getIsDefault());
    }

    @Test
    public void testExistsByUserAndIsDefault(){
        User user=createUser("user3@test.com","9000000003");
        createPaymentMethod(user,YesNoStatus.YES);

        boolean exists=
                paymentMethodRepository.existsByUserAndIsDefault(user,YesNoStatus.YES);

        Assert.assertTrue(exists);
    }
}