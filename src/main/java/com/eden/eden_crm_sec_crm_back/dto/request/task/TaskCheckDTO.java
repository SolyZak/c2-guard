package com.eden.eden_crm_sec_crm_back.dto.request.task;

import com.eden.eden_crm_sec_crm_back.dto.request.markers.OnAddTask;
import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.TaskCheck;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskCheckPatrolExecution;
import com.eden.eden_crm_sec_crm_back.models.patrol_execution.TaskPatrolExecution;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TaskCheckDecimalDTO.class, name = "decimal"),
        @JsonSubTypes.Type(value = TaskCheckNumberDTO.class, name = "number"),
        @JsonSubTypes.Type(value = TaskCheckTextDTO.class, name = "text"),
        @JsonSubTypes.Type(value = TaskCheckListDTO.class, name = "list")
})
public abstract class TaskCheckDTO {


    private Long id;
    @NotNull(message = "{validation.distribution.task-check.name}")
    private String name;
    @NotNull(message = "{validation.distribution.task-check.evidence}")
    private Boolean evidence;

    @NotNull(message = "commentCheck is required")
    private Boolean commentCheck;

    @Size(max = 500)
    private String comment;

    // ✅ Base64-encoded image string (optional)
    private String imageBase64;


    public abstract TaskCheck mapToEntity(Task task);

    public abstract TaskCheckPatrolExecution mapToExecutionEntity(TaskPatrolExecution taskPatrolExecution);
}

