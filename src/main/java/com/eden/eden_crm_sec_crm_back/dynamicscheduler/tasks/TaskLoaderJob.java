package com.eden.eden_crm_sec_crm_back.dynamicscheduler.tasks;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.entities.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.factories.CronScheduledTaskFactory;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.mappers.ScheduledTaskMapper;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.operators.TaskSchedulerOperator;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.utils.JsonNodeUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Validator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskLoaderJob extends CronScheduledTaskFactory {

    public static final String TASK_TYPE = "Tasks24HoursLoader";

    public TaskLoaderJob(
        ApplicationContext applicationContext,
        ObjectMapper objectMapper,
        ScheduledTaskRepository taskRepository,
        ScheduledTaskExecutionLogRepository logRepository,
        ScheduledTaskMapper scheduledTaskMapper,
        Validator validator
    ) {
        super(applicationContext, objectMapper, taskRepository, logRepository, scheduledTaskMapper, validator);
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }

    @Override
    public JsonNode performTask(JsonNode arguments) {
        TaskSchedulerOperator taskSchedulerOperator = applicationContext.getBean(TaskSchedulerOperator.class);
        List<ScheduledTaskEntity> entityList = taskSchedulerOperator.getAllTodayTasksExcludeLoaders(
            List.of(getTaskType())
        );
        entityList.forEach(taskSchedulerOperator::scheduleTask);
        ObjectNode result = JsonNodeUtils.createObjectNode();
        result.put("status", "success");
        result.put("numberOfScheduledTasks", entityList.size());
        return result;
    }
}
