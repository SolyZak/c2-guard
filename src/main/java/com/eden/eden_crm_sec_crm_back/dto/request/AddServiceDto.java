package com.eden.eden_crm_sec_crm_back.dto.request;

import com.eden.eden_crm_sec_crm_back.enums.ActivityEnum;
import com.eden.eden_crm_sec_crm_back.enums.UnitEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddServiceDto {
    @NotNull(message = "validation.service.name.required")
    private String name;

    @NotNull(message = "validation.service.unit.required")
    private UnitEnum unit;

    @NotNull(message = "validation.service.activities.required")
    private List<ActivityEnum> activities;

    private Boolean multiSite = false;

    @NotNull(message = "{validation.service.details.required}")
    @NotEmpty(message = "{validation.service.details.required}")
    @Valid private List<AddServiceDetailsDto> details;
}
