package com.eden.eden_crm_sec_crm_back.dynamicscheduler.tasks;

import com.eden.eden_crm_sec_crm_back.base.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.factories.DateTimeScheduledTaskFactory;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.mappers.ScheduledTaskMapper;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskExecutionLogRepository;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.repositories.ScheduledTaskRepository;
import com.eden.eden_crm_sec_crm_back.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.models.ContractOperationSiteDistributionPatrol;
import com.eden.eden_crm_sec_crm_back.repository.ContractOperationSiteDistributionPatrolRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Validator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskCurrentStatusJob extends DateTimeScheduledTaskFactory {

    public static final String TASK_TYPE = "TaskCurrentStatus";
    private final ContractOperationSiteDistributionPatrolRepository repository;

    public TaskCurrentStatusJob(
        ApplicationContext applicationContext,
        ObjectMapper objectMapper,
        ScheduledTaskRepository taskRepository,
        ScheduledTaskExecutionLogRepository logRepository,
        ScheduledTaskMapper scheduledTaskMapper,
        Validator validator,
        ContractOperationSiteDistributionPatrolRepository repository
    ) {
        super(applicationContext, objectMapper, taskRepository, logRepository, scheduledTaskMapper, validator);
        this.repository = repository;
    }

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }

    @Override
    public JsonNode performTask(JsonNode arguments) {
        Long patrolDistributionId = arguments.get("patrolDistributionId").asLong();

        Optional<ContractOperationSiteDistributionPatrol> optionalDistribution = repository.findById(patrolDistributionId);
        if (optionalDistribution.isEmpty())
            throw new BusinessException("Patrol distribution not found", HttpStatus.NOT_FOUND);

        ContractOperationSiteDistributionPatrol distribution = optionalDistribution.get();
        distribution.setStatus(TaskDistributionStatus.CURRENT.name());
        repository.save(distribution);

        ObjectNode result = createObjectNode();
        result.put("status", "success");
        result.set("usedIds", createObjectNode().put("patrolDistributionId", patrolDistributionId));
        return result;
    }
}
