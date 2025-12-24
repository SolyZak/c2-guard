package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckTextDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_check_text")
@DiscriminatorValue("text")
public class TaskCheckText extends TaskCheck {
    private String notes;

    @Override
    public TaskCheckTextDTO mapToResponse() {
        TaskCheckTextDTO dto = new TaskCheckTextDTO();
        dto.setId(getId());
        dto.setEvidence(getEvidence());
        dto.setName(getName());
        dto.setNotes(notes);
        dto.setCommentCheck(getCommentCheck());
        dto.setComment(getComment());
        return dto;
    }
}
