package com.eden.eden_crm_sec_crm_back.clients;

import com.eden.eden_crm_sec_crm_back.clients.dto.UploadImageRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "${feign.clients.documents}", path = "/documents")
public interface DocumentsFeignClient {
    @PostMapping(value = "/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void uploadImage(@Valid @ModelAttribute UploadImageRequest uploadImageRequest);
}