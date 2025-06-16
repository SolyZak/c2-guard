package com.eden.eden_crm_sec_crm_back.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContractPlannedQntDto {
    private Long id;
    private String name;
    private Long plannedQnt;
}
