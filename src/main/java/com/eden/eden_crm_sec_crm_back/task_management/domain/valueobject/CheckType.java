package com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject;

import com.eden.eden_crm_sec_crm_back.task_management.domain.exception.TaskDomainException;

public enum CheckType {
    TEXT,
    NUMBER,
    DECIMAL,
    LIST;

    public static CheckType fromString(String value) {
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new TaskDomainException("Invalid checkType value: " + value);
        }
    }
}
