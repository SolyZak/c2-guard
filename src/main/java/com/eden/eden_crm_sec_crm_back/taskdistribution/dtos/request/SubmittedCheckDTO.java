package com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SubmittedDecimalCheckDTO.class, name = "decimal"),
        @JsonSubTypes.Type(value = SubmittedNumberCheckDTO.class, name = "number"),
        @JsonSubTypes.Type(value = SubmittedTextCheckDTO.class, name = "text"),
        @JsonSubTypes.Type(value = SubmittedListCheckDTO.class, name = "list")
})
public class SubmittedCheckDTO {

    private Long id;

    @NotNull(message = "{validation.distribution.task-check.name}")
    private String name;

    @NotNull(message = "{validation.distribution.task-check.evidence}")
    private Boolean evidence;

    @NotNull(message = "commentCheck is required")
    private Boolean commentCheck;

    @Size(max = 500)
    private String comment;

    private String imageBase64;

    private String referenceImageUrl;

    private List<String> missingQuality;
}