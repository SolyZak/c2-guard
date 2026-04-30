package com.eden.eden_crm_sec_crm_back.clients.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PcdAiVerifyResponse {
    private Boolean sameObject;
    private Boolean isBlurry;
    private Boolean isBright;
    private Boolean isAligned;
}