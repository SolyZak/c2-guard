package com.eden.eden_crm_sec_crm_back.dynamicscheduler.taskloaders;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.dtos.CronScheduledTaskRequest;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.entities.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.operators.TaskSchedulerOperator;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.tasks.TaskLoaderJob;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskLoaderRegistry {

    private final ScheduledTaskRepository scheduledTaskRepository;
    private final TaskSchedulerOperator taskSchedulerOperator;
    private final TaskLoaderJob taskLoaderJob;

    @PostConstruct
    @Transactional
    public void init() {
        List<ScheduledTaskEntity> loaderTasks = scheduledTaskRepository.findAllByTaskType(taskLoaderJob.getTaskType());
        createIfNotExists(loaderTasks);
        scheduleIfExists(loaderTasks);
    }

    public void createIfNotExists(List<ScheduledTaskEntity> loaderTasks) {
        if (!loaderTasks.isEmpty())
            return;

        CronScheduledTaskRequest scheduledTaskRequest = CronScheduledTaskRequest.builder()
                .name("TaskLoader - " + LocalDate.now())
                .cronExpression("0 0 0 * * *") // midnight every day
                .build();
        ScheduledTaskEntity task = taskLoaderJob.createTask(scheduledTaskRequest);
        taskSchedulerOperator.scheduleTask(task);
    }

    public void scheduleIfExists(List<ScheduledTaskEntity> loaderTasks) {
        if (loaderTasks.isEmpty())
            return;

        loaderTasks.forEach(taskSchedulerOperator::scheduleTask);
    }
}
