package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BulkReorderPatrolDetailRequest {

    @NotEmpty
    private List<Long> orderedDetailIds;
}
