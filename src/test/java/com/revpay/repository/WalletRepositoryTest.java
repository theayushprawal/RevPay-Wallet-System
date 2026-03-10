package com.revpay.repository;

import com.revpay.model.User;
import com.revpay.model.Wallet;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Helper to create a User. 
     * Wallets require a saved User to satisfy the @JoinColumn(name = "USER_ID") constraint.
     */
    private User createTestUser() {
        User user = new User();
        user.setFullName("Wallet Test User");
        user.setEmail(UUID.randomUUID() + "@revpay.com");
        user.setPhone(String.valueOf(System.nanoTime()).substring(0, 10));
        user.setPasswordHash("hashed_password");
        user.setUserType(UserType.PERSONAL);
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedAttempts(0);
        user.setIsLocked(YesNoStatus.NO);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Helper to create a Wallet.
     */
    private Wallet createTestWallet(User user, BigDecimal balance) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(balance);
        wallet.setLastUpdated(LocalDateTime.now());
        return walletRepository.save(wallet);
    }

    @Test
    public void testFindByUser() {
        // Arrange
        User user = createTestUser();
        BigDecimal initialBalance = new BigDecimal("1000.50");
        createTestWallet(user, initialBalance);

        // Act
        Optional<Wallet> foundWallet = walletRepository.findByUser(user);

        // Assert
        assertTrue("Wallet should be found for the user", foundWallet.isPresent());
        assertEquals("User ID should match", user.getUserId(), foundWallet.get().getUser().getUserId());
        // compareTo returns 0 if values are numerically equal regardless of scale (e.g., 100.0 vs 100.00)
        assertEquals(0, initialBalance.compareTo(foundWallet.get().getBalance()));
    }

    @Test
    public void testExistsByUser() {
        // Arrange
        User userWithWallet = createTestUser();
        User userWithoutWallet = createTestUser();
        createTestWallet(userWithWallet, BigDecimal.ZERO);

        // Act
        boolean exists = walletRepository.existsByUser(userWithWallet);
        boolean doesNotExist = walletRepository.existsByUser(userWithoutWallet);

        // Assert
        assertTrue("Should return true for user with wallet", exists);
        assertFalse("Should return false for user without wallet", doesNotExist);
    }
}