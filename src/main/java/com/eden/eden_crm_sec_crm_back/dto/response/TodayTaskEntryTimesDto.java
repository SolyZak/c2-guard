package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetTime;

@Data
@AllArgsConstructor
public class TodayTaskEntryTimesDto {
    private OffsetTime startTime;
    private OffsetTime endTime;
}
