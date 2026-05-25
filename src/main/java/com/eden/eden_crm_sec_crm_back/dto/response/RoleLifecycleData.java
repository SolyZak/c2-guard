package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleLifecycleData {
    private Long id;
    private Integer roleId;
    private String roleName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
