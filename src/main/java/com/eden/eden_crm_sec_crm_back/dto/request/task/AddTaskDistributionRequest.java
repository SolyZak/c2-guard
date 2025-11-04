package com.eden.eden_crm_sec_crm_back.dto.request.task;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddTaskDistributionRequest {
    @NotNull(message = "patrolDistributionId must not be null")
    Long patrolDistributionId;
    @NotNull(message = "taskId must not be null")
    Long taskId;
    @NotNull(message = "checks list must not be null")
    @Valid  // 👈 ensures validation cascades to nested objects
    List<@Valid TaskCheckDTO> checks;
}
