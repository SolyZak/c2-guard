package com.eden.eden_crm_sec_crm_back.dynamicscheduler.operators;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.entities.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.factories.base.ScheduledTaskFactory;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskRepository;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.enums.TaskExecutionType.*;

@Slf4j
@Service
public class TaskSchedulerOperator {
    private final ThreadPoolTaskScheduler scheduler;
    private final ScheduledTaskRepository taskRepository;
    private final Map<String, Class<? extends ScheduledTaskFactory>> factoryTypes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ScheduledFuture<?>> scheduledFutures = new ConcurrentHashMap<>();
    private final ApplicationContext applicationContext;

    public TaskSchedulerOperator(
            ThreadPoolTaskScheduler scheduler,
            ScheduledTaskRepository taskRepository,
            ApplicationContext applicationContext
    ) {
        this.scheduler = scheduler;
        this.taskRepository = taskRepository;
        this.applicationContext = applicationContext;

        String[] beanNames = applicationContext.getBeanNamesForType(ScheduledTaskFactory.class, true, false);
        for (String beanName : beanNames) {
            Class<?> type = applicationContext.getType(beanName);
            if (type == null || !ScheduledTaskFactory.class.isAssignableFrom(type)) continue;
            @SuppressWarnings("unchecked")
            Class<? extends ScheduledTaskFactory> cls = (Class<? extends ScheduledTaskFactory>) type;
            try {
                ScheduledTaskFactory temp = (ScheduledTaskFactory) applicationContext.getBean(beanName);
                String taskType = temp.getTaskType();
                if (factoryTypes.put(taskType, cls) != null) {
                    throw new IllegalStateException("Duplicate TaskFactory for type: " + taskType);
                }
            } catch (Exception e) {
                log.warn("Skipping bean '{}' while registering factories: {}", beanName, e.getMessage());
            }
        }
    }

    @PostConstruct
    public void init() {
        log.info("Registered task types: {}", factoryTypes.keySet());
        scheduleAllActiveTasks();
    }

    public void scheduleAllActiveTasks() {
        List<ScheduledTaskEntity> tasks = getAllTodayTasks();
        tasks.forEach(this::scheduleTask);
    }

    public void scheduleTask(ScheduledTaskEntity task) {
        Class<? extends ScheduledTaskFactory> factoryClass = factoryTypes.get(task.getTaskType());
        if (factoryClass == null) {
            log.error("No factory for taskType '{}', skipping task '{}'", task.getTaskType(), task.getName());
            return;
        }

        ScheduledTaskFactory factoryInstance;
        try {
            factoryInstance = applicationContext.getBean(factoryClass);
        } catch (Exception e) {
            log.error("Failed to obtain factory bean for type {}", task.getTaskType(), e);
            return;
        }

        Runnable runnable = factoryInstance.setTask(task);

        scheduledFutures.compute(task.getId(), (id, prev) -> {
            if (prev != null && !prev.isDone()) {
                try {
                    prev.cancel(false);
                } catch (Exception e) {
                    log.warn("Failed to cancel previous future for task {}", task.getId(), e);
                }
            }

            ScheduledFuture<?> future;
            try {
                switch (task.getTypeOfExecution()) {
                    case CRON -> future = scheduler.schedule(runnable, new CronTrigger(task.getCronExpression(), ZoneId.systemDefault()));
                    case DATETIME -> future = scheduler.schedule(runnable, task.getPlannedExecutionTime().toInstant());
                    case START_TIME_AND_DURATION -> future = scheduler.scheduleAtFixedRate(
                            runnable,
                            task.getStartDateTime().toInstant(),
                            task.getDuration()
                    );
                    default -> {
                        log.warn("Unknown execution type for task '{}', skipping scheduling", task.getName());
                        return null;
                    }
                }
            } catch (Exception e) {
                log.error("Failed to schedule task '{}' (type: {})", task.getName(), task.getTaskType(), e);
                return prev;
            }

            switch (task.getTypeOfExecution()) {
                case CRON -> {
                    CronExpression cron = CronExpression.parse(task.getCronExpression());
                    log.info("Scheduled task '{}', (type: {}), next run at: {}", task.getName(), task.getTaskType(), cron.next(OffsetDateTime.now()));
                }
                case DATETIME -> log.info("Scheduled task '{}' (type: {}), at: {}", task.getName(), task.getTaskType(), task.getPlannedExecutionTime());
                case START_TIME_AND_DURATION -> log.info("Scheduled task '{}' (type: {})", task.getName(), task.getTaskType());
                default -> {
                    log.warn("Unknown execution type for task '{}'", task.getName());
                    return null;
                }
            }
            return future;
        });
    }

