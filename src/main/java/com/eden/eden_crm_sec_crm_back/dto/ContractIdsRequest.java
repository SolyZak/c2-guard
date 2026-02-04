package com.eden.eden_crm_sec_crm_back.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record ContractIdsRequest(
    Set<Long> workforceIds
) {}
