package com.eden.eden_crm_sec_crm_back.task_management.domain.service;

import com.eden.eden_crm_sec_crm_back.task_management.domain.exception.TaskDomainException;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TaskCheckValue;
import org.springframework.stereotype.Service;

/**
 * Domain service for TaskExecution business rules.
 * Contains pure domain logic — no repository injection.
 */
@Service
public class TaskExecutionDomainService {

    public void validateCheckValues(TaskCheckValue checkValues) {
        if (checkValues != null && !checkValues.isValid()) {
            throw new TaskDomainException("Invalid checkValues submitted for check execution");
        }
    }
}
