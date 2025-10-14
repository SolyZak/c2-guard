package com.eden.eden_crm_sec_crm_back.dto.response;

public record CustomerSitePremiseResponseDto(
     Long id,
     String name,
     Double latitude,
     Double longitude,
     Double tolerance,
     String premiseName) {
}
