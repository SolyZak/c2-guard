package com.eden.eden_crm_sec_crm_back.dto.request.task;

import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.TaskCheck;
import com.eden.eden_crm_sec_crm_back.models.TaskCheckText;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskCheckTextDTO extends TaskCheckDTO {
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
}
