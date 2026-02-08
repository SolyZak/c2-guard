package com.eden.eden_crm_sec_crm_back.patrols.dtos.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PatrolTaskDetailsResponse(
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    String status,
    Long siteId,
    String siteName,
    Long locationId,
    Long serviceId,
    String serviceName,
    Boolean hasEvidence,
    String evidenceImage,
    Boolean commentCheck,
    String comment
) {}
