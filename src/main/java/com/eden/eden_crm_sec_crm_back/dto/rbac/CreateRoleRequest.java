package com.eden.eden_crm_sec_crm_back.dto.rbac;

import java.util.List;

public record CreateRoleRequest(String name, String description, List<Long> permissionIds) {}