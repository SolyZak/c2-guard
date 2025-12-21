package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddLocationRequest {
    @NotNull(message = "{validation.location.premise.required}")
    Long premiseId;
    @NotNull(message = "{validation.location.locations.required}")
    List<LocationRequestDto> locations;
}
