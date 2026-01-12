package com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.factories;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.dtos.StartTimeDurationScheduledTaskRequest;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.entities.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.factories.base.ScheduledTaskCreationFactory;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.mappers.ScheduledTaskMapper;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Getter
public abstract non-sealed class StartTimeDurationScheduledTaskFactory extends AbstractScheduledTaskFactory implements ScheduledTaskCreationFactory<StartTimeDurationScheduledTaskRequest> {

    private final ScheduledTaskMapper scheduledTaskMapper;
    private final Validator validator;

    /**
     * Subclasses must provide a constructor with this signature.
     */
    protected StartTimeDurationScheduledTaskFactory (
        ApplicationContext applicationContext,
        ObjectMapper objectMapper,
        ScheduledTaskRepository taskRepository,
        ScheduledTaskExecutionLogRepository logRepository,
        ScheduledTaskMapper scheduledTaskMapper,
        Validator validator
    ) {
        super(applicationContext, objectMapper, taskRepository, logRepository);
        this.scheduledTaskMapper = scheduledTaskMapper;
        this.validator = validator;
    }

    @Override
    @Transactional
    public ScheduledTaskEntity createTask(StartTimeDurationScheduledTaskRequest request) {
        request.setTaskType(getTaskType());
        Set<ConstraintViolation<StartTimeDurationScheduledTaskRequest>> violations = validator.validate(request);
        if (!violations.isEmpty())
            throw new ConstraintViolationException(violations);

        ScheduledTaskEntity scheduledTask = scheduledTaskMapper.startTimeDurationTaskRequestToEntity(request);
        scheduledTask = taskRepository.save(scheduledTask);
        return scheduledTask;
    }

    @Override
    @Transactional
    public List<ScheduledTaskEntity> createTasks(List<StartTimeDurationScheduledTaskRequest> requests) {
        requests.forEach(request -> {
            request.setTaskType(getTaskType());
            Set<ConstraintViolation<StartTimeDurationScheduledTaskRequest>> violations = validator.validate(request);
            if (!violations.isEmpty())
                throw new ConstraintViolationException(violations);
        });

        List<ScheduledTaskEntity> scheduledTasks = scheduledTaskMapper.startTimeDurationTaskRequestsToEntities(requests);
        scheduledTasks = taskRepository.saveAll(scheduledTasks);
        return scheduledTasks;
    }
}
