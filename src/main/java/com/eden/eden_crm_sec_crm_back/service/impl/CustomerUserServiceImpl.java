package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.AddCustomerUserDto;
import com.eden.eden_crm_sec_crm_back.dto.request.ResetCustomerUserPassword;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerUserData;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerUser;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerUserMapper;
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

    @Override
    @Transactional
    public String create(AddCustomerUserDto dto) {
        Customer customer = customerService.getLoggedInCustomer();

        if(customerUserRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException(MessageUtil.getMessage("customer-user.email.exists"), HttpStatus.BAD_REQUEST);
        }
        if(customerUserRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(MessageUtil.getMessage("customer-user.code.exists"), HttpStatus.BAD_REQUEST);
        }

        CustomerUser entity = mapper.toEntity(dto);
        entity.setCustomer(customer);
        customerUserRepository.save(entity);

        // todo call keycloack to create user here

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

        return new PaginateResponse<CustomerUserData>(
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
        // todo call keycloack to reset password here
        return MessageUtil.getMessage("password-reset.success");
    }
}
