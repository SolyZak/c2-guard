package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Data;

@Data
public class ServiceDetailsDropdownDto {
    private Long id;
    private String serviceName;
    private Long serviceId;
    private Long hours;
    private Long days;
}
