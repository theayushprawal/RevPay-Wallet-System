package com.revpay.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "NOTIFICATIONS")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "noti_seq")
    @SequenceGenerator(name = "noti_seq", sequenceName = "GEN_NOTI_ID", allocationSize = 1)
    @Column(name = "NOTIFICATION_ID")
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "IS_READ")
    private String isRead;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(Long notificationId, User user,
                        String message, String type,
                        String isRead, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.user = user;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getIsRead() { return isRead; }
    public void setIsRead(String isRead) { this.isRead = isRead; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}