package com.eden.eden_crm_sec_crm_back.task_management.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadTaskCheckImageRequest {

    @NotNull
    private MultipartFile image;
}