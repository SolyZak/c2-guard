package com.eden.eden_crm_sec_crm_back.dynamicscheduler.factory;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.dto.CreateScheduledTaskRequest;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.interfaces.AbstractScheduledTaskFactory;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.mapper.ScheduledTaskMapper;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.service.TaskSchedulerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TaskMissedStatusJob extends AbstractScheduledTaskFactory {

//    private final TaskSchedulerService taskSchedulerService;
    private final ScheduledTaskMapper scheduledTaskMapper;

    public TaskMissedStatusJob(
            ObjectMapper mapper,
            ScheduledTaskRepository taskRepository,
            ScheduledTaskExecutionLogRepository logRepository,
//            TaskSchedulerService taskSchedulerService,
            ScheduledTaskMapper scheduledTaskMapper
    ) {
        super(mapper, taskRepository, logRepository);
//        this.taskSchedulerService = taskSchedulerService;
        this.scheduledTaskMapper = scheduledTaskMapper;
    }

    @Override
    public String getTaskType() {
        return "TaskMissedStatus";
    }

    @Override
    public JsonNode performTask(JsonNode arguments) throws Exception {
        ObjectNode result = createObjectNode();
        result.set("success", arguments);
        return result;
    }

//    @Transactional
//    public void createTask(@Valid CreateScheduledTaskRequest scheduledTaskRequest) {
//        ScheduledTaskEntity scheduledTask = scheduledTaskMapper.createRequestToEntity(scheduledTaskRequest);
//        scheduledTask = taskRepository.save(scheduledTask);
//        taskSchedulerService.scheduleTask(scheduledTask);
//    }
}
