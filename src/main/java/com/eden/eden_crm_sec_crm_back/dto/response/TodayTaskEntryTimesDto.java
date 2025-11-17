package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;
import java.time.OffsetTime;

@Data
@AllArgsConstructor
public class TodayTaskEntryTimesDto {
    private LocalTime startTime;
    private LocalTime endTime;

    private String status;
    private Long patrolDistributionId;
}
