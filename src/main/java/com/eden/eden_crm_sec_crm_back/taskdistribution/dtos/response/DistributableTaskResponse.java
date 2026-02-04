package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response;

import lombok.Builder;

@Builder
public record DistributableTaskResponse(
    Long patrolDetailId,
    String taskName
) {}
