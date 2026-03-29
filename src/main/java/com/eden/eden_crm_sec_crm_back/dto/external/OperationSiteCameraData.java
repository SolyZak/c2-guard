package com.eden.eden_crm_sec_crm_back.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperationSiteCameraData {
    private Long cameraId;
    private String cameraName;
    private String cameraIp;
    private Long vendorId;
    private String vendorName;
}