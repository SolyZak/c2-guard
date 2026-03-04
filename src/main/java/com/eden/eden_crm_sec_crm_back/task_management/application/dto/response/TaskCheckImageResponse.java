package com.eden.eden_crm_sec_crm_back.task_management.application.dto.response;

public record TaskCheckImageResponse(
        Long taskCheckDefinitionId,
        Long locationId,
        String imageUrl
) {}