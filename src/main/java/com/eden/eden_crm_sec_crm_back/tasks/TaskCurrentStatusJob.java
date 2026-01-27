package com.eden.eden_crm_sec_crm_back.tasks;

import com.eden.eden_crm_sec_crm_back.base.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.models.ContractOperationSiteDistributionPatrol;
import com.eden.eden_crm_sec_crm_back.repository.ContractOperationSiteDistributionPatrolRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github._0xorigin.flexscheduler.base.factories.tasks.base.ScheduledTaskFactory;
import io.github._0xorigin.flexscheduler.utils.JsonNodeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskCurrentStatusJob implements ScheduledTaskFactory {

    public static final String TASK_TYPE = "TaskCurrentStatus";
    private final ContractOperationSiteDistributionPatrolRepository repository;

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }

    @Override
    public JsonNode performTask(JsonNode arguments) {
        Long patrolDistributionId = JsonNodeUtils.getOptionalLong(arguments, "patrolDistributionId")
                .orElseThrow(() -> new BusinessException("Patrol distribution id is required", HttpStatus.BAD_REQUEST));

        Optional<ContractOperationSiteDistributionPatrol> optionalDistribution = repository.findById(patrolDistributionId);
        if (optionalDistribution.isEmpty())
            throw new BusinessException("Patrol distribution not found", HttpStatus.NOT_FOUND);

        ContractOperationSiteDistributionPatrol distribution = optionalDistribution.get();
        if (!distribution.getStatus().equals(TaskDistributionStatus.MISSED.name())) {
            distribution.setStatus(TaskDistributionStatus.CURRENT.name());
            repository.saveAndFlush(distribution);
        }

        ObjectNode result = JsonNodeUtils.createObjectNode();
        result.put("status", "success");
        result.set("usedIds", JsonNodeUtils.createObjectNode().put("patrolDistributionId", patrolDistributionId));
        return result;
    }
}
