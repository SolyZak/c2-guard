package com.eden.eden_crm_sec_crm_back.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class AddComplaintRequest {
    private Long customerId;
    private Long operationSiteId;
    private String description;
    private List<MultipartFile> images;
}
