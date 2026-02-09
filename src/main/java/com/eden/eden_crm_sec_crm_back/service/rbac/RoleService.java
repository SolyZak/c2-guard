package com.eden.eden_crm_sec_crm_back.service.rbac;

import com.eden.eden_crm_sec_crm_back.dto.rbac.RoleSummaryDto;
import com.eden.eden_crm_sec_crm_back.identity.impl.KeycloakRoleAdminService;
import com.eden.eden_crm_sec_crm_back.models.PermissionEntity;
import com.eden.eden_crm_sec_crm_back.models.RoleEntity;
import com.eden.eden_crm_sec_crm_back.repository.PermissionRepository;
import com.eden.eden_crm_sec_crm_back.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepo;
    private final PermissionRepository permissionRepo;
    private final KeycloakRoleAdminService keycloakRoleAdmin;

    @Transactional
    public RoleEntity create(String name, String description, List<Long> permissionIds) {
        roleRepo.findByNameIgnoreCase(name).ifPresent(r -> {
            throw new IllegalArgumentException("Role already exists: " + name);
        });
        if (keycloakRoleAdmin.realmRoleExists(name)) {
            throw new IllegalArgumentException("Role already exists in Keycloak: " + name);
        }

        List<PermissionEntity> perms = permissionRepo.findAllById(permissionIds);

        keycloakRoleAdmin.createRealmRole(name, description);
        keycloakRoleAdmin.replaceRoleComposites(name, perms.stream().map(PermissionEntity::getKeycloakRoleName).toList());

        RoleEntity role = new RoleEntity();
        role.setName(name);
        role.setDescription(description);
        role.setPermissions(perms);
        return roleRepo.save(role);
    }

    @Transactional
    public RoleEntity update(Integer id, String newName, String description, List<Long> permissionIds) {
        RoleEntity role = roleRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));

        boolean hasNewName = newName != null && !newName.isBlank();
        String oldName = role.getName();
        String effectiveNewName = hasNewName ? newName.trim() : oldName;

        // if name changed -> validate uniqueness in DB + Keycloak
        boolean nameChanged = !oldName.equalsIgnoreCase(effectiveNewName);
        if (nameChanged) {
            roleRepo.findByNameIgnoreCase(effectiveNewName).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("Role already exists: " + effectiveNewName);
                }
            });

            if (keycloakRoleAdmin.realmRoleExists(effectiveNewName)) {
                throw new IllegalArgumentException("Role already exists in Keycloak: " + effectiveNewName);
            }
        }

        List<PermissionEntity> perms = permissionRepo.findAllById(permissionIds);

        if (nameChanged) {
            keycloakRoleAdmin.updateRealmRole(oldName, effectiveNewName, description);
        } else {
            keycloakRoleAdmin.updateRealmRoleDescription(oldName, description);
        }

        // composites should be applied to the FINAL name
        keycloakRoleAdmin.replaceRoleComposites(
                effectiveNewName,
                perms.stream().map(PermissionEntity::getKeycloakRoleName).toList()
        );

        role.setName(effectiveNewName);
        role.setDescription(description);
        role.setPermissions(perms);

        return roleRepo.save(role);
    }

    @Transactional
    public void delete(Integer id) {
        RoleEntity role = roleRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));

        keycloakRoleAdmin.deleteRealmRole(role.getName());

        role.setDeleted(true);
        roleRepo.save(role);
    }

    public Page<RoleSummaryDto> getAllSummaries(String q, Pageable pageable) {
        return roleRepo.searchSummaries(q, pageable)
                .map(p -> new RoleSummaryDto(p.getId(), p.getName(), p.getDescription()));
    }
}