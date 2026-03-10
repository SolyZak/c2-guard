package com.eden.eden_crm_sec_crm_back.clients;

import com.eden.eden_crm_sec_crm_back.clients.dto.ImageComparisonRequest;
import com.eden.eden_crm_sec_crm_back.clients.dto.ImageComparisonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "${feign.clients.image-comparison}"
)
public interface ImageComparisonFeignClient {

    @PostMapping("/api/compare")
    ImageComparisonResponse requestComparison(@RequestBody ImageComparisonRequest request);
}