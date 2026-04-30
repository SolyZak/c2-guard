package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.rest;

import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.ImageVerifyRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.ImageVerifyResponse;
import com.eden.eden_crm_sec_crm_back.task_management.application.service.ImageVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pcd-ai")
@RequiredArgsConstructor
public class ImageVerificationController {

    private final ImageVerificationService imageVerificationService;

    @PostMapping("/verify")
    public ApiResponse<ImageVerifyResponse> verify(@Valid @RequestBody ImageVerifyRequest request) {
        ImageVerifyResponse response = imageVerificationService.verify(request);
        return ApiResponse.ok(response);
    }
}