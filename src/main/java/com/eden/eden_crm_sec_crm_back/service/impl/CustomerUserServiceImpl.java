package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.AddCustomerUserDto;
import com.eden.eden_crm_sec_crm_back.dto.request.ResetCustomerUserPassword;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerUserData;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.identity.KeycloakClient;
import com.eden.eden_crm_sec_crm_back.identity.dto.UserRequest;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerUser;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerUserMapper;
import com.eden.eden_crm_sec_crm_back.objects.UserType;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.CustomerUserRepository;
import com.eden.eden_crm_sec_crm_back.service.CustomerService;
import com.eden.eden_crm_sec_crm_back.service.CustomerUserService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerUserServiceImpl implements CustomerUserService {
    private final CustomerUserRepository customerUserRepository;
    private final CustomerUserMapper mapper;
    private final CustomerService customerService;
    private final KeycloakClient keycloakClient;

    @Override
    @Transactional
    public String create(AddCustomerUserDto dto) {
        Customer customer = customerService.getLoggedInCustomer();

        validateCreateUser(dto.getEmail(), dto.getCode());

        CustomerUser entity = mapper.toEntity(dto);
        entity.setCustomer(customer);
        customerUserRepository.save(entity);

        keycloakClient.createUser(new UserRequest(
                entity.getId(), UserType.USER_CUSTOMER, entity.getEmail(), entity.getName(),
                "", dto.getPassword(), entity.getEmail(), true
        ));

        return MessageUtil.getMessage("customer-user.created");
    }

    @Override
    public PaginateResponse<CustomerUserData> paginated(String search, int page, int size) {
        // todo, need to ignore the logged in user in case the logged in user is customer user actor
        Customer customer = customerService.getLoggedInCustomer();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<CustomerUser> resultPage = customerUserRepository.searchByCustomerAndSearch(
                customer.getId(), search, pageable
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
        // todo, need to ignore the logged in user in case the logged in user is customer user actor
        Customer customer = customerService.getLoggedInCustomer();
        CustomerUser user = customerUserRepository.findByIdAncCustomerId(id, customer.getId()).orElseThrow(
                () -> new BusinessException(MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("customer-user")}), HttpStatus.NOT_FOUND)
        );
        if (keycloakClient.userExits(user.getEmail())) {
            keycloakClient.resetPassword(user.getEmail(), dto.password(), false);
        } else {
            keycloakClient.createUser(new UserRequest(
                    user.getId(), UserType.USER_CUSTOMER, user.getEmail(), user.getName(),
                    "", dto.password(), user.getEmail(), true
            ));
        }
        return MessageUtil.getMessage("password-reset.success");
    }

    private void validateCreateUser(String email, String code) {
        if (keycloakClient.userExits(email)) {
            throw new BusinessException(
                    MessageUtil.getMessage("email-cant-login"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if(customerUserRepository.existsByEmail(email)) {
            throw new BusinessException(MessageUtil.getMessage("customer-user.email.exists"), HttpStatus.BAD_REQUEST);
        }
        if(customerUserRepository.existsByCode(code)) {
            throw new BusinessException(MessageUtil.getMessage("customer-user.code.exists"), HttpStatus.BAD_REQUEST);
        }
    }
}
