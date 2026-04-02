package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Data;

@Data
public class LocationCameraResponseDto {
    private Long id;
    private CameraResponseDto camera;
    private Long locationId;
    private Long customerId;
}