package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotNull;

public record OperationSiteCameraRequestDto(
        @NotNull(message = "{validation.camera.id.not.null}")
        Long cameraId,

        @NotNull(message = "{validation.operation.site.id.not.null}")
        Long operationSiteId
) {}