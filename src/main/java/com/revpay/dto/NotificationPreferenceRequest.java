package com.revpay.dto;

import com.revpay.model.enums.NotificationType;
import com.revpay.model.enums.YesNoStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceRequest {

    private Long userId;
    private NotificationType type;
    private YesNoStatus enabled;
}