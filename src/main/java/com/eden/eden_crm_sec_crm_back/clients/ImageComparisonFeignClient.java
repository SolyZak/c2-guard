package com.eden.eden_crm_sec_crm_back.clients;

import com.eden.eden_crm_sec_crm_back.clients.dto.ImageComparisonRequest;
import com.eden.eden_crm_sec_crm_back.clients.dto.ImageComparisonResponse;
import com.eden.eden_crm_sec_crm_back.clients.dto.MatchingFeedbackRequest;
import com.eden.eden_crm_sec_crm_back.clients.dto.ReferenceImageUploadedRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "image-comparison-service",
        url = "${feign.clients.image-comparison.url}"
)
public interface ImageComparisonFeignClient {

    @PostMapping("/api/compare")
    ImageComparisonResponse requestComparison(@RequestBody ImageComparisonRequest request);

    @PostMapping("/api/matching-feedback")
    void sendMatchingFeedback(@RequestBody MatchingFeedbackRequest request);

    @PostMapping("/api/reference-image")
    void notifyReferenceImageUploaded(@RequestBody ReferenceImageUploadedRequest request);
}