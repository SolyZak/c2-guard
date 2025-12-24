package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckNumberDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_check_number")
@DiscriminatorValue("number")
public class TaskCheckNumber extends TaskCheck {
    private String unit;
    private String operator;
    private Integer value;

    @Override
    public TaskCheckNumberDTO mapToResponse() {
        TaskCheckNumberDTO dto = new TaskCheckNumberDTO();
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
