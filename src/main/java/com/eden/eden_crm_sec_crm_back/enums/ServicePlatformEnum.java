package com.eden.eden_crm_sec_crm_back.enums;

import lombok.Getter;

@Getter
public enum ServicePlatformEnum {
    ATTENDANCE("ATTENDANCE", 1L),
    PATROLS("PATROLS", 2L),
    VISITORS("VISITORS", 3L),
    INCIDENTS("INCIDENTS", 4L);

    // You might also want to add getters for the fields
    private final String code;
    private final Long id;

    ServicePlatformEnum(String code, Long id) {
        this.code = code;
        this.id = id;
    }

    public static ServicePlatformEnum fromCode(String code) {
        for (ServicePlatformEnum platform : values()) {
            if (platform.code.equalsIgnoreCase(code)) {
                return platform;
            }
        }
        throw new IllegalArgumentException("Unknown ServicePlatformEnum code: " + code);
    }

}
