package com.revpay.repository;

import com.revpay.model.Notification;
import com.revpay.model.User;
import com.revpay.model.enums.NotificationType;
import com.revpay.model.enums.YesNoStatus;
import com.revpay.model.enums.UserStatus;
import com.revpay.model.enums.UserType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    private User createUser(){
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

    private Notification createNotification(User user,NotificationType type,YesNoStatus isRead){
        Notification n=new Notification();
        n.setUser(user);
        n.setMessage("Test Notification");
        n.setType(type);
        n.setIsRead(isRead);
        n.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(n);
    }

    @Test
    public void testFindByUserOrderByCreatedAtDesc(){
        User user=createUser();
        createNotification(user,NotificationType.TRANSACTION,YesNoStatus.NO);
        createNotification(user,NotificationType.LOAN,YesNoStatus.NO);

        List<Notification> result=notificationRepository.findByUserOrderByCreatedAtDesc(user);

        assertEquals(2,result.size());
    }

    @Test
    public void testFindByUserAndIsReadOrderByCreatedAtDesc(){
        User user=createUser();
        createNotification(user,NotificationType.TRANSACTION,YesNoStatus.NO);
        createNotification(user,NotificationType.LOAN,YesNoStatus.YES);

        List<Notification> result=
                notificationRepository.findByUserAndIsReadOrderByCreatedAtDesc(user,YesNoStatus.NO);

        assertEquals(1,result.size());
    }

    @Test
    public void testFindByUserAndType(){
        User user=createUser();
        createNotification(user,NotificationType.INVOICE,YesNoStatus.NO);

        List<Notification> result=
                notificationRepository.findByUserAndType(user,NotificationType.INVOICE);

        assertEquals(1,result.size());
    }

    @Test
    public void testCountByUserAndIsRead(){
        User user=createUser();
        createNotification(user,NotificationType.TRANSACTION,YesNoStatus.NO);
        createNotification(user,NotificationType.LOAN,YesNoStatus.NO);

        Long count=notificationRepository.countByUserAndIsRead(user,YesNoStatus.NO);

        assertEquals(Long.valueOf(2),count);
    }
}