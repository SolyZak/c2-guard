package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ImmediateTaskReportDetailResponse(
    List<ImmediateTaskCheckDetailDto> checks
) {}
