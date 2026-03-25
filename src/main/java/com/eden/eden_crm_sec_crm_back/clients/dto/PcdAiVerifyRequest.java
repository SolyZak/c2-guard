package com.eden.eden_crm_sec_crm_back.clients.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PcdAiVerifyRequest {
    private String imageUrl1;
    private String imageBase64_2;
}