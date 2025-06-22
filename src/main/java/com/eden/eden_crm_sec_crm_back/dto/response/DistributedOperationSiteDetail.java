package com.eden.eden_crm_sec_crm_back.dto.response;

import com.eden.eden_crm_sec_crm_back.enums.WeekDaysEnum;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetTime;
import java.util.Set;

@Data
@Builder
public class DistributedOperationSiteDetail {
    private Long quantity;
    private Set<WeekDaysEnum> days;
    private OffsetTime fromTime;
    private OffsetTime toTime;
}
