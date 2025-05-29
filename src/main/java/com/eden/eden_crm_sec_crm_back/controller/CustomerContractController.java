package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.base.util.ApiResponse;
import com.eden.eden_crm_sec_crm_back.dto.request.AddContractDto;
import com.eden.eden_crm_sec_crm_back.service.CustomerContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/customers/contracts")
@RequiredArgsConstructor
@Tag(
        name = "Customer Contract API",
        description = "This part is for customer portal, will provide the needed for contracts API")
public class CustomerContractController {
    private final CustomerContractService customerContractService;

    @Operation(summary = "Create Contract API")
    @PostMapping
    public ApiResponse<String> createAgreement(@RequestBody @Valid AddContractDto dto) {
        return ApiResponse.ok(customerContractService.createAgreement(dto));
    }
}
