package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddPatrolRequest {
    @NotNull(message = "{validation.patrol.name.required}")
    private String patrolName;
    @NotNull(message = "{validation.patrol.frequency.required}")
    private String frequency;
    @NotNull(message = "{validation.patrol.frequency_rate.required}")
    private String frequencyRate;
    @NotNull(message = "{validation.patrol.details.required}")
    private List<AddPatrolDetailRequest> details;
}
