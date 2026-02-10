package com.eden.eden_crm_sec_crm_back.patrols.dtos.response;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record PatrolTaskDetailsResponse(
    Long id,
    String name,
    OffsetDateTime startDateTime,
    OffsetDateTime endDateTime,
    String status,
    Long siteId,
    String siteName,
    Long serviceId,
    String serviceName,
    Boolean hasEvidence,
    String evidenceImage,
    Boolean commentCheck,
    String comment
) {}
