package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.base.util.ApiResponse;
import com.eden.eden_crm_sec_crm_back.dto.rbac.PredefinedData.*;

import com.eden.eden_crm_sec_crm_back.service.rbac.PredefinedDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/predefined-data")
@RequiredArgsConstructor
public class PredefinedDataController {

    private final PredefinedDataService service;

    @GetMapping
    public ApiResponse<List<PredefinedClassDto>> getPredefined() {
        return ApiResponse.ok(service.predefinedNoChecks());
    }

    @GetMapping("/{roleId}")
    public ApiResponse<List<PredefinedClassCheckedDto>> getPredefinedForRole(@PathVariable Integer roleId) {
        return ApiResponse.ok(service.predefinedWithChecks(roleId));
    }
}