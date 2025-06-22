package com.eden.eden_crm_sec_crm_back.dto.external;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetTime;

@Data
@Builder
public class AttendanceWorkingPeriodData {
    private Long id;
    private Long quantity;
    private OffsetTime fromTime;
    private OffsetTime toTime;
}
