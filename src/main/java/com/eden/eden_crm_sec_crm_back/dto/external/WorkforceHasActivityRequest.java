package com.eden.eden_crm_sec_crm_back.dto.external;

import com.eden.eden_crm_sec_crm_back.enums.ActivityEnum;
import jakarta.validation.constraints.NotNull;

public record WorkforceHasActivityRequest(
    @NotNull
    Long contractOperationSiteDistributionDetailId,
    @NotNull
    ActivityEnum activity
) {}
