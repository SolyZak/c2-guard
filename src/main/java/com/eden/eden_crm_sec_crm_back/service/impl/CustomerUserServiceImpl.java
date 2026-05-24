package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.AddCustomerUserDto;
import com.eden.eden_crm_sec_crm_back.dto.request.ResetCustomerUserPassword;
import com.eden.eden_crm_sec_crm_back.dto.request.UpdateCustomerUserDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerUserData;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerUserInfoResponse;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.identity.KeycloakClient;
import com.eden.eden_crm_sec_crm_back.identity.dto.UserRequest;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerUser;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerUserMapper;
import com.eden.eden_crm_sec_crm_back.objects.UserType;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerUserRepository;
import com.eden.eden_crm_sec_crm_back.service.AsyncEmailService;
import com.eden.eden_crm_sec_crm_back.service.CustomerUserService;
import com.eden.eden_crm_sec_crm_back.service.rbac.CustomerUserRoleService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.OracleStorageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CustomerUserServiceImpl implements CustomerUserService {
    private final CustomerUserRepository customerUserRepository;
    private final CustomerRepository customerRepository;
    private final CustomerUserMapper mapper;
    private final KeycloakClient keycloakClient;
    private final Utils utils;
    private final AsyncEmailService asyncEmailService;
    private final CustomerUserRoleService customerUserRoleService;
    private final OracleStorageUtil oracleStorageUtil;

    @Value("${customer-portal.url}")
    private String customerPortalUrl;

    @Override
    @Transactional
    public String create(AddCustomerUserDto dto) {
        Long customerId = getLoggedInCustomerId();
        Customer customer = customerRepository.findById(customerId).orElseThrow(UserNotProvided::new);

        validateCreateUser(dto.getEmail(), dto.getCode());

        CustomerUser entity = mapper.toEntity(dto);
        entity.setCustomer(customer);
        customerUserRepository.save(entity);

        String fullLogoUrl = buildFullLogoUrl(customer.getLogo());

        keycloakClient.createUser(new UserRequest(
                entity.getId(),
                customer.getId(),       // customerId for token
                UserType.USER_CUSTOMER,
                entity.getEmail(),
                entity.getName(),
                "",
                dto.getPassword(),
                entity.getEmail(),
                true,
                fullLogoUrl,
                customer.getName()      // customer_name claim — parent's name
        ));
        customerUserRoleService.assignRole(entity.getId(), dto.getRoleId());

        sendEmailToEnabledCustomer(entity.getName(), entity.getEmail(), dto.getPassword());

        return MessageUtil.getMessage("customer-user.created");
    }

    @Override
    public PaginateResponse<CustomerUserData> paginated(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<CustomerUser> resultPage = customerUserRepository.searchByCustomerAndSearch(
                getLoggedInCustomerId(), search, pageable
        );

        return new PaginateResponse<>(
                resultPage.getContent().stream()
                        .map(mapper::toResponse)
                        .toList(),
                page,
                size,
                resultPage.getTotalElements(),
                (long) resultPage.getTotalPages()
        );
    }

    @Override
    public String resetPassword(Long id, ResetCustomerUserPassword dto) {
        CustomerUser user = customerUserRepository.findByIdAncCustomerId(id, getLoggedInCustomerId()).orElseThrow(
                () -> new BusinessException(
                        MessageUtil.getMessage("entity.not-found",
                                new Object[]{MessageUtil.getMessage("customer-user")}),
                        HttpStatus.NOT_FOUND)
        );
        if (keycloakClient.userExits(user.getEmail())) {
            keycloakClient.resetPassword(user.getEmail(), dto.password(), false);
        } else {
            Customer parent = user.getCustomer();
            String fullLogoUrl = buildFullLogoUrl(parent.getLogo());

            keycloakClient.createUser(new UserRequest(
                    user.getId(),
                    parent.getId(),                // customerId for token
                    UserType.USER_CUSTOMER,
                    user.getEmail(),
                    user.getName(),
                    "",
                    dto.password(),
                    user.getEmail(),
                    true,
                    fullLogoUrl,
                    parent.getName()               // customer_name claim — parent's name
            ));
        }
        return MessageUtil.getMessage("password-reset.success");
    }

    @Override
    @Transactional
    public String update(Long id, UpdateCustomerUserDto dto) {
        CustomerUser user = customerUserRepository.findByIdAncCustomerId(id, getLoggedInCustomerId()).orElseThrow(
                () -> new BusinessException(
                        MessageUtil.getMessage("entity.not-found",
                                new Object[]{MessageUtil.getMessage("customer-user")}),
                        HttpStatus.NOT_FOUND)
        );

        if (dto.getCode() != null && !dto.getCode().equals(user.getCode())
                && customerUserRepository.existsByCodeAndIdNot(dto.getCode(), id)) {
            throw new BusinessException(
                    MessageUtil.getMessage("customer-user.code.exists"),
                    HttpStatus.BAD_REQUEST);
        }

        Integer newRoleId = dto.getRoleId();
        Integer currentRoleId = user.getRole() != null ? user.getRole().getId() : null;

        mapper.updateEntity(dto, user);
        customerUserRepository.save(user);

        if (newRoleId != null && !newRoleId.equals(currentRoleId)) {
            customerUserRoleService.assignRole(user.getId(), newRoleId);
        }

        return MessageUtil.getMessage("customer-user.updated");
    }

    private void validateCreateUser(String email, String code) {
        if (keycloakClient.userExits(email)) {
            throw new BusinessException(
                    MessageUtil.getMessage("email-cant-login"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (customerUserRepository.existsByEmail(email)) {
            throw new BusinessException(MessageUtil.getMessage("customer-user.email.exists"), HttpStatus.BAD_REQUEST);
        }
        if (customerUserRepository.existsByCode(code)) {
            throw new BusinessException(MessageUtil.getMessage("customer-user.code.exists"), HttpStatus.BAD_REQUEST);
        }
    }

    private Long getLoggedInCustomerId() {
        return utils.getLoggedInUser().getCustomerId();
    }

    /** Builds the full public URL or returns null if no logo is set. */
    private String buildFullLogoUrl(String storedPath) {
        if (!StringUtils.hasText(storedPath)) return null;
        return oracleStorageUtil.getStorageUrl() + storedPath;
    }

    private void sendEmailToEnabledCustomer(String name, String emailTo, String password) {
        String subject = "Application Credentials";
        String body = "Dear " + name + ",<br><br>" +
                "Your credentials login for the system is, Username: " + emailTo +
                ", password: " + password + "<br>" +
                "You can login through the following link <a href=\"" + customerPortalUrl + "\">visit link</a>" + "<br>";
        asyncEmailService.sendHtmlEmailAsync(emailTo, subject, body);
    }

    @Override
    public CustomerUserInfoResponse getLoggedInUserInfo() {
        var userData = utils.getLoggedInUser();

        return CustomerUserInfoResponse.builder()
                .id(userData.getId())
                .name(userData.getName())
                .type(userData.getType())
                .customerId(userData.getCustomerId())
                .build();
    }
}