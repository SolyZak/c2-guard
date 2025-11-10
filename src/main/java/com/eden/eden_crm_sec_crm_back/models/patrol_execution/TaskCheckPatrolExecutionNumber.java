package com.eden.eden_crm_sec_crm_back.models.patrol_execution;

import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckNumberDTO;
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
@Table(name = "task_check_number_patrol_execution")
@DiscriminatorValue("number")
public class TaskCheckPatrolExecutionNumber extends TaskCheckPatrolExecution {
    private String unit;
    private Integer value;

    @Override
    public TaskCheckNumberDTO mapToResponse() {
        TaskCheckNumberDTO dto = new TaskCheckNumberDTO();
        dto.setId(getId());
        dto.setName(getName());
        dto.setEvidence(getEvidence());
        dto.setUnit(unit);
        dto.setValue(value);
        return dto;
    }
}
