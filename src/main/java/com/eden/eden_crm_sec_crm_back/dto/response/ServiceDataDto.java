package com.eden.eden_crm_sec_crm_back.dto.response;

import com.eden.eden_crm_sec_crm_back.enums.UnitEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ServiceDataDto {
    private Long id;
    private String name;
    private UnitEnum unit;
    private Boolean multiSite;
    private LocalDateTime createdDate;
    private List<ServiceDetailsDataDto> serviceDetails;
}
