package com.eden.eden_crm_sec_crm_back.service.rbac;

import com.eden.eden_crm_sec_crm_back.identity.impl.KeycloakRoleAdminService;
import com.eden.eden_crm_sec_crm_back.models.CustomerUser;
import com.eden.eden_crm_sec_crm_back.models.RoleEntity;
import com.eden.eden_crm_sec_crm_back.repository.CustomerUserRepository;
import com.eden.eden_crm_sec_crm_back.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerUserRoleService {

    private final CustomerUserRepository customerUserRepo;
    private final RoleRepository roleRepo;
    private final KeycloakRoleAdminService keycloakRoleAdmin;

    @Transactional
    public String assignRole(Long customerUserId, Integer roleId) {
        CustomerUser user = customerUserRepo.findById(customerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Customer user not found: " + customerUserId));

        RoleEntity newRole = roleRepo.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));

        if (user.getRole() != null) {
            keycloakRoleAdmin.removeRealmRoleFromUserByUsername(user.getEmail(), user.getRole().getName());
        }

        user.setRole(newRole);
        customerUserRepo.save(user);

        keycloakRoleAdmin.assignRealmRoleToUserByUsername(user.getEmail(), newRole.getName());

        return newRole.getName();
    }
}