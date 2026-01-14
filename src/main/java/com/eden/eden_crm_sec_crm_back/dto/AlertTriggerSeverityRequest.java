package com.eden.eden_crm_sec_crm_back.dto;

import com.eden.eden_crm_sec_crm_back.enums.Severity;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AlertTriggerSeverityRequest(
    @NotNull
    Severity severity
) {}
