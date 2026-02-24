package com.eden.eden_crm_sec_crm_back.task_management.application.dto.response;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TaskCheckValue;

import java.time.LocalDateTime;

public record TaskCheckExecutionResponse(
                Long id,
                Long taskCheckDefinitionId,
                Long taskExecutionId,
                String checkType,
                TaskCheckValue checkValues,
                String evidenceImagePath,
                String comment,
                LocalDateTime createdAt) {
}
