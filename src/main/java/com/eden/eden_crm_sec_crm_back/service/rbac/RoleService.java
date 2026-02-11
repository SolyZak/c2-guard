package com.eden.eden_crm_sec_crm_back.service.rbac;

import com.eden.eden_crm_sec_crm_back.dto.rbac.RoleSummaryDto;
import com.eden.eden_crm_sec_crm_back.identity.impl.KeycloakRoleAdminService;
import com.eden.eden_crm_sec_crm_back.models.PermissionEntity;
import com.eden.eden_crm_sec_crm_back.models.RoleEntity;
import com.eden.eden_crm_sec_crm_back.repository.CustomerUserRepository;
import com.eden.eden_crm_sec_crm_back.repository.PermissionRepository;
import com.eden.eden_crm_sec_crm_back.repository.RoleRepository;
import com.eden.eden_crm_sec_crm_back.utils.TenantRoleKeycloakName;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
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
    private final CustomerUserRepository customerUserRepository;
    private final KeycloakRoleAdminService keycloakRoleAdmin;
    private final Utils utils;

    private Long currentCustomerId() {
        return utils.getLoggedInUser().getCustomerId();
    }

    @Transactional
    public RoleEntity create(String name, String description, List<Long> permissionIds) {
        Long customerId = currentCustomerId();

        String displayName = name == null ? null : name.trim();
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Role name is required");
        }

        if (roleRepo.existsByCustomerIdAndNameIgnoreCaseAndDeletedFalse(customerId, displayName)) {
            throw new IllegalArgumentException("Role already exists: " + displayName);
        }

        List<PermissionEntity> perms = permissionRepo.findAllById(permissionIds);

        // retry a few times in the extremely unlikely event of a collision
        final int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            String keycloakRoleName = TenantRoleKeycloakName.build(customerId, displayName);

            try {
                keycloakRoleAdmin.createRealmRole(keycloakRoleName, description);
                keycloakRoleAdmin.replaceRoleComposites(
                        keycloakRoleName,
                        perms.stream().map(PermissionEntity::getKeycloakRoleName).toList()
                );

                RoleEntity role = new RoleEntity();
                role.setCustomerId(customerId);
                role.setName(displayName);
                role.setKeycloakRoleName(keycloakRoleName);
                role.setDescription(description);
                role.setPermissions(perms);

                return roleRepo.save(role);

            } catch (jakarta.ws.rs.WebApplicationException ex) {
                // Keycloak conflict on role create -> retry
                if (ex.getResponse() != null && ex.getResponse().getStatus() == 409 && attempt < maxAttempts) {
                    continue;
                }
                throw ex;
            }
        }

        throw new IllegalStateException("Failed to create role after retries");
    }

    @Transactional
    public RoleEntity update(Integer id, String newName, String description, List<Long> permissionIds) {
        Long customerId = currentCustomerId();

        RoleEntity role = roleRepo.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));

        if (!role.getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("Cannot update role outside your tenant");
        }

        String effectiveNewName = (newName != null && !newName.isBlank())
                ? newName.trim()
                : role.getName();

        boolean nameChanged = !role.getName().equalsIgnoreCase(effectiveNewName);
        if (nameChanged) {
            if (roleRepo.existsByCustomerIdAndNameIgnoreCaseAndDeletedFalse(customerId, effectiveNewName)) {
                throw new IllegalArgumentException("Role already exists: " + effectiveNewName);
            }
        }

        List<PermissionEntity> perms = permissionRepo.findAllById(permissionIds);

        keycloakRoleAdmin.updateRealmRoleDescription(role.getKeycloakRoleName(), description);

        keycloakRoleAdmin.replaceRoleComposites(
                role.getKeycloakRoleName(),
                perms.stream().map(PermissionEntity::getKeycloakRoleName).toList()
        );

        role.setName(effectiveNewName);
        role.setDescription(description);
        role.setPermissions(perms);

        return roleRepo.save(role);
    }

    @Transactional
    public void delete(Integer id) {
        Long customerId = currentCustomerId();

        RoleEntity role = roleRepo.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));

        if (!role.getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("Cannot delete role outside your tenant");
        }

        boolean isUsed = customerUserRepository.existsActiveUserUsingRole(role.getId(), customerId);
        if (isUsed) {
            throw new IllegalStateException("Cannot delete role because it is assigned to one or more users");
        }

        keycloakRoleAdmin.deleteRealmRole(role.getKeycloakRoleName());

        role.setDeleted(true);
        roleRepo.save(role);
    }

    public Page<RoleSummaryDto> getAllSummaries(String q, Pageable pageable) {
        Long customerId = currentCustomerId();

        return roleRepo.searchSummaries(customerId, q, pageable)
                .map(p -> new RoleSummaryDto(p.getId(), p.getName(), p.getDescription()));
    }
}