package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.PremiseRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.PremiseUpdateRequestDto;
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
        description = "This part is for customer portal, will provide the needed for premise API"
)
public class PremisesController {

    private final PremiseServiceImpl premiseService;

    @PostMapping
    @Operation(summary = "Create premise (includes longitude/latitude)")
    ApiResponse<PremiseResponseDto> addPremise(@RequestBody @Valid PremiseRequestDto requestDto) {
        return ApiResponse.ok(premiseService.addPremise(requestDto));
    }

    @GetMapping
    @Operation(summary = "Get all premises")
    ApiResponse<List<PremiseResponseDto>> getAllPremises() {
        return ApiResponse.ok(premiseService.getPremises());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Read premise (returns longitude/latitude)")
    ApiResponse<PremiseResponseDto> getPremise(@PathVariable("id") Long id) {
        return ApiResponse.ok(premiseService.getPremiseById(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update premise (Code, Name, longitude, latitude)")
    ApiResponse<PremiseResponseDto> updatePremise(@PathVariable("id") Long id,
                                                  @RequestBody @Valid PremiseUpdateRequestDto requestDto) {
        return ApiResponse.ok(premiseService.updatePremisePartial(id, requestDto));
    }
}