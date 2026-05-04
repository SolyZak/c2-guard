package com.eden.eden_crm_sec_crm_back.identity.dto;

import com.eden.eden_crm_sec_crm_back.objects.UserType;

public record UserRequest(
        Long userId,
        Long customerId,
        UserType userType,
        String username,
        String firstName,
        String lastName,
        String password,
        String email,
        boolean forceChangePassword,
        String customerLogo,
        String customerName    // NEW
) {
    public UserRequest(Long userId, Long customerId, UserType userType,
                       String username, String firstName, String lastName,
                       String password, String email, boolean forceChangePassword) {
        this(userId, customerId, userType, username, firstName, lastName,
                password, email, forceChangePassword, null, null);
    }

    public UserRequest(Long userId, Long customerId, UserType userType,
                       String username, String firstName, String lastName,
                       String password, String email, boolean forceChangePassword,
                       String customerLogo) {
        this(userId, customerId, userType, username, firstName, lastName,
                password, email, forceChangePassword, customerLogo, null);
    }
}