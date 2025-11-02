package com.eden.eden_crm_sec_crm_back.dto.request.task;

import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.TaskCheck;
import com.eden.eden_crm_sec_crm_back.models.TaskCheckList;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskCheckListDTO extends TaskCheckDTO {
    private List<String> listItems;

    @Override
    public TaskCheck mapToEntity(Task task) {
        TaskCheckList entity = new TaskCheckList();
        entity.setListItems(listItems);
        entity.setName(getName());
        entity.setEvidence(getEvidence());
        entity.setTask(task);
        return entity;
    }
}