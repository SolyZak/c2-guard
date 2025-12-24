package com.eden.eden_crm_sec_crm_back.dto.request.task;

import com.eden.eden_crm_sec_crm_back.dto.request.markers.OnAddTask;
import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.TaskCheck;
import com.eden.eden_crm_sec_crm_back.models.TaskCheckNumber;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskCheckPatrolExecution;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskCheckPatrolExecutionNumber;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskPatrolExecution;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCheckNumberDTO extends TaskCheckDTO {
    @NotNull(message = "{validation.distribution.task-check.unit}")
    private String unit;
    @NotNull(groups = OnAddTask.class, message = "{validation.distribution.task-check.operator}")
    private String operator;
    @NotNull(message = "{validation.distribution.task-check.value}")
    private Integer value;

    @Override
    public TaskCheck mapToEntity(Task task) {
        TaskCheckNumber entity = new TaskCheckNumber();

        entity.setOperator(operator);
        entity.setUnit(unit);
        entity.setValue(value);
        entity.setName(getName());
        entity.setEvidence(getEvidence());
        entity.setTask(task);
        entity.setCommentCheck((getCommentCheck()));
        entity.setComment(getComment());
        return entity;
    }

    @Override
    public TaskCheckPatrolExecution mapToExecutionEntity(TaskPatrolExecution taskPatrolExecution) {
        TaskCheckPatrolExecutionNumber entity = new TaskCheckPatrolExecutionNumber();
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