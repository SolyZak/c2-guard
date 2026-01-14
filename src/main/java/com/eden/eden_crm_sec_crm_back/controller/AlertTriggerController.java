package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.AlertTriggerDTO;
import com.eden.eden_crm_sec_crm_back.dto.AlertTriggerSeverityRequest;
import com.eden.eden_crm_sec_crm_back.dto.TriggerWithAlertTriggerResponse;
import com.eden.eden_crm_sec_crm_back.enums.ServicePlatformEnum;
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
    public ResponseEntity<Map<ServicePlatformEnum, List<TriggerWithAlertTriggerResponse>>> getAllAlertTriggers() {
        return ResponseEntity.ok(alertTriggerService.getAllAlertTriggers());
    }

    @PostMapping("/{id}/severity")
    public ResponseEntity<AlertTriggerDTO> setAlertTriggerSeverity(
        @PathVariable Long id,
        @Valid @RequestBody AlertTriggerSeverityRequest request
    ) {
        return ResponseEntity.ok(alertTriggerService.setAlertTriggerSeverity(id, request));
    }
}
