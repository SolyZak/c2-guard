package com.eden.eden_crm_sec_crm_back.task_management.application.dto.response;

import java.util.List;

public record TaskDefinitionResponse(
        Long id,
        String name,
        String severity,
        List<TaskCheckDefinitionResponse> checks
) {
}
