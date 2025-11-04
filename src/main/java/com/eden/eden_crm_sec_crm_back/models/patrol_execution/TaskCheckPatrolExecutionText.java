package com.eden.eden_crm_sec_crm_back.models.patrol_execution;

import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckTextDTO;
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
@Table(name = "task_check_text_patrol_execution")
@DiscriminatorValue("text")
public class TaskCheckPatrolExecutionText extends TaskCheckPatrolExecution {
    private String notes;

    @Override
    public TaskCheckTextDTO mapToResponse() {
        TaskCheckTextDTO dto = new TaskCheckTextDTO();
        dto.setId(getId());
        dto.setEvidence(getEvidence());
        dto.setName(getName());
        dto.setNotes(notes);
        return dto;
    }
}
