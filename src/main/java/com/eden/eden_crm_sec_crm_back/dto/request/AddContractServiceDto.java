package com.eden.eden_crm_sec_crm_back.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddContractServiceDto {
    @NotNull(message = "{validation.contract.services.serviceDetailsId.required}")
    @Min(value = 1, message = "{validation.contract.services.serviceDetailsId.min-1}")
    private Long serviceDetailsId;

    @NotNull(message = "{validation.contract.services.quantity.required}")
    @Min(value = 1, message = "{validation.contract.services.quantity.min-1}")
    @Max(value = 10000, message = "{validation.contract.services.quantity.max-10000}")
    private Long quantity;

    @NotNull(message = "{validation.contract.services.unitPrice.required}")
    @Min(value = 1, message = "{validation.contract.services.unitPrice.min-1}")
    @Max(value = 1000000, message = "{validation.contract.services.unitPrice.max-1000000}")
    private Double unitPrice;

    // ALTER TABLE public.customer_contract ALTER COLUMN currency TYPE int4 USING currency::int4;
    // ALTER TABLE public.customer_contract DROP CONSTRAINT customer_contract_currency_check;
}
