package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WorkforceSiteDistributionDto {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Double tolerance;
    private List<WorkforceSiteDistributionServiceDto> services;
}
