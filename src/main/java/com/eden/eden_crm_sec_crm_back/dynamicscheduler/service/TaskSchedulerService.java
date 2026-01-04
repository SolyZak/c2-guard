package com.eden.eden_crm_sec_crm_back.dynamicscheduler.service;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.dto.CreateScheduledTaskRequest;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.mapper.ScheduledTaskMapper;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.operator.TaskSchedulerOperator;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskRepository;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskSchedulerService {

    private final ScheduledTaskRepository taskRepository;
    private final ScheduledTaskMapper scheduledTaskMapper;
    private final Validator validator;
    private final TaskSchedulerOperator schedulerOperator;

    @Transactional
    public ScheduledTaskEntity createTask(CreateScheduledTaskRequest scheduledTaskRequest) {
        Set<ConstraintViolation<CreateScheduledTaskRequest>> violations = validator.validate(scheduledTaskRequest);
        if (!violations.isEmpty())
            throw new ConstraintViolationException(violations);

        ScheduledTaskEntity scheduledTask = scheduledTaskMapper.createRequestToEntity(scheduledTaskRequest);
        scheduledTask = taskRepository.save(scheduledTask);
        return scheduledTask;
    }

    @Transactional
    public ScheduledTaskEntity createAndScheduleTask(CreateScheduledTaskRequest scheduledTaskRequest) {
        ScheduledTaskEntity scheduledTask = createTask(scheduledTaskRequest);
        schedulerOperator.scheduleTask(scheduledTask);
        return scheduledTask;
    }

    @Transactional
    public ScheduledTaskEntity createAndScheduleTaskIfExecuteToday(CreateScheduledTaskRequest scheduledTaskRequest) {
        OffsetDateTime now = OffsetDateTime.now();
        ScheduledTaskEntity scheduledTask = createTask(scheduledTaskRequest);
        if (
            schedulerOperator.isDateTimeTypeAndWithInToday(scheduledTask, now)
            || schedulerOperator.isCronTypeAndWithInToday(scheduledTask, now)
            || schedulerOperator.isStartDateTimeAndDurationAndWithInToday(scheduledTask, now)
        )
            schedulerOperator.scheduleTask(scheduledTask);
        return scheduledTask;
    }
}
