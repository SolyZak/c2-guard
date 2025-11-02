package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.PremiseRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.PremiseResponseDto;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.service.impl.PremiseServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/premise")
@RequiredArgsConstructor
@Tag(
        name = "Premises API",
        description = "This part is for customer portal, will provide the needed for premise API")
public class PremisesController {

    private final PremiseServiceImpl premiseService;

    @PostMapping
    @Operation(summary = "Create premise")
    ApiResponse<PremiseResponseDto> addPremise(@RequestBody @Valid PremiseRequestDto requestDto) {
        return ApiResponse.ok(premiseService.addPremise(requestDto));
    }

    @Operation(summary = "Get all premises")
    @GetMapping
    ApiResponse<List<PremiseResponseDto>> getAllPremises() {
        return ApiResponse.ok(premiseService.getPremises());
    }
}
