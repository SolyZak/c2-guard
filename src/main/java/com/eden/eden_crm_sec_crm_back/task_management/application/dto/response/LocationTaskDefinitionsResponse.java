package com.eden.eden_crm_sec_crm_back.task_management.application.dto.response;

import java.util.List;

public record LocationTaskDefinitionsResponse(
        Long locationId,
        String locationName,
        List<TaskDefinitionWithChecksResponse> taskDefinitions
) {}