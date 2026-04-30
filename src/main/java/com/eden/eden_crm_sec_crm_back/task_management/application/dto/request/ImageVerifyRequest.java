package com.eden.eden_crm_sec_crm_back.task_management.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ImageVerifyRequest {

    @NotBlank(message = "imageUrl1 is required")
    private String imageUrl1;

    @NotBlank(message = "imageBase64_2 is required")
    private String imageBase64_2;
}