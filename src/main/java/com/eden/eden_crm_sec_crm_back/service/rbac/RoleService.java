package com.eden.eden_crm_sec_crm_back.service.rbac;

import com.eden.eden_crm_sec_crm_back.dto.rbac.RoleSummaryDto;
import com.eden.eden_crm_sec_crm_back.identity.impl.KeycloakRoleAdminService;
import com.eden.eden_crm_sec_crm_back.models.PermissionEntity;
import com.eden.eden_crm_sec_crm_back.models.RoleEntity;
import com.eden.eden_crm_sec_crm_back.repository.PermissionRepository;
import com.eden.eden_crm_sec_crm_back.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    public RoleEntity update(Integer id, String description, List<Long> permissionIds) {
        RoleEntity role = roleRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));

        //keep role name immutable to avoid rename complexity
        List<PermissionEntity> perms = permissionRepo.findAllById(permissionIds);

        keycloakRoleAdmin.updateRealmRoleDescription(role.getName(), description);
        keycloakRoleAdmin.replaceRoleComposites(role.getName(), perms.stream().map(PermissionEntity::getKeycloakRoleName).toList());

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

    public List<RoleSummaryDto> getAllSummaries() {
        return roleRepo.findByDeletedFalseOrderByNameAsc()
                .stream()
                .map(p -> new RoleSummaryDto(p.getId(), p.getName(), p.getDescription()))
                .toList();
    }
}