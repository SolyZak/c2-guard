package com.eden.eden_crm_sec_crm_back.service.rbac;

import com.eden.eden_crm_sec_crm_back.identity.impl.KeycloakRoleAdminService;
import com.eden.eden_crm_sec_crm_back.models.CustomerUser;
import com.eden.eden_crm_sec_crm_back.models.RoleEntity;
import com.eden.eden_crm_sec_crm_back.models.RoleLifecycle;
import com.eden.eden_crm_sec_crm_back.repository.CustomerUserRepository;
import com.eden.eden_crm_sec_crm_back.repository.RoleLifecycleRepository;
import com.eden.eden_crm_sec_crm_back.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerUserRoleService {

    private final CustomerUserRepository customerUserRepo;
    private final RoleRepository roleRepo;
    private final KeycloakRoleAdminService keycloakRoleAdmin;
    private final RoleLifecycleRepository roleLifecycleRepo;

    @Transactional
    public String assignRole(Long customerUserId, Integer roleId) {
        CustomerUser user = customerUserRepo.findById(customerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Customer user not found: " + customerUserId));

        RoleEntity newRole = roleRepo.findActiveById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));

        Long userCustomerId = user.getCustomer().getId();
        if (!userCustomerId.equals(newRole.getCustomerId())) {
            throw new IllegalArgumentException("Cannot assign a role from another tenant");
        }

        if (user.getRole() != null) {
            keycloakRoleAdmin.removeRealmRoleFromUserByUsername(
                    user.getEmail(),
                    user.getRole().getKeycloakRoleName()
            );
        }

        user.setRole(newRole);
        customerUserRepo.save(user);

        recordLifecycleChange(user, newRole);

        keycloakRoleAdmin.assignRealmRoleToUserByUsername(
                user.getEmail(),
                newRole.getKeycloakRoleName()
        );

        return newRole.getName();
    }

    private void recordLifecycleChange(CustomerUser user, RoleEntity newRole) {
        Optional<RoleLifecycle> openOpt =
                roleLifecycleRepo.findByUserIdAndEndDateIsNull(user.getId());

        if (openOpt.isPresent() && openOpt.get().getRole().getId().equals(newRole.getId())) {
            // same role re-assigned — keep the existing open span untouched
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        openOpt.ifPresent(open -> {
            open.setEndDate(now);
            roleLifecycleRepo.save(open);
        });

        RoleLifecycle next = RoleLifecycle.builder()
                .role(newRole)
                .user(user)
                .customerId(user.getCustomer().getId())
                .startDate(now)
                .build();
        roleLifecycleRepo.save(next);
    }
}