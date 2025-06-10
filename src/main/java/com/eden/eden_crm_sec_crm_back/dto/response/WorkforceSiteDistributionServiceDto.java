package com.eden.eden_crm_sec_crm_back.dto.response;

import com.eden.eden_crm_sec_crm_back.enums.UnitEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WorkforceSiteDistributionServiceDto {
    private Long id;
    private String name;
    private Long hours;
    private Long days;
    private UnitEnum unit;
    private List<WorkforceSiteDistributionWorkingPeriodDto> periods;
}
