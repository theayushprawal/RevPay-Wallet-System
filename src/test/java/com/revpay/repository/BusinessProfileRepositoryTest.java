package com.revpay.repository;

import com.revpay.model.BusinessProfile;
import com.revpay.model.User;
import com.revpay.model.enums.UserStatus;
import com.revpay.model.enums.UserType;
import com.revpay.model.enums.YesNoStatus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BusinessProfileRepositoryTest {

    @Autowired
    private BusinessProfileRepository businessProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByUser() {

        // ===== Create User =====
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPhone("9999999999");
        user.setPasswordHash("password123");

        user.setUserType(UserType.PERSONAL);
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedAttempts(0);
        user.setIsLocked(YesNoStatus.NO);
        user.setCreatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        // ===== Create BusinessProfile =====
        BusinessProfile profile = new BusinessProfile();
        profile.setUser(user);
        profile.setBusinessName("Test Business");

        businessProfileRepository.save(profile);

        // ===== Test findByUser() =====
        Optional<BusinessProfile> foundProfile =
                businessProfileRepository.findByUser(user);

        // ===== Assertions =====
        assertTrue(foundProfile.isPresent());
        assertEquals("Test Business", foundProfile.get().getBusinessName());
    }
}