package com.eden.eden_crm_sec_crm_back.identity.impl;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

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
        return rr().roles().list().stream().anyMatch(r -> r.getName().equalsIgnoreCase(roleName));
    }

    public void createRealmRole(String roleName, String description) {
        RoleRepresentation rep = new RoleRepresentation();
        rep.setName(roleName);
        rep.setDescription(description);
        rr().roles().create(rep);
    }

    public void updateRealmRoleDescription(String roleName, String description) {
        RoleResource roleResource = rr().roles().get(roleName);
        RoleRepresentation rep = roleResource.toRepresentation();
        rep.setDescription(description);
        roleResource.update(rep);
    }

    public void deleteRealmRole(String roleName) {
        rr().roles().deleteRole(roleName);
    }

    public void replaceRoleComposites(String roleName, List<String> permissionRoleNames) {
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
    }

    public void assignRealmRoleToUserByUsername(String username, String roleName) {
        UserResource userRes = getUserByUsername(username);
        RoleRepresentation roleRep = rr().roles().get(roleName).toRepresentation();
        userRes.roles().realmLevel().add(List.of(roleRep));
    }

    public void removeRealmRoleFromUserByUsername(String username, String roleName) {
        UserResource userRes = getUserByUsername(username);
        RoleRepresentation roleRep = rr().roles().get(roleName).toRepresentation();
        userRes.roles().realmLevel().remove(List.of(roleRep));
    }

    private UserResource getUserByUsername(String username) {
        UsersResource users = rr().users();
        UserRepresentation user = users.search(username, true).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Keycloak user not found: " + username));
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