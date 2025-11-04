package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class TodayTaskEntryDto {
    private String taskName;
    private String patrolName;
    private String locationName;
    private String premiseName;
    private String patrolFreqType;
    private LocalDate endDate;
    private List<TodayTaskEntryTimesDto> times;
}
