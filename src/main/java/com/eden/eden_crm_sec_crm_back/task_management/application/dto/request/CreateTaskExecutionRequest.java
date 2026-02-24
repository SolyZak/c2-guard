package com.eden.eden_crm_sec_crm_back.task_management.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateTaskExecutionRequest {

    @NotNull
    private Long workforceId;

    @NotNull
    private Long customerId;

}
