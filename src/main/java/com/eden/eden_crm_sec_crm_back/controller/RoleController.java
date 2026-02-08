package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.rbac.*;
import com.eden.eden_crm_sec_crm_back.mapper.RbacMapper;
import com.eden.eden_crm_sec_crm_back.service.rbac.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RbacMapper rbacMapper;

    @PostMapping
    public RoleDto create(@RequestBody CreateRoleRequest req) {
        return rbacMapper.toDto(roleService.create(req.name(), req.description(), req.permissionIds()));
    }

    @PutMapping("/{id}")
    public RoleDto update(@PathVariable Integer id, @RequestBody UpdateRoleRequest req) {
        return rbacMapper.toDto(roleService.update(id, req.description(), req.permissionIds()));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        roleService.delete(id);
    }

    @GetMapping
    public PagedResponse<RoleSummaryDto> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = (page != null && size != null)
                ? PageRequest.of(page, size, Sort.by("name").ascending())
                : Pageable.unpaged();

        Page<RoleSummaryDto> result = roleService.getAllSummaries(q, pageable);
        return PagedResponse.from(result);
    }
}