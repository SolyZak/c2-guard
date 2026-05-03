package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.payloads;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TaskCheckExecutionDetailPayload(
    Long id,
    String checkName,
    String checkValues,
    String evidenceImageUrl,
    LocalDateTime createdAt
) {}
