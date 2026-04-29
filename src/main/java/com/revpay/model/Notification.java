package com.revpay.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revpay.model.enums.NotificationType;
import com.revpay.model.enums.YesNoStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "NOTIFICATIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "noti_seq")
    @SequenceGenerator(name = "noti_seq", sequenceName = "GEN_NOTI_ID", allocationSize = 1)
    @Column(name = "NOTIFICATION_ID")
    private Long notificationId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY) // OPTIMIZATION: Changed from EAGER (default) to LAZY
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "MESSAGE")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_READ")
    private YesNoStatus isRead;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
}