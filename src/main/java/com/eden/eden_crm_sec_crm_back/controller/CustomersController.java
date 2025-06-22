package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.CustomerPaginateDto;
import com.eden.eden_crm_sec_crm_back.dto.request.CustomerRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.UpdateCustomerRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerResponseDto;
import com.eden.eden_crm_sec_crm_back.enums.CustomTimezone;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.payload.MessageResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.service.CustomerService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/eden/customers")
@Tag(
        name = "Eden Marketplace, Customers APIs",
        description = "This part is for eden marketplace part of the system it'll provide the needed APIs for customers")
@RequiredArgsConstructor
public class CustomersController {
    private final CustomerService customerService;

    @Operation(summary = "Get allowed timezones dropdown")
    @GetMapping("/timezone/dropdown")
    ApiResponse<List<CustomTimezone>> timezoneDropdown() {
        return ApiResponse.ok(List.of(
                CustomTimezone.EGYPT,
                CustomTimezone.SAUDI_ARABIA,
                CustomTimezone.EMIRATES
        ));
    }

    @Operation(summary = "Create customer")
    @PostMapping
    ApiResponse<CustomerResponseDto> createCustomer(@Valid @RequestBody CustomerRequestDto dto) {
        return ApiResponse.ok(customerService.create(dto));
    }

    @Operation(summary = "Paginate customers")
    @GetMapping
    ApiResponse<PaginateResponse<CustomerResponseDto>> paginateCustomers(
            @RequestParam(defaultValue = "0", name = "page") Integer page,
            @RequestParam(defaultValue = "10", name = "size") Integer size,
            @RequestParam(required = false, name = "search") String search
    ) {
        CustomerPaginateDto dto = new CustomerPaginateDto(page, size, search);
        return ApiResponse.ok(customerService.paginate(dto));
    }

    @Operation(summary = "All customers")
    @GetMapping(path = "all")
    ApiResponse<List<CustomerResponseDto>> allCustomers(
            @RequestParam(required = false, name = "search") String search
    ) {
        return ApiResponse.ok(customerService.all(search));
    }

    @Operation(summary = "Delete a customer")
    @DeleteMapping(path = "{id}")
    ApiResponse<MessageResponse> deleteCustomer(
            @PathVariable("id") Long id
    ) {
        return ApiResponse.ok(customerService.delete(id));
    }

    @Operation(summary = "Get single customer")
    @GetMapping(path = "{id}")
    ApiResponse<CustomerResponseDto> showCustomer(
            @PathVariable("id") Long id
    ) {
        return ApiResponse.ok(customerService.show(id));
    }

    @Operation(summary = "Update a customer")
    @PutMapping(path = "{id}")
    ApiResponse<CustomerResponseDto> updateCustomer(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateCustomerRequestDto dto
    ) {
        return ApiResponse.ok(customerService.update(id, dto));
    }

    @Operation(summary = "Enable customer account via keycloak")
    @PostMapping(path = "/enable-customer/{id}")
    ApiResponse<String> enableCustomer(@PathVariable("id") Long id) {
        customerService.enableCustomer(id);
        return ApiResponse.ok(MessageUtil.getMessage("customer.enabled"));
    }
}
