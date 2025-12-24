package com.eden.eden_crm_sec_crm_back.dto.request.task;

import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.TaskCheck;
import com.eden.eden_crm_sec_crm_back.models.TaskCheckList;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskCheckPatrolExecution;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskCheckPatrolExecutionList;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskPatrolExecution;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskCheckListDTO extends TaskCheckDTO {
    @NotNull(message = "{validation.distribution.task-check.list-items}")
    private List<String> listItems;

    @Override
    public TaskCheck mapToEntity(Task task) {
        TaskCheckList entity = new TaskCheckList();
        entity.setListItems(listItems);
        entity.setName(getName());
        entity.setEvidence(getEvidence());
        entity.setTask(task);
        entity.setCommentCheck(getCommentCheck());
        entity.setComment(getComment());
        return entity;
    }

    @Override
    public TaskCheckPatrolExecution mapToExecutionEntity(TaskPatrolExecution taskPatrolExecution) {
        TaskCheckPatrolExecutionList entity = new TaskCheckPatrolExecutionList();
        entity.setId(getId());
        entity.setListItems(listItems);
        entity.setName(getName());
        entity.setEvidence(getEvidence());
        if (getEvidence()) {
            entity.setImage(getImageBase64());
        }
        entity.setTaskPatrolExecution(taskPatrolExecution);
        return entity;
    }
}