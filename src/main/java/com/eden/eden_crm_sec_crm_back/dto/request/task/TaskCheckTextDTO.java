package com.eden.eden_crm_sec_crm_back.dto.request.task;

import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.TaskCheck;
import com.eden.eden_crm_sec_crm_back.models.TaskCheckText;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskCheckPatrolExecution;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskCheckPatrolExecutionText;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskPatrolExecution;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskCheckTextDTO extends TaskCheckDTO {
    @NotNull(message = "{validation.distribution.task-check.notes}")
    private String notes;

    @Override
    public TaskCheck mapToEntity(Task task) {
        TaskCheckText taskCheckText = new TaskCheckText();
        taskCheckText.setNotes(notes);
        taskCheckText.setEvidence(getEvidence());
        taskCheckText.setName(getName());
        taskCheckText.setTask(task);
        return taskCheckText;
    }

    @Override
    public TaskCheckPatrolExecution mapToExecutionEntity(TaskPatrolExecution taskPatrolExecution) {
        TaskCheckPatrolExecutionText taskCheckText = new TaskCheckPatrolExecutionText();
        taskCheckText.setId(getId());
        taskCheckText.setNotes(notes);
        taskCheckText.setEvidence(getEvidence());
        if (getEvidence()) {
            taskCheckText.setImage(getImageBase64());
        }
        taskCheckText.setName(getName());
        taskCheckText.setTaskPatrolExecution(taskPatrolExecution);
        return taskCheckText;
    }
}
