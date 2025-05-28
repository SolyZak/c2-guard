package com.eden.eden_crm_sec_crm_back.utils;

import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.objects.UserType;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class Utils {

    public static UserData getLoggedInCustomer() {
        try {
            return UserData.builder()
                    .id("1")
                    .name("Test Customer")
                    .type(UserType.CUSTOMER)
                    .build();
        } catch (Exception e) {
            log.error("Exception when trying to get logged in customer data exception: {}", e.getMessage());
        }
        return null;
    }

    public static Long getLoggedInCustomerId() {
        return Long.valueOf(Objects.requireNonNull(getLoggedInCustomer()).getId());
    }
}
