package com.eden.eden_crm_sec_crm_back.dto.response;

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
        Long serviceId,
        String serviceName,
        Boolean hasEvidence,
        String evidenceImage,
        Long locationId,
        Boolean commentCheck,
        String comment
) {}
