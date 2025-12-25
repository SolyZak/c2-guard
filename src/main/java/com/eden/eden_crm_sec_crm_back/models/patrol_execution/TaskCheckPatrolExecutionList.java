package com.eden.eden_crm_sec_crm_back.models.patrol_execution;

import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckListDTO;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_check_list_patrol_execution")
@DiscriminatorValue("list")
public class TaskCheckPatrolExecutionList extends TaskCheckPatrolExecution {
    private List<String> listItems;

    @Override
    public TaskCheckListDTO mapToResponse() {
        TaskCheckListDTO dto = new TaskCheckListDTO();
        dto.setId(getId());
        dto.setListItems(listItems);
        dto.setName(getName());
        dto.setEvidence(getEvidence());
        dto.setCommentCheck(getCommentCheck());
        dto.setComment(getComment());
        return dto;
    }
}
