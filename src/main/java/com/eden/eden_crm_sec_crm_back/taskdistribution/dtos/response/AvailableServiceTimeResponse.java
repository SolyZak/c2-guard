package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response;

import lombok.Builder;

import java.time.OffsetTime;

@Builder
public record AvailableServiceTimeResponse(
    Long serviceTimeId,
    OffsetTime startTime,
    OffsetTime endTime
) {}
