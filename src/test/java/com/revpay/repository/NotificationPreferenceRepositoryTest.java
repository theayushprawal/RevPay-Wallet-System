package com.revpay.repository;

import com.revpay.model.NotificationPreference;
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
import java.util.Optional;
import java.util.UUID;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class NotificationPreferenceRepositoryTest {

    @Autowired
    private NotificationPreferenceRepository notificationPreferenceRepository;

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

    private NotificationPreference createPreference(User user,NotificationType type){
        NotificationPreference pref=new NotificationPreference();
        pref.setUser(user);
        pref.setType(type);
        pref.setEnabled(YesNoStatus.YES);
        return notificationPreferenceRepository.save(pref);
    }

    @Test
    public void testFindByUserAndType(){
        User user=createUser();
        createPreference(user,NotificationType.TRANSACTION);

        Optional<NotificationPreference> result=
                notificationPreferenceRepository.findByUserAndType(user,NotificationType.TRANSACTION);

        assertTrue(result.isPresent());
        assertEquals(NotificationType.TRANSACTION,result.get().getType());
    }

    @Test
    public void testFindByUser(){
        User user=createUser();
        createPreference(user,NotificationType.TRANSACTION);
        createPreference(user,NotificationType.MONEY_REQUEST);

        List<NotificationPreference> result=
                notificationPreferenceRepository.findByUser(user);

        assertEquals(2,result.size());
    }
}