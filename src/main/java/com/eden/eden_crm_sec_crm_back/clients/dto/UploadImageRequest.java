package com.eden.eden_crm_sec_crm_back.clients.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UploadImageRequest {
    MultipartFile image;
    String path;
}
