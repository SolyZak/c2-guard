package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.CustomerPaginateDto;
import com.eden.eden_crm_sec_crm_back.dto.request.CustomerRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.UpdateCustomerRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerResponseDto;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.payload.MessageResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.service.CustomerService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/customers")
@RequiredArgsConstructor
public class CustomersController {
    private final CustomerService customerService;

    @PostMapping
    ApiResponse<CustomerResponseDto> createCustomer(@Valid @RequestBody CustomerRequestDto dto) {
        return ApiResponse.ok(customerService.create(dto));
    }

    @GetMapping
    ApiResponse<PaginateResponse<CustomerResponseDto>> paginateCustomers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String search
    ) {
        CustomerPaginateDto dto = new CustomerPaginateDto(page, size, search);
        return ApiResponse.ok(customerService.paginate(dto));
    }

    @GetMapping(path = "all")
    ApiResponse<List<CustomerResponseDto>> allCustomers(
            @RequestParam(required = false) String search
    ) {
        return ApiResponse.ok(customerService.all(search));
    }

    @DeleteMapping(path = "{id}")
    ApiResponse<MessageResponse> deleteCustomer(
            @PathVariable() Long id
    ) {
        return ApiResponse.ok(customerService.delete(id));
    }

    @GetMapping(path = "{id}")
    ApiResponse<CustomerResponseDto> showCustomer(
            @PathVariable() Long id
    ) {
        return ApiResponse.ok(customerService.show(id));
    }

    @PutMapping(path = "{id}")
    ApiResponse<CustomerResponseDto> updateCustomer(
            @PathVariable() Long id,
            @Valid @RequestBody UpdateCustomerRequestDto dto
    ) {
        return ApiResponse.ok(customerService.update(id, dto));
    }

    @PostMapping(path = "/enable-customer/{id}")
    ApiResponse<String> enableCustomer(@PathVariable Long id) {
        customerService.enableCustomer(id);
        return ApiResponse.ok(MessageUtil.getMessage("customer.enabled"));
    }
}
