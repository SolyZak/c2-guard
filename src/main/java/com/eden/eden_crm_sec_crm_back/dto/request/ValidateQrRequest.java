package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateQrRequest {

    @NotBlank
    private String payload;   // raw text from the QR code
}