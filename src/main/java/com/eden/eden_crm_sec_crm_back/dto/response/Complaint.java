package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Complaint {
    private Long id;
    private Long operationSiteId;
    private String operationSiteName;
    private Long contractId;
    private String contractName;
    private LocalDateTime creationDate;
    private String description;
    private List<String> images;
}
