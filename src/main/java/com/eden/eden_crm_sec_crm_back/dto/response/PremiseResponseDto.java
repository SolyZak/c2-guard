package com.eden.eden_crm_sec_crm_back.dto.response;

import java.util.List;

public record PremiseResponseDto(
        Long id,
        String name,
        String code,
        List<PatrolSummaryDto> patrols
) {
}
