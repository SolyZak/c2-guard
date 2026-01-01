package com.eden.eden_crm_sec_crm_back.dynamicscheduler.interfaces;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskExecutionLogEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.enums.ScheduledTaskStatus;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Getter
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractScheduledTaskFactory implements Runnable, ScheduledTaskFactory {

    protected final ObjectMapper mapper;
    protected final ScheduledTaskRepository taskRepository;
    protected final ScheduledTaskExecutionLogRepository logRepository;
    protected ScheduledTaskEntity taskEntity;

    @Override
    @Transactional
    public void run() {
        ScheduledTaskExecutionLogEntity execution = new ScheduledTaskExecutionLogEntity();
        execution.setTask(taskEntity);
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
        }
    }

    @Override
    public AbstractScheduledTaskFactory createInstance(
            ObjectMapper mapper,
            ScheduledTaskEntity taskEntity,
            ScheduledTaskRepository taskRepository,
            ScheduledTaskExecutionLogRepository logRepository
    ) {
        this.taskEntity = taskEntity;
        return this;
    }

    protected ObjectNode createObjectNode() {
        return mapper.createObjectNode();
    }

    protected <T> T readArguments(Class<T> clazz) {
        JsonNode args = taskEntity.getArguments();
        if (args == null || args.isNull()) {
            return null;
        }
        try {
            return mapper.treeToValue(args, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse task arguments", e);
        }
    }

    protected String getArg(String fieldName, String defaultValue) {
        JsonNode args = taskEntity.getArguments();
        if (args == null) return defaultValue;
        JsonNode node = args.get(fieldName);
        return node != null && node.isTextual() ? node.asText() : defaultValue;
    }

    protected int getArg(String fieldName, int defaultValue) {
        JsonNode args = taskEntity.getArguments();
        if (args == null) return defaultValue;
        JsonNode node = args.get(fieldName);
        return node != null && node.isInt() ? node.asInt() : defaultValue;
    }
}
