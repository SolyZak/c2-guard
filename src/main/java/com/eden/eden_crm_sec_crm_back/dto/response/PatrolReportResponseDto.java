package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PatrolReportResponseDto(
        Long id,
        String name,
        String code,
        List<PatrolSummaryDto> patrols
) {}
