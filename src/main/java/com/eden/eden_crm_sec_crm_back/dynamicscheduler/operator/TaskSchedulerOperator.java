package com.eden.eden_crm_sec_crm_back.dynamicscheduler.operator;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.enums.TaskExecutionType;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.interfaces.ScheduledTaskFactory;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
public class TaskSchedulerOperator {
    private final ApplicationContext applicationContext;
    private final ObjectMapper objectMapper;
    private final ThreadPoolTaskScheduler scheduler;
    private final ScheduledTaskRepository taskRepository;
    private final ScheduledTaskExecutionLogRepository logRepository;
    private final Map<String, ScheduledTaskFactory> factories = new HashMap<>();
    private final Map<UUID, ScheduledFuture<?>> scheduledFutures = new HashMap<>();

    public TaskSchedulerOperator(
        ApplicationContext applicationContext,
        ObjectMapper objectMapper,
        ThreadPoolTaskScheduler scheduler,
        ScheduledTaskRepository taskRepository,
        ScheduledTaskExecutionLogRepository logRepository,
        List<ScheduledTaskFactory> factoryList
    ) {
        this.applicationContext = applicationContext;
        this.objectMapper = objectMapper;
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
        List<ScheduledTaskEntity> tasks = getAllTodayTasks();
        tasks.forEach(this::scheduleTask);
    }

    public void scheduleTask(ScheduledTaskEntity task) {
        ScheduledTaskFactory factory = factories.get(task.getTaskType());
        if (factory == null) {
            log.error("No factory for taskType '{}', skipping task '{}'", task.getTaskType(), task.getName());
            return;
        }

        Runnable runnable = factory.createInstanceAndSetEntity(applicationContext, objectMapper, taskRepository, logRepository, task);

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

    public List<ScheduledTaskEntity> getAllTodayTasks() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime endOfDay = now.with(LocalTime.MAX);
        return taskRepository.findAllActiveTasksInDateRange(now, endOfDay)
                .stream()
                .filter(task -> {
                    if (task.getTypeOfExecution() != TaskExecutionType.CRON)
                        return true;
                    return isCronTypeAndWithInToday(task, now);
                })
                .toList();
    }

    public List<ScheduledTaskEntity> getAllTodayTasksExcludeLoaders(List<String> loaders) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime endOfDay = now.with(LocalTime.MAX);
        return taskRepository.findAllActiveTasksInDateRangeExcludeLoaders(now, endOfDay, loaders)
                .stream()
                .filter(task -> {
                    if (task.getTypeOfExecution() != TaskExecutionType.CRON)
                        return true;
                    return isCronTypeAndWithInToday(task, now);
                })
                .toList();
    }

    public boolean isDateTimeTypeAndWithInToday(ScheduledTaskEntity taskEntity, OffsetDateTime nowDateTime) {
        OffsetDateTime endOfDay = nowDateTime.with(LocalTime.MAX);
        boolean isDateTimeType = taskEntity.getTypeOfExecution() == TaskExecutionType.DATETIME;
        boolean isAfterStartOfDay = taskEntity.getPlannedExecutionTime().isAfter(nowDateTime);
        boolean isBeforeEndOfDay = taskEntity.getPlannedExecutionTime().isBefore(endOfDay);
        return isDateTimeType && isAfterStartOfDay && isBeforeEndOfDay;
    }

    public boolean isCronTypeAndWithInToday(ScheduledTaskEntity taskEntity, OffsetDateTime nowDateTime) {
        OffsetDateTime endOfDay = nowDateTime.with(LocalTime.MAX);
        boolean isCronType = taskEntity.getTypeOfExecution() == TaskExecutionType.CRON;
        try {
            CronExpression cron = CronExpression.parse(taskEntity.getCronExpression());
            return isCronType && Optional.ofNullable(cron.next(nowDateTime))
                    .map(dateTime -> !dateTime.isAfter(endOfDay))
                    .orElse(false);
        } catch (Exception e) {
            log.error("Invalid cron expression: " + taskEntity.getCronExpression(), e);
            return false;
        }
    }

    public boolean isStartDateTimeAndDurationAndWithInToday(ScheduledTaskEntity taskEntity, OffsetDateTime nowDateTime) {
        OffsetDateTime endOfDay = nowDateTime.with(LocalTime.MAX);
        boolean isStartTimeAndDurationType = taskEntity.getTypeOfExecution() == TaskExecutionType.START_TIME_AND_DURATION;
        boolean isStartTimeInPastOrToday = taskEntity.getStartDateTime().isBefore(endOfDay);
        return isStartTimeAndDurationType && isStartTimeInPastOrToday;
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down the Scheduler.........");
        scheduler.shutdown();
    }
}
