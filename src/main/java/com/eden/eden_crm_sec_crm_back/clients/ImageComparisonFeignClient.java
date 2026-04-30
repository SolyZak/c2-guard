package com.eden.eden_crm_sec_crm_back.clients;

import com.eden.eden_crm_sec_crm_back.clients.dto.ImageComparisonRequest;
import com.eden.eden_crm_sec_crm_back.clients.dto.ImageComparisonResponse;
import com.eden.eden_crm_sec_crm_back.clients.dto.MatchingFeedbackRequest;
import com.eden.eden_crm_sec_crm_back.clients.dto.PcdAiVerifyRequest;
import com.eden.eden_crm_sec_crm_back.clients.dto.PcdAiVerifyResponse;
import com.eden.eden_crm_sec_crm_back.clients.dto.ReferenceImageUploadedRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "pcd-ai-service"
)
public interface ImageComparisonFeignClient {

    @PostMapping("/pcd-ai/compare")
    ImageComparisonResponse requestComparison(@RequestBody ImageComparisonRequest request);

    @PostMapping("/pcd-ai/feedback")
    void sendMatchingFeedback(@RequestBody MatchingFeedbackRequest request);

    @PostMapping("/pcd-ai/baseline")
    void notifyReferenceImageUploaded(@RequestBody ReferenceImageUploadedRequest request);

    @PostMapping("/pcd-ai/verify")
    PcdAiVerifyResponse verify(@RequestBody PcdAiVerifyRequest request);
}