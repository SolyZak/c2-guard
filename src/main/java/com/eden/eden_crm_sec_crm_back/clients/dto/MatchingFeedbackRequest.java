package com.eden.eden_crm_sec_crm_back.clients.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchingFeedbackRequest {
    private Long taskCheckExecutionId;
    private Long taskLocationChecksImageId;
    private Boolean matching;
}