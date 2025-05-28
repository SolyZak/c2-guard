package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.AddServiceDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ServiceDataDto;
import com.eden.eden_crm_sec_crm_back.dto.response.ServiceDetailsDropdownDto;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.service.CustomerServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/customers/service")
@RequiredArgsConstructor
@Tag(
        name = "Services API",
        description = "This part is for customer portal, will provide the needed for services API")
public class CustomerServiceController {
    private final CustomerServiceService customerServiceService;

    @GetMapping
    @Operation(summary = "Paginate logged in customer services")
    public ApiResponse<PaginateResponse<ServiceDataDto>> paginateMyServices(
            @RequestParam(defaultValue = "0", name = "page") Integer page,
            @RequestParam(defaultValue = "10", name = "size") Integer size
    ) {
        return ApiResponse.ok(customerServiceService.paginateMyServices(page, size));
    }

    @GetMapping("/details/dropdown")
    @Operation(summary = "Get service details dropdown, service concatenated with hours & days numbers")
    public ApiResponse<List<ServiceDetailsDropdownDto>> allMyServiceDetails() {
        return ApiResponse.ok(customerServiceService.allMyServiceDetails());
    }

    @PostMapping
    @Operation(summary = "Create a service for the logged in customer")
    public ApiResponse<String> create(@RequestBody @Valid AddServiceDto dto) {
        return ApiResponse.ok(customerServiceService.create(dto));
    }
}
