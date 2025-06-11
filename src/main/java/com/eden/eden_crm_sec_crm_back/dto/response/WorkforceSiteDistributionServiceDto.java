package com.eden.eden_crm_sec_crm_back.dto.response;

import com.eden.eden_crm_sec_crm_back.enums.ActivityEnum;
import com.eden.eden_crm_sec_crm_back.enums.UnitEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class WorkforceSiteDistributionServiceDto {
    private Long id;
    private String name;
    private Long hours;
    private Long days;
    private UnitEnum unit;
    private Long qnt;
    private Set<ActivityEnum> activities;
    private List<WorkforceSiteDistributionWorkingPeriodDto> periods;
}
