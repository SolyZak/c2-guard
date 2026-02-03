package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    List<PermissionEntity> findByDeletedFalseOrderByKeycloakRoleNameAsc();
    Optional<PermissionEntity> findByKeycloakRoleName(String keycloakRoleName);
}