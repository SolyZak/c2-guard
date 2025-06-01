package com.eden.eden_crm_sec_crm_back.dto.external;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerInfo {
    private Long id;
    private String name;
    private String code;
}
