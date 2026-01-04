package com.eden.eden_crm_sec_crm_back.dynamicscheduler.taskloaders;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.dto.CreateScheduledTaskRequest;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.enums.TaskExecutionType;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.operator.TaskSchedulerOperator;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.service.TaskSchedulerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TaskLoaderRegistry {

    private final ScheduledTaskRepository scheduledTaskRepository;
    private final TaskSchedulerService taskSchedulerService;

    @PostConstruct
    public void init() {
        createIfNotExists();
    }

    public void createIfNotExists() {
        List<ScheduledTaskEntity> loaderTasks = scheduledTaskRepository.findAllByTaskType("Tasks24HoursLoader");
        if (loaderTasks.isEmpty()) {
            CreateScheduledTaskRequest scheduledTaskRequest = CreateScheduledTaskRequest.builder()
                    .name("TaskLoader - " + LocalDate.now())
                    .taskType("Tasks24HoursLoader")
                    .typeOfExecution(TaskExecutionType.CRON)
                    .cronExpression("0 0 12 * * *")
                    .createdAt(OffsetDateTime.now())
                    .isActive(true)
                    .build();
            taskSchedulerService.createAndScheduleTask(scheduledTaskRequest);
        }
    }
}
