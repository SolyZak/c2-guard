package com.eden.eden_crm_sec_crm_back.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {
    private String targetToken;
    private String userId;
    private String title;
    private String body;
    private String notificationType;
    private Map<String, String> data;
}
