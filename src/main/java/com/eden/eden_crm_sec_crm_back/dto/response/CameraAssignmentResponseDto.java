package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Data;

@Data
public class CameraAssignmentResponseDto {
    private Long id;
    private String name;
    private String ip;
    private CameraResponseDto.VendorData vendor;
    private CameraResponseDto.CustomerData customer;
    private boolean assigned;
}