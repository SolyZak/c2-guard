package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BulkLocationCameraRequestDto(

        @NotNull(message = "{validation.location.id.not.null}")
        Long locationId,

        @NotEmpty(message = "{validation.camera.ids.not.empty}")
        List<@NotNull(message = "{validation.camera.id.not.null}") Long> cameraIds
) {}