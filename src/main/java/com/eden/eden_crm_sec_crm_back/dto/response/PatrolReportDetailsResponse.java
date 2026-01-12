package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PatrolReportDetailsResponse(
    String premiseName,
    String patrolName,
    List<PatrolLocationDetailsResponse> locations
) {}
