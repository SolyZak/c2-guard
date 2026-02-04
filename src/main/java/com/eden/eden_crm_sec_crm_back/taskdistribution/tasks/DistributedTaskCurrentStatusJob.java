package com.eden.eden_crm_sec_crm_back.taskdistribution.tasks;

import com.eden.eden_crm_sec_crm_back.base.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskExecutionSlot;
import com.eden.eden_crm_sec_crm_back.taskdistribution.enums.TaskDistributionStatus;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.TaskExecutionSlotRepository;
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
public class DistributedTaskCurrentStatusJob implements ScheduledTaskFactory {

    public static final String TASK_TYPE = "DistributedTaskCurrentStatus";
    private final TaskExecutionSlotRepository repository;

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }

    @Override
    public JsonNode performTask(JsonNode arguments) {
        Long executionSlotId = JsonNodeUtils.getOptionalLong(arguments, "executionSlotId")
                .orElseThrow(() -> new BusinessException("Execution slot id is required", HttpStatus.BAD_REQUEST));

        Optional<TaskExecutionSlot> executionSlotOptional = repository.findById(executionSlotId);
        if (executionSlotOptional.isEmpty())
            throw new BusinessException("Execution slot not found", HttpStatus.NOT_FOUND);

        TaskExecutionSlot executionSlot = executionSlotOptional.get();
        if (executionSlot.getStatus() != TaskDistributionStatus.MISSED) {
            executionSlot.setStatus(TaskDistributionStatus.CURRENT);
            repository.saveAndFlush(executionSlot);
        }

        ObjectNode result = JsonNodeUtils.createObjectNode();
        result.put("status", "success");
        return result;
    }
}
