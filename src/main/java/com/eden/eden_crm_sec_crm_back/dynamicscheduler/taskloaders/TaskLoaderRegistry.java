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
    public void init() {
        createIfNotExists();
    }

    public void createIfNotExists() {
        List<ScheduledTaskEntity> loaderTasks = scheduledTaskRepository.findAllByTaskType(taskLoaderJob.getTaskType());
        if (loaderTasks.isEmpty()) {
            CronScheduledTaskRequest scheduledTaskRequest = CronScheduledTaskRequest.builder()
                    .name("TaskLoader - " + LocalDate.now())
                    .cronExpression("0 0 12 * * *") // 12:00 every day
                    .build();
            ScheduledTaskEntity task = taskLoaderJob.createTask(scheduledTaskRequest);
            taskSchedulerOperator.scheduleTask(task);
        }
    }
}
