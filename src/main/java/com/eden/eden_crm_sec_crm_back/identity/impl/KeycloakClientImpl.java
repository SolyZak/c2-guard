package com.eden.eden_crm_sec_crm_back.identity.impl;

import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.identity.KeycloakClient;
import com.eden.eden_crm_sec_crm_back.identity.dto.UserRequest;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakClientImpl implements KeycloakClient {

    private final Keycloak keycloak;
    private static final String USER_TYPE_ATTRIBUTE = "user_type";
    private static final String USER_ID_ATTRIBUTE = "user_id";

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public Boolean userExists(String username) {
        UsersResource usersResource = getRealmResource().users();
        List<UserRepresentation> users = usersResource.search(username, true);
        return users != null && !users.isEmpty();
    }

    @Override
    public Boolean userExistsIgnoreUserId(String username, Long userId) {
        UsersResource usersResource = getRealmResource().users();
        List<UserRepresentation> users = usersResource.search(username, true);

        if (users == null || users.isEmpty()) {
            return false;
        }

        UserRepresentation user = users.get(0);
        Map<String, List<String>> userAttributes = user.getAttributes();

        if (userAttributes == null) {
            return true;
        }

        List<String> userIdValues = userAttributes.get(USER_ID_ATTRIBUTE);
        if (userIdValues == null || userIdValues.isEmpty()) {
            return true;
        }

        return !userIdValues.get(0).equalsIgnoreCase(userId.toString());
    }

    @Override
    public void createUser(UserRequest userRequest) {
        UserRepresentation userRepresentation = getUserRepresentation(userRequest);

        try (Response response = getRealmResource().users().create(userRepresentation)) {
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                log.info("Successfully created user: {}", userRepresentation.getUsername());
                return;
            }
            log.error("Failed to create user with keycloak response: {}", response.getStatusInfo().getReasonPhrase());
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.failed.create.user"),
                    HttpStatus.BAD_REQUEST
            );
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to create user: {}", e.getMessage(), e);
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.failed.create.user"),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    @Override
    public void deleteUser(String username) {
        UsersResource usersResource = getRealmResource().users();
        UserRepresentation user = usersResource.search(username, true)
                .stream().findFirst()
                .orElseThrow(() -> new BusinessException(
                        MessageUtil.getMessage("identity-manager.user.not.found"),
                        HttpStatus.NOT_FOUND
                ));

        try (Response response = getRealmResource().users().delete(user.getId())) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                log.error("Failed to delete user with keycloak response: {}", response.getStatusInfo().getReasonPhrase());
                throw new BusinessException(
                        MessageUtil.getMessage("identity-manager.failed.delete.user"),
                        HttpStatus.BAD_REQUEST
                );
            }
            log.info("Successfully deleted user: {}", username);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete user: {}", e.getMessage(), e);
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.failed.delete.user"),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    @Override
    public void updateUser(String username, UserRequest updatedRequest) {
        UsersResource usersResource = getRealmResource().users();
        UserRepresentation existingUser = usersResource.search(username, true)
                .stream().findFirst()
                .orElseThrow(() -> new BusinessException(
                        MessageUtil.getMessage("identity-manager.user.not.found"),
                        HttpStatus.NOT_FOUND
                ));

        String userId = existingUser.getId();

        // Update fields
        existingUser.setFirstName(updatedRequest.firstName());
        existingUser.setLastName(updatedRequest.lastName());
        existingUser.setEmail(updatedRequest.email());
        existingUser.setUsername(updatedRequest.username());

        // Update attributes
        Map<String, List<String>> attributes = existingUser.getAttributes() != null
                ? new HashMap<>(existingUser.getAttributes())
                : new HashMap<>();
        attributes.put(USER_TYPE_ATTRIBUTE, Collections.singletonList(updatedRequest.userType().name()));
        attributes.put(USER_ID_ATTRIBUTE, Collections.singletonList(updatedRequest.userId().toString()));
        existingUser.setAttributes(attributes);

        try {
            usersResource.get(userId).update(existingUser);
            log.info("Successfully updated user: {}", username);
        } catch (Exception e) {
            log.error("Failed to update user: {}", e.getMessage(), e);
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.failed.update.user"),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    @Override
    public void resetPassword(String username, String newPassword, boolean forceChangeOnFirstLogin) {
        UsersResource usersResource = getRealmResource().users();
        UserRepresentation user = usersResource.search(username, true)
                .stream().findFirst()
                .orElseThrow(() -> new BusinessException(
                        MessageUtil.getMessage("identity-manager.user.not.found"),
                        HttpStatus.NOT_FOUND
                ));

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        credential.setTemporary(forceChangeOnFirstLogin);

        try {
            usersResource.get(user.getId()).resetPassword(credential);
            log.info("Successfully reset password for user: {}", username);
        } catch (Exception e) {
            log.error("Failed to reset password for user [{}]: {}", username, e.getMessage(), e);
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.failed.reset.password"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private RealmResource getRealmResource() {
        return keycloak.realm(realm);
    }

    private static UserRepresentation getUserRepresentation(UserRequest request) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(request.username());
        userRepresentation.setEmail(request.email());
        userRepresentation.setFirstName(request.firstName());
        userRepresentation.setLastName(request.lastName());
        userRepresentation.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.password());
        credential.setTemporary(request.forceChangePassword());
        userRepresentation.setCredentials(Collections.singletonList(credential));

        // Add custom attributes
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(USER_TYPE_ATTRIBUTE, Collections.singletonList(request.userType().name()));
        attributes.put(USER_ID_ATTRIBUTE, Collections.singletonList(request.userId().toString()));
        userRepresentation.setAttributes(attributes);
        return userRepresentation;
    }
}