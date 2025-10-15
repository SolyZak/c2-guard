package com.eden.eden_crm_sec_crm_back.dto.request;

import lombok.Data;

@Data
public class LocationDto {
    String locationName;
    String accessType;
    Double longitude;
    Double latitude;
}
