package com.eden.eden_crm_sec_crm_back.task_management.application.dto.response;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TaskCheckValue;

public record TaskCheckDefinitionResponse(
                Long id,
                String name,
                String severity,
                String checkType,
                TaskCheckValue checkSettings,
                boolean hasEvidence,
                boolean hasComment) {
}
