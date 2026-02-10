package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PremiseRequestDto {
    @NotNull(message = "{validation.premise.name.required}")
    @Size(max = 300, message = "{validation.premise.name.max.length}")
    private String name;

    @NotNull(message = "{validation.premise.code.required}")
    @Size(max = 300, message = "{validation.premise.code.max.length}")
    private String code;

    private BigDecimal longitude;
    private BigDecimal latitude;

}
