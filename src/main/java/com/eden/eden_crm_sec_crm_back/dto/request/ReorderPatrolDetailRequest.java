package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReorderPatrolDetailRequest {
    @NotNull
    private Long patrolDetailId;

    @NotNull
    @Min(1)
    private Integer newPosition;
}
