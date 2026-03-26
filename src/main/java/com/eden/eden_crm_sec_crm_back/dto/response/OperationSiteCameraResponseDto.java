package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Data;

@Data
public class OperationSiteCameraResponseDto {
    private Long id;
    private CameraResponseDto camera;
    private Long operationSiteId;
}