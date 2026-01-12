package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Builder;

@Builder
public record ValidateLocationResponse(
        boolean success
) {}
