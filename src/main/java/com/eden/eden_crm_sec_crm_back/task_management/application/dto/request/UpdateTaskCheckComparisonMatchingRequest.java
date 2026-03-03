package com.eden.eden_crm_sec_crm_back.task_management.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTaskCheckComparisonMatchingRequest {
    @NotNull
    private Boolean matching;
}