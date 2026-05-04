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
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakClientImpl implements KeycloakClient {

    private final Keycloak keycloak;
    private static final String USER_TYPE_ATTRIBUTE     = "user_type";
    private static final String USER_ID_ATTRIBUTE       = "user_id";
    private static final String CUSTOMER_ID_ATTRIBUTE   = "customer_id";
    private static final String CUSTOMER_LOGO_ATTRIBUTE = "customer_logo";
    private static final String CUSTOMER_NAME_ATTRIBUTE = "customer_name";

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public Boolean userExits(String username) {
        UsersResource usersResource = getRealmResource().users();
        List<UserRepresentation> users = usersResource.searchByUsername(username, true);
        return users != null && !users.isEmpty();
    }

    @Override
    public Boolean userExitsIgnoreUserId(String username, Long userId) {
        UsersResource usersResource = getRealmResource().users();
        List<UserRepresentation> users = usersResource.searchByUsername(username, true);
        if (users != null && !users.isEmpty()) {
            Map<String, List<String>> userAttributes = users.get(0).getAttributes();
            if (userAttributes == null || !userAttributes.containsKey(USER_ID_ATTRIBUTE)) {
                return true;
            }
            Optional<String> userIdExists = userAttributes.get(USER_ID_ATTRIBUTE).stream().findFirst();
            return userIdExists.isEmpty() || !userIdExists.get().equalsIgnoreCase(userId.toString());
        }
        return false;
    }

    @Override
    public void createUser(UserRequest userRequest) {
        UserRepresentation userRepresentation = getUserRepresentation(userRequest);
        try (Response response = getRealmResource().users().create(userRepresentation)) {
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                URI location = response.getLocation();
                if (location != null) return;
            } else {
                log.error("failed to create user with keycloak response: {}", response.getStatusInfo().getReasonPhrase());
                throw new BusinessException(MessageUtil.getMessage("identity-manager.failed.create.user"), HttpStatus.BAD_REQUEST);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to create user: {}", e.getMessage());
            throw new BusinessException(MessageUtil.getMessage("identity-manager.failed.create.user"), HttpStatus.SERVICE_UNAVAILABLE);
        }
        log.error("Failed to return user for username: {}", userRepresentation.getUsername());
        throw new BusinessException(MessageUtil.getMessage("identity-manager.failed.retrieve.user"), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public void deleteUser(String username) {
        UsersResource usersResource = getRealmResource().users();
        UserRepresentation user = usersResource.searchByUsername(username, true)
                .stream().findFirst()
                .orElseThrow(() -> new BusinessException(MessageUtil.getMessage("identity-manager.user.not.found"), HttpStatus.NOT_FOUND));

        try (Response response = getRealmResource().users().delete(user.getId())) {
            if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()
                    && response.getStatus() != Response.Status.OK.getStatusCode()) {
                log.error("failed to delete user with keycloak response: {}", response.getStatusInfo().getReasonPhrase());
                throw new BusinessException(MessageUtil.getMessage("identity-manager.failed.delete.user"), HttpStatus.BAD_REQUEST);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete user: {}", e.getMessage());
            throw new BusinessException(MessageUtil.getMessage("identity-manager.failed.delete.user"), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public void updateUser(String username, UserRequest updatedRequest) {
        UsersResource usersResource = getRealmResource().users();
        Optional<UserRepresentation> userOptional = usersResource.searchByUsername(username, true)
                .stream().findFirst();

        if (userOptional.isEmpty()) {
            throw new BusinessException(MessageUtil.getMessage("identity-manager.user.not.found"), HttpStatus.NOT_FOUND);
        }

        UserRepresentation existingUser = userOptional.get();
        String userId = existingUser.getId();

        existingUser.setFirstName(updatedRequest.firstName());
        existingUser.setLastName(updatedRequest.lastName());
        existingUser.setEmail(updatedRequest.email());
        existingUser.setUsername(updatedRequest.username());

        Map<String, List<String>> attributes = existingUser.getAttributes() != null
                ? new HashMap<>(existingUser.getAttributes())
                : new HashMap<>();
        attributes.put(USER_TYPE_ATTRIBUTE, Collections.singletonList(updatedRequest.userType().name()));
        attributes.put(USER_ID_ATTRIBUTE,   Collections.singletonList(updatedRequest.userId().toString()));
        if (updatedRequest.customerId() != null) {
            attributes.put(CUSTOMER_ID_ATTRIBUTE, Collections.singletonList(updatedRequest.customerId().toString()));
        }
        if (StringUtils.hasText(updatedRequest.customerLogo())) {
            attributes.put(CUSTOMER_LOGO_ATTRIBUTE, Collections.singletonList(updatedRequest.customerLogo()));
        } else {
            attributes.remove(CUSTOMER_LOGO_ATTRIBUTE);
        }
        if (StringUtils.hasText(updatedRequest.customerName())) {
            attributes.put(CUSTOMER_NAME_ATTRIBUTE, Collections.singletonList(updatedRequest.customerName()));
        } else {
            attributes.remove(CUSTOMER_NAME_ATTRIBUTE);
        }
        existingUser.setAttributes(attributes);

        try {
            usersResource.get(userId).update(existingUser);
        } catch (Exception e) {
            log.error("Failed to update user: {}", e.getMessage());
            throw new BusinessException(MessageUtil.getMessage("identity-manager.failed.update.user"), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public void resetPassword(String username, String newPassword, boolean forceChangeOnFirstLogin) {
        UsersResource usersResource = getRealmResource().users();
        UserRepresentation user = usersResource.searchByUsername(username, true)
                .stream().findFirst()
                .orElseThrow(() -> new BusinessException(
                        MessageUtil.getMessage("identity-manager.user.not.found"), HttpStatus.NOT_FOUND));

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        credential.setTemporary(forceChangeOnFirstLogin);

        try {
            usersResource.get(user.getId()).resetPassword(credential);
        } catch (Exception e) {
            log.error("Failed to reset password for user [{}]: {}", username, e.getMessage());
            throw new BusinessException(
                    MessageUtil.getMessage("identity-manager.failed.reset.password"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void updateCustomerLogo(String username, String newLogoUrl) {
        UsersResource usersResource = getRealmResource().users();
        Optional<UserRepresentation> userOpt = usersResource.searchByUsername(username, true)
                .stream().findFirst();

        if (userOpt.isEmpty()) {
            log.debug("Skipping customer_logo update — user [{}] not found in Keycloak", username);
            return;
        }

        UserRepresentation existing = userOpt.get();
        Map<String, List<String>> attributes = existing.getAttributes() != null
                ? new HashMap<>(existing.getAttributes())
                : new HashMap<>();

        if (StringUtils.hasText(newLogoUrl)) {
            attributes.put(CUSTOMER_LOGO_ATTRIBUTE, Collections.singletonList(newLogoUrl));
        } else {
            attributes.remove(CUSTOMER_LOGO_ATTRIBUTE);
        }
        existing.setAttributes(attributes);

        try {
            usersResource.get(existing.getId()).update(existing);
        } catch (Exception e) {
            log.error("Failed to update customer_logo for user [{}]: {}", username, e.getMessage());
            // best-effort
        }
    }

    @Override
    public void updateCustomerName(String username, String newName) {
        UsersResource usersResource = getRealmResource().users();
        Optional<UserRepresentation> userOpt = usersResource.searchByUsername(username, true)
                .stream().findFirst();

        if (userOpt.isEmpty()) {
            log.debug("Skipping customer_name update — user [{}] not found in Keycloak", username);
            return;
        }

        UserRepresentation existing = userOpt.get();
        Map<String, List<String>> attributes = existing.getAttributes() != null
                ? new HashMap<>(existing.getAttributes())
                : new HashMap<>();

        if (StringUtils.hasText(newName)) {
            attributes.put(CUSTOMER_NAME_ATTRIBUTE, Collections.singletonList(newName));
        } else {
            attributes.remove(CUSTOMER_NAME_ATTRIBUTE);
        }
        existing.setAttributes(attributes);

        try {
            usersResource.get(existing.getId()).update(existing);
        } catch (Exception e) {
            log.error("Failed to update customer_name for user [{}]: {}", username, e.getMessage());
            // best-effort
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

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(USER_TYPE_ATTRIBUTE, Collections.singletonList(request.userType().name()));
        attributes.put(USER_ID_ATTRIBUTE,   Collections.singletonList(request.userId().toString()));
        if (request.customerId() != null) {
            attributes.put(CUSTOMER_ID_ATTRIBUTE, Collections.singletonList(request.customerId().toString()));
        }
        if (StringUtils.hasText(request.customerLogo())) {
            attributes.put(CUSTOMER_LOGO_ATTRIBUTE, Collections.singletonList(request.customerLogo()));
        }
        if (StringUtils.hasText(request.customerName())) {
            attributes.put(CUSTOMER_NAME_ATTRIBUTE, Collections.singletonList(request.customerName()));
        }
        userRepresentation.setAttributes(attributes);
        return userRepresentation;
    }
}