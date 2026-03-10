package com.revpay.repository;

import com.revpay.model.MoneyRequest;
import com.revpay.model.User;
import com.revpay.model.enums.RequestStatus;
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
public class MoneyRequestRepositoryTest {

    @Autowired
    private MoneyRequestRepository moneyRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        User user=new User();
        user.setFullName("Test User");
        user.setEmail(UUID.randomUUID()+"@test.com");
        user.setPhone(String.valueOf(System.nanoTime()).substring(0,10));
        user.setPasswordHash("password");
        user.setUserType(UserType.PERSONAL);
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedAttempts(0);
        user.setIsLocked(YesNoStatus.NO);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private MoneyRequest createMoneyRequest(User sender,User receiver,RequestStatus status,BigDecimal amount) {
        MoneyRequest req=new MoneyRequest();
        req.setSender(sender);
        req.setReceiver(receiver);
        req.setAmount(amount);
        req.setStatus(status);
        req.setCreatedAt(LocalDateTime.now());
        return moneyRequestRepository.save(req);
    }

    @Test
    public void testFindByReceiver() {
        User sender=createUser();
        User receiver=createUser();
        createMoneyRequest(sender,receiver,RequestStatus.PENDING,BigDecimal.valueOf(100));
        List<MoneyRequest> list=moneyRequestRepository.findByReceiver(receiver);
        assertEquals(1,list.size());
    }

    @Test
    public void testFindBySender() {
        User sender=createUser();
        User receiver=createUser();
        createMoneyRequest(sender,receiver,RequestStatus.PENDING,BigDecimal.valueOf(200));
        List<MoneyRequest> list=moneyRequestRepository.findBySender(sender);
        assertEquals(1,list.size());
    }

    @Test
    public void testFindByReceiverAndStatus() {
        User sender=createUser();
        User receiver=createUser();
        createMoneyRequest(sender,receiver,RequestStatus.PENDING,BigDecimal.valueOf(300));
        List<MoneyRequest> list=moneyRequestRepository.findByReceiverAndStatus(receiver,RequestStatus.PENDING);
        assertEquals(1,list.size());
    }

    @Test
    public void testFindBySenderAndStatus() {
        User sender=createUser();
        User receiver=createUser();
        createMoneyRequest(sender,receiver,RequestStatus.ACCEPTED,BigDecimal.valueOf(400));
        List<MoneyRequest> list=moneyRequestRepository.findBySenderAndStatus(sender,RequestStatus.ACCEPTED);
        assertEquals(1,list.size());
    }

    @Test
    public void testGetPendingRequestAmount() {
        User sender=createUser();
        User receiver=createUser();
        createMoneyRequest(sender,receiver,RequestStatus.PENDING,BigDecimal.valueOf(500));
        BigDecimal total=moneyRequestRepository.getPendingRequestAmount(receiver.getUserId());
        assertTrue(total.compareTo(BigDecimal.valueOf(500))==0);
    }
}