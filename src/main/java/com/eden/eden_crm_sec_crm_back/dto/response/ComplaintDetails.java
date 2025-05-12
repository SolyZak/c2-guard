package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ComplaintDetails {
    private Integer id;
    private String description;
    private LocalDateTime creationDate;
    private List<String> evidencesPaths;
}
