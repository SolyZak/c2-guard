package com.eden.eden_crm_sec_crm_back.task_management.domain.service;

import com.eden.eden_crm_sec_crm_back.task_management.domain.exception.TaskDomainException;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TaskCheckValue;
import org.springframework.stereotype.Service;

/**
 * Domain service for TaskDefinition business rules.
 * Contains pure domain logic — no repository injection.
 */
@Service
public class TaskDefinitionDomainService {

    public void validateCheckDefinition(TaskCheckValue checkSettings, String checkName) {
        if (checkSettings != null && !checkSettings.isValid()) {
            throw new TaskDomainException("Invalid checkSettings for check: " + checkName);
        }
    }
}
