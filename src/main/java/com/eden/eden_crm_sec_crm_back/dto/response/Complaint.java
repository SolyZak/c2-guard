package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Complaint {
    private Integer id;
    private String operationSiteName;
    private Long customerSiteId;
    private String customerName;
    private LocalDateTime creationDate;
    private Long orgUnitId;
    private Long managerId;
}
