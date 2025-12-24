package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckDecimalDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_check_decimal")
@DiscriminatorValue("decimal")
public class TaskCheckDecimal extends TaskCheck {
    private String unit;
    private String operator;
    private Double value;

    @Override
    public TaskCheckDecimalDTO mapToResponse() {
        TaskCheckDecimalDTO dto = new TaskCheckDecimalDTO();
        dto.setId(getId());
        dto.setName(getName());
        dto.setEvidence(getEvidence());
        dto.setUnit(unit);
        dto.setOperator(operator);
        dto.setValue(value);
        dto.setCommentCheck(getCommentCheck());
        dto.setComment(getComment());
        return dto;
    }
}
