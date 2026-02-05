package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.rbac.AssignCustomerUserRoleRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.AddCustomerUserDto;
import com.eden.eden_crm_sec_crm_back.dto.request.CustomerActivationRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.ResetCustomerUserPassword;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerUserData;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerUserInfoResponse;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.service.CustomerUserService;
import com.eden.eden_crm_sec_crm_back.service.rbac.CustomerUserRoleService;

import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/customer/users")
@RequiredArgsConstructor
@Tag(
        name = "Customer Users API",
        description = "This part is for customer portal, will provide the needed for customer users API")
public class CustomerUserController {

    private final CustomerUserService customerUserService;
    private final CustomerUserRoleService CustomerUserRoleService;

    @PostMapping
    @Operation(summary = "Create customer user")
    ApiResponse<String> create(@RequestBody @Valid AddCustomerUserDto dto) {
        return ApiResponse.ok(customerUserService.create(dto));
    }

    @PutMapping("{id}")
    @Operation(summary = "Rest customer user password")
    ApiResponse<String> resetPassword(
            @PathVariable("id") Long id,
            @RequestBody @Valid ResetCustomerUserPassword dto
    ) {
        return ApiResponse.ok(customerUserService.resetPassword(id, dto));
    }

    @Operation(summary = "Paginate customer users")
    @GetMapping
    ApiResponse<PaginateResponse<CustomerUserData>> paginated(
            @RequestParam(defaultValue = "0", name = "page") Integer page,
            @RequestParam(defaultValue = "10", name = "size") Integer size,
            @RequestParam(required = false, name = "search") String search
    ) {
        return ApiResponse.ok(customerUserService.paginated(search, page, size));
    }
    @GetMapping("/info")
    ApiResponse<CustomerUserInfoResponse> getLoggedInCustomerUser() {
        return ApiResponse.ok(customerUserService.getLoggedInUserInfo());
    }

    @PutMapping("/{id}/role")
    public Map<String, String> assign(@PathVariable Long id, @RequestBody AssignCustomerUserRoleRequest req) {
        String roleName = CustomerUserRoleService.assignRole(id, req.roleId());
        return Map.of("roleName", roleName);
    }

    @Operation(summary = "Activate/Deactivate customer user (DB + Keycloak)")
    @PatchMapping("{id}/activation")
    ApiResponse<String> setActivation(
            @PathVariable("id") Long id,
            @Valid @RequestBody CustomerActivationRequestDto dto
    ) {
        customerUserService.setCustomerUserActivation(id, dto.active());
        return ApiResponse.ok(dto.active()
                ? MessageUtil.getMessage("customer-user.activated")
                : MessageUtil.getMessage("customer-user.deactivated"));
    }
}
