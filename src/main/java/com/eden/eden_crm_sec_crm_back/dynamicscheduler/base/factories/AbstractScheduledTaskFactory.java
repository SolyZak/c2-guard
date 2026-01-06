package com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.factories;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.entities.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.entities.ScheduledTaskExecutionLogEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.enums.ScheduledTaskStatus;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.enums.TaskExecutionType;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.factories.base.ScheduledTaskFactory;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.mappers.ScheduledTaskMapper;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Validator;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Constructor;
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
        logRepository.save(execution);

        log.info("Task [{}] '{}' started", taskEntity.getTaskType(), taskEntity.getName());

        try {
            JsonNode result = performTask(taskEntity.getArguments());
            execution.setResult(result);
            execution.setStatus(ScheduledTaskStatus.SUCCESS);
            log.info("Task [{}] '{}' succeeded", taskEntity.getTaskType(), taskEntity.getName());
        } catch (Exception e) {
            execution.setStatus(ScheduledTaskStatus.FAILED);
            execution.setResult(createObjectNode().put("error", e.getMessage()));
            log.error("Task [{}] '{}' failed", taskEntity.getTaskType(), taskEntity.getName(), e);
        } finally {
            execution.setFinishedAt(OffsetDateTime.now());
            logRepository.save(execution);
            if (taskEntity.getTypeOfExecution() == TaskExecutionType.DATETIME) {
                taskEntity.setIsExecutionFinished(true);
                taskRepository.save(taskEntity);
            }
        }
    }

    @Override
    public AbstractScheduledTaskFactory createInstanceAndSetEntity(ScheduledTaskFactory factory, ScheduledTaskEntity task) {
        try {
            Constructor<? extends AbstractScheduledTaskFactory> ctor = this.getClass().getDeclaredConstructor(
                    ApplicationContext.class,
                    ObjectMapper.class,
                    ScheduledTaskRepository.class,
                    ScheduledTaskExecutionLogRepository.class,
                    ScheduledTaskMapper.class,
                    Validator.class
            );
            var instance = ctor.newInstance(
                factory.getApplicationContext(),
                factory.getObjectMapper(),
                factory.getTaskRepository(),
                factory.getLogRepository(),
                factory.getScheduledTaskMapper(),
                factory.getValidator()
            );
            instance.taskEntity = task;
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate concrete ScheduledTaskFactory via reflection. Ensure the subclass has a matching constructor.", e);
        }
    }

    protected ObjectNode createObjectNode() {
        return objectMapper.createObjectNode();
    }

    protected <T> T readArguments(Class<T> clazz) {
        Objects.requireNonNull(taskEntity, TASK_ENTITY_NOT_NULL_MESSAGE);

        JsonNode args = taskEntity.getArguments();
        if (args == null || args.isNull()) {
            return null;
        }
        try {
            return objectMapper.treeToValue(args, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse task arguments", e);
        }
    }

    protected String getArg(String fieldName, String defaultValue) {
        Objects.requireNonNull(taskEntity, TASK_ENTITY_NOT_NULL_MESSAGE);

        JsonNode args = taskEntity.getArguments();
        if (args == null) return defaultValue;
        JsonNode node = args.get(fieldName);
        return node != null && node.isTextual() ? node.asText() : defaultValue;
    }

    protected int getArg(String fieldName, int defaultValue) {
        Objects.requireNonNull(taskEntity, TASK_ENTITY_NOT_NULL_MESSAGE);

        JsonNode args = taskEntity.getArguments();
        if (args == null) return defaultValue;
        JsonNode node = args.get(fieldName);
        return node != null && node.isInt() ? node.asInt() : defaultValue;
    }
}
