package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateQrResponse {
    private boolean success;
    private String message;
    private Long locationId;
    private String locationName;
}
