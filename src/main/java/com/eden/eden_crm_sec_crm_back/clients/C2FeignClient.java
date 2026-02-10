package com.eden.eden_crm_sec_crm_back.clients;

import com.eden.eden_crm_sec_crm_back.clients.dto.AlertResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "${feign.clients.c2}", path = "/crm-sec-c2")
public interface C2FeignClient {
    @GetMapping(value = "/alerts")
    List<AlertResponse> getAllAlerts();
}
