package com.eden.eden_crm_sec_crm_back.dynamicscheduler.service;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.interfaces.ScheduledTaskFactory;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
public class TaskSchedulerService {

    private final ObjectMapper mapper;
    private final ThreadPoolTaskScheduler scheduler;
    private final ScheduledTaskRepository taskRepository;
    private final ScheduledTaskExecutionLogRepository logRepository;
    private final Map<String, ScheduledTaskFactory> factories = new HashMap<>();
    private final Map<UUID, ScheduledFuture<?>> scheduledFutures = new HashMap<>();

    public TaskSchedulerService(
        ObjectMapper mapper,
        ThreadPoolTaskScheduler scheduler,
        ScheduledTaskRepository taskRepository,
        ScheduledTaskExecutionLogRepository logRepository,
        List<ScheduledTaskFactory> factoryList
    ) {
        this.mapper = mapper;
        this.scheduler = scheduler;
        this.taskRepository = taskRepository;
        this.logRepository = logRepository;
        for (ScheduledTaskFactory factory : factoryList) {
            String type = factory.getTaskType();
            if (factories.put(type, factory) != null) {
                throw new IllegalStateException("Duplicate TaskFactory for type: " + type);
            }
        }
    }

    @PostConstruct
    public void init() {
        log.info("Registered task types: {}", factories.keySet());
        scheduleAllActiveTasks();
    }

    public void scheduleAllActiveTasks() {
        List<ScheduledTaskEntity> tasks = taskRepository.findAllByIsActiveTrue();
        tasks.forEach(this::scheduleTask);
    }

    public void scheduleTask(ScheduledTaskEntity task) {
        ScheduledTaskFactory factory = factories.get(task.getTaskType());
        if (factory == null) {
            log.error("No factory for taskType '{}', skipping task '{}'", task.getTaskType(), task.getName());
            return;
        }

        Runnable runnable = factory.createInstance(mapper, task, taskRepository, logRepository);

        ScheduledFuture<?> future;
        switch (task.getTypeOfExecution()) {
            case CRON -> future = scheduler.schedule(runnable, new CronTrigger(task.getCronExpression(), ZoneId.systemDefault()));
            case DATETIME -> future = scheduler.schedule(runnable, task.getPlannedExecutionTime().toInstant());
            case START_TIME_AND_DURATION -> future = scheduler.scheduleAtFixedRate(
                    runnable,
                    task.getStartDateTime().toInstant(),
                    task.getDuration()
            );
            default -> {
                log.warn("Unknown execution type for task '{}'", task.getName());
                return;
            }
        }

        // Cancel previous if exists
        Optional.ofNullable(scheduledFutures.put(task.getId(), future))
                .ifPresent(prev -> {
                        if (prev.isDone()) {
                            scheduledFutures.remove(task.getId());
                            scheduledFutures.put(task.getId(), future);
                        }
                        else
                            prev.cancel(false);
                });

        log.info("Scheduled task '{}' (type: {})", task.getName(), task.getTaskType());
    }

    public void cancelTask(UUID taskId) {
        ScheduledFuture<?> future = scheduledFutures.remove(taskId);
        if (future != null) {
            future.cancel(false);
            log.info("Cancelled scheduled task ID: {}", taskId);
        }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}
