package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.ContractOperationRuleDTO;
import com.eden.eden_crm_sec_crm_back.dto.response.ContractWithRules;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.service.ContractOperationRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "/customers/contract-operation-rules")
@RequiredArgsConstructor
@Tag(
        name = "Contract Operation Rules API",
        description = "This part is for customer portal, will provide the needed for operation sites API")
public class ContractOperationRuleController {
    private final ContractOperationRuleService  contractOperationRuleService;

    @PutMapping("/set-rules")
    public ApiResponse<String> changeContractRule(@RequestBody @Valid ContractOperationRuleDTO dto) {
        dto.validate();
        return ApiResponse.ok(contractOperationRuleService.changeContractRule(dto));
    }

    @Operation(summary = "Get Contract Dropdown with rules")
    @GetMapping("/contracts/dropdown")
    public ApiResponse<List<ContractWithRules>> contractListWithRules() {
        return ApiResponse.ok(contractOperationRuleService.contractListWithRules());
    }
}
