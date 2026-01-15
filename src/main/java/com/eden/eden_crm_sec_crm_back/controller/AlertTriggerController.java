package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.AlertTriggerSeverityRequest;
import com.eden.eden_crm_sec_crm_back.dto.ServicePlatformWithTriggersResponse;
import com.eden.eden_crm_sec_crm_back.service.AlertTriggerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/alert-triggers")
@RequiredArgsConstructor
public class AlertTriggerController {
    
    private final AlertTriggerService alertTriggerService;

    @GetMapping
    public ResponseEntity<List<ServicePlatformWithTriggersResponse>> getAllAlertTriggers() {
        return ResponseEntity.ok(alertTriggerService.getAllAlertTriggers());
    }

    @PostMapping("/{id}/severity")
    public ResponseEntity<Map<String, String>> setAlertTriggerSeverity(
        @PathVariable Long id,
        @Valid @RequestBody AlertTriggerSeverityRequest request
    ) {
        alertTriggerService.setAlertTriggerSeverity(id, request);
        return ResponseEntity.ok(Map.of("message", "Alert trigger severity updated successfully"));
    }
}
