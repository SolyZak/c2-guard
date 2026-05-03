package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ImmediateTaskCheckDetailDto(
    String checkName,
    String checkValue,
    LocalDateTime implementationDateTime,
    String evidenceImageUrl
) {}
