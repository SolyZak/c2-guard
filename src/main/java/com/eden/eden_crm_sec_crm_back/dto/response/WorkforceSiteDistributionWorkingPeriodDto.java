package com.eden.eden_crm_sec_crm_back.dto.response;

import com.eden.eden_crm_sec_crm_back.enums.AttendStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class WorkforceSiteDistributionWorkingPeriodDto {
    private Long id;
    private LocalTime fromTime;
    private LocalTime toTime;
    private Boolean isWorking;
    private AttendStatus checkInStatus;
    private AttendStatus checkOutStatus;
}
