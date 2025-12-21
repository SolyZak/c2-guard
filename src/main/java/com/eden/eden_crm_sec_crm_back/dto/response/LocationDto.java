package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record LocationDto(
        Long locationId,
        String locationName,
        Double longitude,
        Double latitude
) {
}
