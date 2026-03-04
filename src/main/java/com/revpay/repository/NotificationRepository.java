package com.revpay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.model.Notification;
import com.revpay.model.User;
import com.revpay.model.enums.NotificationType;
import com.revpay.model.enums.YesNoStatus;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // All notifications for a user
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // Unread notifications for a user
    List<Notification> findByUserAndIsReadOrderByCreatedAtDesc(User user, YesNoStatus isRead);

    // Filter notifications by type (TRANSACTION, LOAN, INVOICE, etc.)
    List<Notification> findByUserAndType(User user, NotificationType type);

    Long countByUserAndIsRead(User user, YesNoStatus isRead);
}