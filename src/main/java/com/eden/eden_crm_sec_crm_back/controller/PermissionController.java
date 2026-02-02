package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.rbac.PermissionDto;
import com.eden.eden_crm_sec_crm_back.mapper.RbacMapper;
import com.eden.eden_crm_sec_crm_back.service.rbac.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions") // contextPath /crm already applies
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService service;
    private final RbacMapper rbacMapper;

    @GetMapping
    public List<PermissionDto> list() {
        return service.list().stream().map(rbacMapper::toDto).toList();
    }
}