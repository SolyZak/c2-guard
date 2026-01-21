package com.eden.eden_crm_sec_crm_back.dto.response;

import com.eden.eden_crm_sec_crm_back.objects.UserType;

import lombok.Builder;

@Builder
public record CustomerUserInfoResponse(
        String id,
        String name,
        UserType type,
        Long customerId
) {}
