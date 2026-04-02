package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.VendorRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.VendorResponseDto;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/vendors")
@Tag(
        name = "Vendors APIs",
        description = "APIs for managing vendors for cameras"
)
@RequiredArgsConstructor
public class VendorsController {
    private final VendorService vendorService;

    @Operation(summary = "Create a new vendor")
    @PostMapping
    ApiResponse<VendorResponseDto> createVendor(@Valid @RequestBody VendorRequestDto dto) {
        return ApiResponse.ok(vendorService.create(dto));
    }

    @Operation(summary = "Get all vendors without pagination")
    @GetMapping
    ApiResponse<List<VendorResponseDto>> getAllVendors() {
        return ApiResponse.ok(vendorService.getAll());
    }

    @Operation(summary = "Get all vendors with pagination")
    @GetMapping(path = "paginated")
    ApiResponse<PaginateResponse<VendorResponseDto>> getPaginatedVendors(
            @RequestParam(defaultValue = "0", name = "page") Integer page,
            @RequestParam(defaultValue = "10", name = "size") Integer size
    ) {
        return ApiResponse.ok(vendorService.getPaginated(page, size));
    }
}

