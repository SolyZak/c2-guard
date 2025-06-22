package com.eden.eden_crm_sec_crm_back.dto.response;

import com.eden.eden_crm_sec_crm_back.enums.CustomTimezone;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WorkforceSiteDistributionDto {
    private Long id;
    private String name;
    private Long contractId;
    private String contractName;
    private Long customerId;
    private String customerName;
    private Double latitude;
    private Double longitude;
    private Double tolerance;
    private CustomTimezone timezone;
    private List<WorkforceSiteDistributionServiceDto> services;
}
