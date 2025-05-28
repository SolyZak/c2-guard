package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddServiceDetailsDto {
    @NotNull(message = "{validation.service.details.hours.required}")
    @Min(value = 1, message = "{validation.service.details.hours.min-1}")
    @Max(value = 24, message = "{validation.service.details.hours.max-24}")
    private Long hours;

    @NotNull(message = "{validation.service.details.days.required}")
    @Min(value = 1, message = "{validation.service.details.days.min-1}")
    @Max(value = 7, message = "{validation.service.details.days.max-7}")
    private Long days;
}
