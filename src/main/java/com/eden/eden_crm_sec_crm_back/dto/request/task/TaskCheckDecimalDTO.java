package com.eden.eden_crm_sec_crm_back.dto.request.task;

import com.eden.eden_crm_sec_crm_back.dto.request.markers.OnAddTask;
import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.TaskCheck;
import com.eden.eden_crm_sec_crm_back.models.TaskCheckDecimal;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskCheckPatrolExecution;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskCheckPatrolExecutionDecimal;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskPatrolExecution;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCheckDecimalDTO extends TaskCheckDTO {
    @NotNull(message = "{validation.distribution.task-check.unit}")
    private String unit;
    @NotNull(groups = OnAddTask.class, message = "{validation.distribution.task-check.operator}")
    private String operator;
    @NotNull(message = "{validation.distribution.task-check.value}")
    private Double value;

    @Override
    public TaskCheck mapToEntity(Task task) {
        TaskCheckDecimal entity = new TaskCheckDecimal();
        entity.setOperator(operator);
        entity.setUnit(unit);
        entity.setValue(value);
        entity.setName(getName());
        entity.setEvidence(getEvidence());
        entity.setTask(task);
        entity.setCommentCheck(getCommentCheck());
        entity.setComment(getComment());
        return entity;
    }

    @Override
    public TaskCheckPatrolExecution mapToExecutionEntity(TaskPatrolExecution taskPatrolExecution) {
        TaskCheckPatrolExecutionDecimal entity = new TaskCheckPatrolExecutionDecimal();
        entity.setId(getId());
        entity.setUnit(unit);
        entity.setValue(value);
        entity.setName(getName());
        entity.setEvidence(getEvidence());
        if (getEvidence()) {
            entity.setImage(getImageBase64());
        }
        entity.setTaskPatrolExecution(taskPatrolExecution);
        return entity;
    }
}

