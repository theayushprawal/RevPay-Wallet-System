package com.revpay.repository;

import com.revpay.dto.TopCustomerResponse;
import com.revpay.model.Transaction;
import com.revpay.model.User;
import com.revpay.model.enums.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    // Helper to create Users
    private User createUser(String name, UserType type) {
        User user = new User();
        user.setFullName(name);
        user.setEmail(UUID.randomUUID() + "@test.com");
        user.setPhone(String.valueOf(System.nanoTime()).substring(0, 10));
        user.setPasswordHash("hash");
        user.setUserType(type);
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedAttempts(0);
        user.setIsLocked(YesNoStatus.NO);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Helper to create Transactions
    private Transaction createTransaction(User sender, User receiver, BigDecimal amount, TransactionType type, TransactionStatus status) {
        Transaction t = new Transaction();
        t.setSender(sender);
        t.setReceiver(receiver);
        t.setAmount(amount);
        t.setTxnType(type);
        t.setStatus(status);
        t.setTxnDate(LocalDateTime.now());
        t.setRemarks("Test Txn");
        return transactionRepository.save(t);
    }

    @Test
    public void testFindBySender() {
        User sender = createUser("Sender", UserType.PERSONAL);
        User receiver = createUser("Receiver", UserType.PERSONAL);
        createTransaction(sender, receiver, new BigDecimal("100"), TransactionType.SEND, TransactionStatus.SUCCESS);

        List<Transaction> result = transactionRepository.findBySender(sender);
        assertEquals(1, result.size());
    }

    @Test
    public void testFindByReceiver() {
        User sender = createUser("Sender", UserType.PERSONAL);
        User receiver = createUser("Receiver", UserType.PERSONAL);
        createTransaction(sender, receiver, new BigDecimal("50"), TransactionType.SEND, TransactionStatus.SUCCESS);

        List<Transaction> result = transactionRepository.findByReceiver(receiver);
        assertEquals(1, result.size());
    }

    @Test
    public void testFindBySenderAndTxnType() {
        User sender = createUser("Sender", UserType.PERSONAL);
        createTransaction(sender, null, new BigDecimal("10"), TransactionType.DEPOSIT, TransactionStatus.SUCCESS);

        List<Transaction> result = transactionRepository.findBySenderAndTxnType(sender, TransactionType.DEPOSIT);
        assertEquals(1, result.size());
    }

    @Test
    public void testFindTop5BySenderOrReceiverOrderByTxnDateDesc() {
        User user = createUser("MainUser", UserType.PERSONAL);
        User other = createUser("Other", UserType.PERSONAL);
        for (int i = 0; i < 6; i++) {
            createTransaction(user, other, new BigDecimal(i), TransactionType.SEND, TransactionStatus.SUCCESS);
        }

        List<Transaction> result = transactionRepository.findTop5BySenderOrReceiverOrderByTxnDateDesc(user, user);
        assertEquals(5, result.size());
    }

    @Test
    public void testFilterTransactionsPaged() {
        User user = createUser("John Doe", UserType.PERSONAL);
        User other = createUser("Jane Smith", UserType.PERSONAL);
        createTransaction(user, other, new BigDecimal("500"), TransactionType.SEND, TransactionStatus.SUCCESS);

        Page<Transaction> page = transactionRepository.filterTransactionsPaged(
                user.getUserId(), TransactionType.SEND, TransactionStatus.SUCCESS,
                null, null, new BigDecimal("100"), new BigDecimal("1000"),
                "John", PageRequest.of(0, 10)
        );

        assertFalse(page.isEmpty());
        assertEquals(1, page.getTotalElements());
    }

    @Test
    public void testGetTotalSent() {
        User user = createUser("User", UserType.PERSONAL);
        createTransaction(user, null, new BigDecimal("100"), TransactionType.SEND, TransactionStatus.SUCCESS);
        createTransaction(user, null, new BigDecimal("50"), TransactionType.SEND, TransactionStatus.SUCCESS);
        createTransaction(user, null, new BigDecimal("200"), TransactionType.SEND, TransactionStatus.FAILED);

        BigDecimal total = transactionRepository.getTotalSent(user.getUserId());
        // Should only sum SUCCESS: 100 + 50 = 150
        assertEquals(0, new BigDecimal("150.00").compareTo(total));
    }

    @Test
    public void testGetTopCustomers() {
        User business = createUser("Business", UserType.BUSINESS);
        User customer = createUser("Loyal Customer", UserType.PERSONAL);
        
        createTransaction(customer, business, new BigDecimal("1000"), TransactionType.SEND, TransactionStatus.SUCCESS);
        createTransaction(customer, business, new BigDecimal("500"), TransactionType.SEND, TransactionStatus.SUCCESS);

        List<TopCustomerResponse> result = transactionRepository.getTopCustomers(business.getUserId());

        assertEquals(1, result.size());
        assertEquals("Loyal Customer", result.get(0).getCustomerName());
        assertEquals(Long.valueOf(2), result.get(0).getTotalTransactions());
    }

    @Test
    public void testGetRevenueFromDate() {
        User business = createUser("Store", UserType.BUSINESS);
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
        
        createTransaction(null, business, new BigDecimal("250"), TransactionType.RECEIVE, TransactionStatus.SUCCESS);

        BigDecimal revenue = transactionRepository.getRevenueFromDate(business.getUserId(), lastWeek);
        assertTrue(revenue.compareTo(BigDecimal.ZERO) > 0);
    }
}