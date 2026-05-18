package com.eden.eden_crm_sec_crm_back.clients;

import com.eden.eden_crm_sec_crm_back.clients.dto.SendNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "${feign.clients.notification}", path = "/notifications")
public interface NotificationClient {

    @PostMapping("/send")
    Map<String, Object> sendNotification(@RequestBody SendNotificationRequest request);
}
