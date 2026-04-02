package com.eden.eden_crm_sec_crm_back.identity;

import com.eden.eden_crm_sec_crm_back.identity.dto.UserRequest;

public interface KeycloakClient {

    Boolean userExists(String username);

    Boolean userExistsIgnoreUserId(String username, Long userId);

    void createUser(UserRequest userRequest);

    void deleteUser(String username);

    void updateUser(String username, UserRequest updatedRequest);

    void resetPassword(String username, String newPassword, boolean forceChangeOnFirstLogin);
}