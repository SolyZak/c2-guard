package com.eden.eden_crm_sec_crm_back.dynamicscheduler.controllers;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.dtos.DateTimeScheduledTaskRequest;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.entities.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.services.TaskSchedulerService;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.tasks.TaskCurrentStatusJob;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/scheduled-tasks")
public class ScheduledTaskController {

    private final TaskSchedulerService taskSchedulerService;
    private final TaskCurrentStatusJob taskCurrentStatusJob;

    @PostMapping("/datetime-task")
    public ResponseEntity<?> createDateTimeTask(HttpServletRequest request, @RequestBody DateTimeScheduledTaskRequest taskRequest) {
        ScheduledTaskEntity taskEntity = taskCurrentStatusJob.createTask(taskRequest);
        taskSchedulerService.scheduleTaskIfExecuteToday(taskEntity);
        return ResponseEntity.ok(taskEntity);
    }

}
