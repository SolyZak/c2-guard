package com.eden.eden_crm_sec_crm_back.dynamicscheduler.task;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.entity.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.interfaces.AbstractScheduledTaskFactory;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.operator.TaskSchedulerOperator;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.repository.ScheduledTaskRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskLoaderJob extends AbstractScheduledTaskFactory {

    public TaskLoaderJob(
        ApplicationContext applicationContext,
        ObjectMapper objectMapper,
        ScheduledTaskRepository taskRepository,
        ScheduledTaskExecutionLogRepository logRepository
    ) {
        super(applicationContext, objectMapper, taskRepository, logRepository);
    }

    @Override
    public String getTaskType() {
        return "Tasks24HoursLoader";
    }

    @Override
    public JsonNode performTask(JsonNode arguments) {
        TaskSchedulerOperator taskSchedulerOperator = applicationContext.getBean(TaskSchedulerOperator.class);
        List<ScheduledTaskEntity> entityList = taskSchedulerOperator.getAllTodayTasksExcludeLoaders(
            List.of(getTaskType())
        );
        entityList.forEach(taskSchedulerOperator::scheduleTask);
        ObjectNode result = createObjectNode();
        result.put("status", "success");
        result.put("numberOfScheduledTasks", entityList.size());
        return result;
    }
}
