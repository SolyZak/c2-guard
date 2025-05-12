package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerPaginateDto {
    // Page is optional, default value will be 1 if not provided
    @Min(value = 0, message = "{validation.page.minimum.zero}")
    @Nullable
    private Integer page = 0;

    // Limit is optional, default value will be 10 if not provided
    @Min(value = 1, message = "{validation.size.minimum.one}")
    @Max(value = 100, message = "validation.size.maximum.hundred")
    @Nullable
    private Integer size = 10;

    // Search is optional, no validation needed as it's not required
    @Nullable
    private String search;
}
