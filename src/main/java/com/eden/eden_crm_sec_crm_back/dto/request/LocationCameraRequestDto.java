package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotNull;

public record LocationCameraRequestDto(

        @NotNull(message = "{validation.location.id.not.null}")
        Long locationId,

        @NotNull(message = "{validation.camera.id.not.null}")
        Long cameraId
) {}