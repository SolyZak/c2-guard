package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WorkforceSiteDistributionDto {
    private Long id;
    private String name;
    private List<WorkforceSiteDistributionServiceDto> services;
}
