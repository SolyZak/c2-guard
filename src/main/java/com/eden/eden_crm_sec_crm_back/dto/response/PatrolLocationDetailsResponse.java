package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PatrolLocationDetailsResponse(
        Long id,
        String name,
        List<PatrolTaskDetailsResponse> tasks
) {}
