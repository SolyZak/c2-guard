package com.eden.eden_crm_sec_crm_back.dto.request.task;

import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.TaskCheck;
import com.eden.eden_crm_sec_crm_back.models.TaskCheckDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCheckDecimalDTO extends TaskCheckDTO {
    private String unit;
    private String operator;
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
        return entity;
    }
}

