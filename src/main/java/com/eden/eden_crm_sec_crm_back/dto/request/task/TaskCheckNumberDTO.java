package com.eden.eden_crm_sec_crm_back.dto.request.task;

import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.TaskCheck;
import com.eden.eden_crm_sec_crm_back.models.TaskCheckNumber;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCheckNumberDTO extends TaskCheckDTO {
    private String unit;
    private String operator;
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
        return entity;
    }
}