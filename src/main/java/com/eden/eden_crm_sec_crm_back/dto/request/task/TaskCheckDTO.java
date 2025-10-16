package com.eden.eden_crm_sec_crm_back.dto.request.task;

import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.TaskCheck;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
    private String name;
    private Boolean evidence;

    public abstract TaskCheck mapToEntity(Task task);
}

