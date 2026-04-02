package com.eden.eden_crm_sec_crm_back.identity.impl;

import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakRoleAdminService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.permission-client-uuid}")
    private String permissionClientUuid;

    private RealmResource rr() {
        return keycloak.realm(realm);
    }

    public boolean realmRoleExists(String roleName) {
        try {
            rr().roles().get(roleName).toRepresentation();
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    public void createRealmRole(String roleName, String description) {
        RoleRepresentation rep = new RoleRepresentation();
        rep.setName(roleName);
        rep.setDescription(description);
        try {
            rr().roles().create(rep);
            log.info("Created realm role: {}", roleName);
        } catch (Exception e) {
            log.error("Failed to create realm role [{}]: {}", roleName, e.getMessage(), e);
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.failed.create.role"),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    public void updateRealmRoleDescription(String roleName, String description) {
        try {
            RoleResource roleResource = rr().roles().get(roleName);
            RoleRepresentation rep = roleResource.toRepresentation();
            rep.setDescription(description);
            roleResource.update(rep);
            log.info("Updated realm role description: {}", roleName);
        } catch (NotFoundException e) {
            log.error("Realm role not found: {}", roleName);
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.role.not.found"),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            log.error("Failed to update realm role description [{}]: {}", roleName, e.getMessage(), e);
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.failed.update.role"),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    public void updateRealmRole(String oldName, String newName, String description) {
        try {
            RoleResource roleResource = rr().roles().get(oldName);
            RoleRepresentation rep = roleResource.toRepresentation();
            rep.setName(newName);
            rep.setDescription(description);
            roleResource.update(rep);
            log.info("Updated realm role from [{}] to [{}]", oldName, newName);
        } catch (NotFoundException e) {
            log.error("Realm role not found: {}", oldName);
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.role.not.found"),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            log.error("Failed to update realm role [{}]: {}", oldName, e.getMessage(), e);
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.failed.update.role"),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    public void deleteRealmRole(String roleName) {
        try {
            rr().roles().deleteRole(roleName);
            log.info("Deleted realm role: {}", roleName);
        } catch (NotFoundException e) {
            log.error("Realm role not found for deletion: {}", roleName);
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.role.not.found"),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            log.error("Failed to delete realm role [{}]: {}", roleName, e.getMessage(), e);
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.failed.delete.role"),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    public void replaceRoleComposites(String roleName, List<String> permissionRoleNames) {
        try {
            RoleResource roleRes = rr().roles().get(roleName);

            Set<RoleRepresentation> existing = roleRes.getRoleComposites();
            if (existing != null && !existing.isEmpty()) {
                roleRes.deleteComposites(new ArrayList<>(existing));
            }

            if (permissionRoleNames == null || permissionRoleNames.isEmpty()) return;

            List<RoleRepresentation> composites = permissionRoleNames.stream()
                    .map(this::getPermissionClientRole)
                    .toList();

            roleRes.addComposites(composites);
            log.info("Replaced composites for role [{}] with {} permissions", roleName, permissionRoleNames.size());
        } catch (NotFoundException e) {
            log.error("Role or permission not found while replacing composites for: {}", roleName);
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.role.not.found"),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            log.error("Failed to replace composites for role [{}]: {}", roleName, e.getMessage(), e);
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.failed.update.role"),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    public void assignRealmRoleToUserByUsername(String username, String roleName) {
        try {
            UserResource userRes = getUserByUsername(username);
            RoleRepresentation roleRep = rr().roles().get(roleName).toRepresentation();
            userRes.roles().realmLevel().add(List.of(roleRep));
            log.info("Assigned role [{}] to user [{}]", roleName, username);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to assign role [{}] to user [{}]: {}", roleName, username, e.getMessage(), e);
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.failed.assign.role"),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    public void removeRealmRoleFromUserByUsername(String username, String roleName) {
        try {
            UserResource userRes = getUserByUsername(username);
            RoleRepresentation roleRep = rr().roles().get(roleName).toRepresentation();
            userRes.roles().realmLevel().remove(List.of(roleRep));
            log.info("Removed role [{}] from user [{}]", roleName, username);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to remove role [{}] from user [{}]: {}", roleName, username, e.getMessage(), e);
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.failed.remove.role"),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    private UserResource getUserByUsername(String username) {
        UsersResource users = rr().users();
        UserRepresentation user = users.search(username, true).stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        MessageUtil.getMessage("identity-manager.user.not.found"),
                        HttpStatus.NOT_FOUND
                ));
        return users.get(user.getId());
    }

    private RoleRepresentation getPermissionClientRole(String permissionRoleName) {
        return rr().clients()
                .get(permissionClientUuid)
                .roles()
                .get(permissionRoleName)
                .toRepresentation();
    }
}