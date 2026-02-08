package com.eden.eden_crm_sec_crm_back.patrols.dtos.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PatrolReportResponseDto(
    Long id,
    String name,
    String code,
    List<PatrolSummaryDto> patrols
) {}
