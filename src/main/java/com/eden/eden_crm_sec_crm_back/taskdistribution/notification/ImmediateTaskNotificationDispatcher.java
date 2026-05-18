package com.eden.eden_crm_sec_crm_back.taskdistribution.notification;

import com.eden.eden_crm_sec_crm_back.clients.AttendanceFeignClient;
import com.eden.eden_crm_sec_crm_back.clients.NotificationClient;
import com.eden.eden_crm_sec_crm_back.clients.dto.SendNotificationRequest;
import com.eden.eden_crm_sec_crm_back.taskdistribution.events.ImmediateTaskAssignedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImmediateTaskNotificationDispatcher {

    private static final String NOTIFICATION_TYPE = "IMMEDIATE_TASK_ASSIGNED";
    private static final String TITLE = "Immediate Task Assigned";

    private final NotificationClient notificationClient;
    private final AttendanceFeignClient attendanceClient;

    @Async
    @EventListener
    public void onImmediateTaskAssigned(ImmediateTaskAssignedEvent event) {
        String body = "You have been assigned an immediate task on " + event.locationName();
        String distributionId = String.valueOf(event.immediateTaskDistributionId());

        for (Long workforceId : event.workforceIds()) {
            String token = fetchToken(workforceId);
            if (token == null) {
                continue;
            }
            try {
                notificationClient.sendNotification(SendNotificationRequest.builder()
                    .targetToken(token)
                    .userId(String.valueOf(workforceId))
                    .title(TITLE)
                    .body(body)
                    .notificationType(NOTIFICATION_TYPE)
                    .data(Map.of("immediateTaskDistributionId", distributionId))
                    .build());
                log.info("Immediate task notification sent: workforceId={}, immediateTaskDistributionId={}",
                    workforceId, distributionId);
            } catch (Exception e) {
                log.error("Immediate task notification failed: workforceId={}, immediateTaskDistributionId={}: {}",
                    workforceId, distributionId, e.getMessage());
            }
        }
    }

    private String fetchToken(Long workforceId) {
        try {
            String token = attendanceClient.getPushToken(String.valueOf(workforceId));
            if (token != null && !token.isBlank()) {
                return token;
            }
            log.warn("No FCM token found for workforceId={}", workforceId);
        } catch (Exception e) {
            log.error("Failed to fetch FCM token for workforceId={}: {}", workforceId, e.getMessage());
        }
        return null;
    }
}
