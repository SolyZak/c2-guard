package com.eden.eden_crm_sec_crm_back.task_management.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskLocationChecksImageRequest {

    @NotNull(message = "Task definition ID is required")
    private Long taskDefinitionId;

    @NotNull(message = "Location ID is required")
    private Long locationId;

    @NotNull(message = "Task check definition ID is required")
    private Long taskCheckDefinitionId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @Size(max = 500, message = "Reference image URL must not exceed 500 characters")
    private String refImage;
}