package com.eden.eden_crm_sec_crm_back.models.patrol_execution;

import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckDecimalDTO;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_check_decimal_patrol_execution")
@DiscriminatorValue("decimal")
public class TaskCheckPatrolExecutionDecimal extends TaskCheckPatrolExecution {
    private String unit;
    private Double value;

    @Override
    public TaskCheckDecimalDTO mapToResponse() {
        TaskCheckDecimalDTO dto = new TaskCheckDecimalDTO();
        dto.setId(getId());
        dto.setName(getName());
        dto.setEvidence(getEvidence());
        dto.setUnit(unit);
        dto.setValue(value);
        return dto;
    }
}
