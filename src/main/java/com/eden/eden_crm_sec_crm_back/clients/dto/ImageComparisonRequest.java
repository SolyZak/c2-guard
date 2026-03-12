package com.eden.eden_crm_sec_crm_back.clients.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageComparisonRequest {
    private Long taskCheckExecutionId;
    private Long taskLocationChecksImageId;
    private String referenceImagePath;
    private String evidenceImagePath;
}