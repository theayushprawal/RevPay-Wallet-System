package com.revpay.model;

import com.revpay.model.enums.NotificationType;
import com.revpay.model.enums.YesNoStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "NOTIFICATION_PREFERENCES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notif_pref_seq")
    @SequenceGenerator(name = "notif_pref_seq", sequenceName = "GEN_NOTIF_PREF_ID", allocationSize = 1)
    private Long preferenceId;

    // OPTIMIZATION: Changed from EAGER (default) to LAZY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "ENABLED")
    private YesNoStatus enabled;
}