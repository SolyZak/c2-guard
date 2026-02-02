package com.eden.eden_crm_sec_crm_back.service.rbac;

import com.eden.eden_crm_sec_crm_back.models.PermissionEntity;
import com.eden.eden_crm_sec_crm_back.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository repo;

    public List<PermissionEntity> list() {
        return repo.findByDeletedFalseOrderByKeycloakRoleNameAsc();
    }
}