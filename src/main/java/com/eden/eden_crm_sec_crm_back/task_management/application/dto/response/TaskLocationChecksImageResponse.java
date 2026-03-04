package com.eden.eden_crm_sec_crm_back.task_management.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskLocationChecksImageResponse {

    private Long id;
    private Long taskDefinitionId;
    private Long locationId;
    private Long taskCheckDefinitionId;
    private Long customerId;
    private String refImage;
    private Long createdBy;
    private Long modifiedBy;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}