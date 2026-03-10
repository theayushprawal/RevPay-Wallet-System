package com.revpay.repository;

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
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // Helper method to create a user for testing
    private User createUser(String email, String phone) {
        User user = new User();
        user.setFullName("Test User");
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash("password_hash");
        user.setUserType(UserType.PERSONAL);
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedAttempts(0);
        user.setIsLocked(YesNoStatus.NO);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Test
    public void testFindByEmail() {
        String email = "test@revpay.com";
        createUser(email, "1234567890");

        Optional<User> found = userRepository.findByEmail(email);

        assertTrue(found.isPresent());
        assertEquals(email, found.get().getEmail());
    }

    @Test
    public void testFindByPhone() {
        String phone = "9876543210";
        createUser("test@revpay.com", phone);

        Optional<User> found = userRepository.findByPhone(phone);

        assertTrue(found.isPresent());
        assertEquals(phone, found.get().getPhone());
    }

    @Test
    public void testFindByEmailOrPhone() {
        String email = "email@revpay.com";
        String phone = "1112223333";
        createUser(email, phone);

        Optional<User> byEmail = userRepository.findByEmailOrPhone(email, "unknown");
        Optional<User> byPhone = userRepository.findByEmailOrPhone("unknown", phone);

        assertTrue(byEmail.isPresent());
        assertTrue(byPhone.isPresent());
        assertEquals(email, byEmail.get().getEmail());
    }

    @Test
    public void testExistsByEmail() {
        String email = "exists@revpay.com";
        createUser(email, "5555555555");

        assertTrue(userRepository.existsByEmail(email));
        assertFalse(userRepository.existsByEmail("nonexistent@revpay.com"));
    }

    @Test
    public void testExistsByPhone() {
        String phone = "0000000000";
        createUser("other@revpay.com", phone);

        assertTrue(userRepository.existsByPhone(phone));
        assertFalse(userRepository.existsByPhone("9999999999"));
    }
}