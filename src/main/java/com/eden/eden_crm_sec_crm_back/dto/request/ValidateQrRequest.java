package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateQrRequest {

    @NotBlank(message = "Payload cannot be blank")
    private String payload;   // raw text from the QR code

    @NotNull(message = "Patrol Distribution ID is required")
    private Long patrolDistributionId;

    @NotNull(message = "Task ID is required")
    private Long taskId;

    @NotNull(message = "Location ID is required")
    private Long locationId;
}