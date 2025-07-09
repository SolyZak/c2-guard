package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class AddComplaintRequest {
    @NotNull(message = "{validation.complaint.contractId.required}")
    private Long contractId;

    @NotNull(message = "{validation.complaint.operationSiteId.required}")
    private Long operationSiteId;

    @NotNull(message = "{validation.complaint.description.required}")
    @Size(max = 500, message = "{validation.complaint.description.max.length}")
    private String description;

    private List<MultipartFile> images;
}
