package com.eden.eden_crm_sec_crm_back.clients;

import com.eden.eden_crm_sec_crm_back.dto.TriggerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "${feign.clients.visitor}", path = "/visitor")
public interface VisitorFeignClient {
    @GetMapping(value = "/triggers")
    List<TriggerResponse> getAllTriggers();

    @GetMapping(value = "/triggers/{triggerId}")
    TriggerResponse getTrigger(@RequestParam Long triggerId);
}
