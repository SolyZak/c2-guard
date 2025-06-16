package com.eden.eden_crm_sec_crm_back.dto.external;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class AttendanceWorkingPeriodData {
    private Long id;
    private Long quantity;
    private LocalTime fromTime;
    private LocalTime toTime;
}
