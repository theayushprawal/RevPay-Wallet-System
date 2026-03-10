package com.revpay.repository;

import com.revpay.model.SecurityQuestion;
import com.revpay.model.User;
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
import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SecurityQuestionRepositoryTest {

    @Autowired
    private SecurityQuestionRepository securityQuestionRepository;

    @Autowired
    private EntityManager entityManager;

    private User createUser(){
        User user=new User();
        user.setFullName("Security User");
        user.setEmail("security@test.com");
        user.setPhone("9222222222");
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

    @Test
    public void testFindByUser(){
        User user=createUser();

        SecurityQuestion sq=new SecurityQuestion();
        sq.setUser(user);
        sq.setQuestion("Your first school?");
        sq.setAnswerHash("hashedAnswer");
        entityManager.persist(sq);

        Optional<SecurityQuestion> result=
                securityQuestionRepository.findByUser(user);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals("Your first school?",result.get().getQuestion());
    }
}