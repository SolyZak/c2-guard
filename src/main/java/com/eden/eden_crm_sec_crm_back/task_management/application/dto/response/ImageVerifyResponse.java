package com.eden.eden_crm_sec_crm_back.task_management.application.dto.response;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.ImageQualityIssue;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ImageVerifyResponse {

    private boolean sameObject;
    private Boolean isBlurry;
    private Boolean isBright;
    private Boolean isAligned;
    private List<ImageQualityIssue> missingQuality;
}