    public void cancelTask(UUID taskId) {
        scheduledFutures.computeIfPresent(taskId, (id, future) -> {
            try {
                future.cancel(false);
            } catch (Exception e) {
                log.warn("Failed to cancel task {}", id, e);
            }
            log.info("Cancelled scheduled task ID: {}", taskId);
            return null;
        });
    }

    public List<ScheduledTaskEntity> getAllTodayTasks() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startOfDay = now.with(LocalTime.MIN);
        OffsetDateTime endOfDay = now.with(LocalTime.MAX);
        return taskRepository.findAllActiveTasksInDateRange(startOfDay, endOfDay)
                .stream()
                .filter(task -> {
                    if (task.getTypeOfExecution() != CRON)
                        return true;
                    return isCronTypeAndWithInToday(task, now);
                })
                .toList();
    }

    public List<ScheduledTaskEntity> getAllTodayTasksExcludeLoaders(List<String> loaders) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startOfDay = now.with(LocalTime.MIN);
        OffsetDateTime endOfDay = now.with(LocalTime.MAX);
        log.info("Loading tasks from datetime: {}", startOfDay);
        log.info("Loading tasks to datetime: {}", endOfDay);
        return taskRepository.findAllActiveTasksInDateRangeExcludeLoaders(startOfDay, endOfDay, loaders)
                .stream()
                .filter(task -> {
                    if (task.getTypeOfExecution() != CRON)
                        return true;
                    return isCronTypeAndWithInToday(task, now);
                })
                .toList();
    }

    public boolean isDateTimeTypeAndWithInToday(ScheduledTaskEntity taskEntity, OffsetDateTime nowDateTime) {
        OffsetDateTime startOfDay = nowDateTime.with(LocalTime.MIN);
        OffsetDateTime endOfDay = nowDateTime.with(LocalTime.MAX);
        boolean isDateTimeType = taskEntity.getTypeOfExecution() == DATETIME;
        if (!isDateTimeType)
            return false;
        boolean isAfterStartOfDay = taskEntity.getPlannedExecutionTime().isAfter(startOfDay);
        boolean isBeforeEndOfDay = taskEntity.getPlannedExecutionTime().isBefore(endOfDay);
        return isAfterStartOfDay && isBeforeEndOfDay;
    }

    public boolean isCronTypeAndWithInToday(ScheduledTaskEntity taskEntity, OffsetDateTime nowDateTime) {
        OffsetDateTime endOfDay = nowDateTime.with(LocalTime.MAX);
        boolean isCronType = taskEntity.getTypeOfExecution() == CRON;
        if (!isCronType)
            return false;
        try {
            CronExpression cron = CronExpression.parse(taskEntity.getCronExpression());
            return Optional.ofNullable(cron.next(nowDateTime))
                    .map(dateTime -> !dateTime.isAfter(endOfDay))
                    .orElse(false);
        } catch (Exception e) {
            log.error("Invalid cron expression: " + taskEntity.getCronExpression(), e);
            return false;
        }
    }

    public boolean isStartDateTimeAndDurationAndWithInToday(ScheduledTaskEntity taskEntity, OffsetDateTime nowDateTime) {
        OffsetDateTime endOfDay = nowDateTime.with(LocalTime.MAX);
        boolean isStartTimeAndDurationType = taskEntity.getTypeOfExecution() == START_TIME_AND_DURATION;
        if (!isStartTimeAndDurationType)
            return false;
        return taskEntity.getStartDateTime().isBefore(endOfDay);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down the Scheduler.........");
        scheduler.shutdown();
    }
}
