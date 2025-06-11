package com.eden.eden_crm_sec_crm_back.identity;

import com.eden.eden_crm_sec_crm_back.identity.dto.UserRequest;

public interface KeycloakClient {
    Boolean userExits(String username);
    Boolean userExitsIgnoreUserId(String username, Long userId);
    void createUser(UserRequest userRequest);
    void updateUser(String username, UserRequest updatedRequest);
    void deleteUser(String username);
}
