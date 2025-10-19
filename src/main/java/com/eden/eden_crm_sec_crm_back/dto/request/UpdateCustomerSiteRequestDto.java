package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCustomerSiteRequestDto {
    @NotNull(message = "{validation.operation.site.name.required}")
    @Size(max = 300, message = "{validation.operation.site.name.max.length}")
    private String name;

    @NotNull(message = "{validation.point.latitude.longitude.required}")
    private Double latitude;

    @NotNull(message = "{validation.point.latitude.longitude.required}")
    private Double longitude;

    @NotNull(message = "{validation.point.tolerance.required}")
    private Double tolerance;

    @NotNull(message = "{validation.point.tolerance.required}")
    private Long premiseId;
}
