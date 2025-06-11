package com.eden.eden_crm_sec_crm_back.identity.dto;

import com.eden.eden_crm_sec_crm_back.objects.UserType;

public record UserRequest(
        Long userId,
        UserType userType,
        String username,
        String firstName,
        String lastName,
        String password,
        String email,
        Boolean forceChangePassword
) {
}
