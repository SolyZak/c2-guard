package com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.factories;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.entities.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.entities.ScheduledTaskExecutionLogEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.enums.ScheduledTaskStatus;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.enums.TaskExecutionType;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.factories.base.ScheduledTaskFactory;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.utils.JsonNodeUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Getter
public abstract sealed class AbstractScheduledTaskFactory
        implements Runnable, ScheduledTaskFactory
        permits CronScheduledTaskFactory, DateTimeScheduledTaskFactory, StartTimeDurationScheduledTaskFactory {

    private final Logger log = LoggerFactory.getLogger("ScheduledTaskFactory");
    protected final ApplicationContext applicationContext;
    protected final ObjectMapper objectMapper;
    protected final ScheduledTaskRepository taskRepository;
    protected final ScheduledTaskExecutionLogRepository logRepository;
    private ScheduledTaskEntity taskEntity;
    protected static final String TASK_ENTITY_NOT_NULL_MESSAGE = "taskEntity must be not null";

    /**
     * Subclasses must provide a constructor with this signature.
     */
    protected AbstractScheduledTaskFactory(
        ApplicationContext applicationContext,
        ObjectMapper objectMapper,
        ScheduledTaskRepository taskRepository,
        ScheduledTaskExecutionLogRepository logRepository
    ) {
        this.applicationContext = applicationContext;
        this.objectMapper = objectMapper;
        this.taskRepository = taskRepository;
        this.logRepository = logRepository;
    }

    @Override
    @Transactional
    public void run() {
        Objects.requireNonNull(taskEntity, TASK_ENTITY_NOT_NULL_MESSAGE);
        if (Boolean.FALSE.equals(taskEntity.getIsActive())) {
            log.info("Task [{}] '{}' InActive - Skipping the execution", taskEntity.getTaskType(), taskEntity.getName());
            return;
        }

        ScheduledTaskExecutionLogEntity execution = new ScheduledTaskExecutionLogEntity();
        execution.setTask(taskEntity);
        execution.setStatus(ScheduledTaskStatus.STARTED);
        execution.setStartedAt(OffsetDateTime.now());
        taskEntity.getExecutionLogs().add(execution);
        execution = logRepository.saveAndFlush(execution);

        ScheduledTaskEntity task = execution.getTask();
        log.info("Task [{}] is exists before execute", task.getId());

        log.info("Task [{}] '{}' started", task.getTaskType(), task.getName());

        try {
            log.info("Task [{}] '{}' arguments: {}", task.getTaskType(), task.getName(), task.getArguments());
            JsonNode result = performTask(task.getArguments());
            log.info("Task [{}] '{}' result: {}", task.getTaskType(), task.getName(), result);
            execution.setResult(result);
            execution.setStatus(ScheduledTaskStatus.SUCCESS);
            log.info("Task [{}] '{}' succeeded", task.getTaskType(), task.getName());
        } catch (Exception e) {
            execution.setStatus(ScheduledTaskStatus.FAILED);
            execution.setResult(JsonNodeUtils.createObjectNode().put("error", e.getMessage()));
            log.error("Task [{}] '{}' failed", task.getTaskType(), task.getName(), e);
        } finally {
            execution.setFinishedAt(OffsetDateTime.now());
            logRepository.saveAndFlush(execution);
            if (task.getTypeOfExecution() == TaskExecutionType.DATETIME) {
                task.setIsExecutionFinished(true);
                task = taskRepository.saveAndFlush(task);
            }
        }
    }

    @Override
    public AbstractScheduledTaskFactory setTask(ScheduledTaskEntity task) {
        try {
            this.taskEntity = task;
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate concrete ScheduledTaskFactory via reflection. Ensure the subclass has a matching constructor.", e);
        }
    }
}
