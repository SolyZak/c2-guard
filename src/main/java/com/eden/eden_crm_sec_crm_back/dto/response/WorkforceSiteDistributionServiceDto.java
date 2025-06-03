package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkforceSiteDistributionServiceDto {
    private Long id;
    private String name;
    private Long hours;
    private Long days;
}
