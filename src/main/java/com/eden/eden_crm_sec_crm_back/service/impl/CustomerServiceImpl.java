package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.CustomerPaginateDto;
import com.eden.eden_crm_sec_crm_back.dto.request.CustomerRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.UpdateCustomerRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerResponseDto;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.identity.KeycloakClient;
import com.eden.eden_crm_sec_crm_back.identity.dto.UserRequest;
import com.eden.eden_crm_sec_crm_back.mapper.CustomerMapper;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.objects.UserType;
import com.eden.eden_crm_sec_crm_back.payload.MessageResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.service.AsyncEmailService;
import com.eden.eden_crm_sec_crm_back.service.CustomerService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final AsyncEmailService asyncEmailService;
    private final KeycloakClient keycloakClient;

    @Value("${customer-portal.url}")
    private String customerPortalUrl;

    @Transactional
    public CustomerResponseDto create(CustomerRequestDto dto) {
        isEmailExists(dto.email());
        isCodeExists(dto.code());
        if (keycloakClient.userExits(dto.email())) {
            throw new BusinessException(
                    MessageUtil.getMessage("email-cant-login"),
                    HttpStatus.BAD_REQUEST
            );
        }

        Customer customer = mapper.requestToCustomer(dto);
        customer = repository.save(customer);

        return mapper.customerToResponse(customer);
    }

    @Override
    public PaginateResponse<CustomerResponseDto> paginate(CustomerPaginateDto dto) {
        Page<Customer> customers = customers(dto);

        return new PaginateResponse<>(
                customers.getContent().stream().map(mapper::customerToResponse).toList(),
                dto.getPage(),
                dto.getSize(),
                customers.getTotalElements(),
                (long) customers.getTotalPages()
        );
    }

    @Override
    public List<CustomerResponseDto> all(String keyword) {
        List<Customer> customers = ObjectUtils.isEmpty(keyword) ? repository.findAll() : repository.findAll(keyword);
        return customers
                .stream()
                .map(mapper::customerToResponse)
                .toList();
    }

    @Transactional
    @Override
    public MessageResponse delete(Long id) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new BusinessException(MessageUtil.getMessage("exception.customer.not.found"), HttpStatus.NOT_FOUND));

        keycloakClient.deleteUser(customer.getEmail());

        // TODO: check if customer has active projects or not if yes throw next exception
        // throw new BusinessException(ExceptionMessages.CUSTOMER_HAS_PROJECT, HttpStatus.BAD_REQUEST);

        // TODO: need to apply soft deletes which will reflect in getting data
        repository.delete(customer);

        return new MessageResponse(MessageUtil.getMessage("success.customer.deleted"));
    }

    @Override
    public CustomerResponseDto show(Long id) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new BusinessException(MessageUtil.getMessage("exception.customer.not.found"), HttpStatus.NOT_FOUND));

        return mapper.customerToResponse(customer);
    }

    @Override
    public CustomerResponseDto update(Long id, UpdateCustomerRequestDto dto) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new BusinessException(MessageUtil.getMessage("exception.customer.not.found"), HttpStatus.NOT_FOUND));

        // validate email
        isEmailExists(dto.email(), id);
        if (keycloakClient.userExitsIgnoreUserId(dto.email(), id)) {
            throw new BusinessException(
                    MessageUtil.getMessage("email-cant-login"),
                    HttpStatus.BAD_REQUEST
            );
        }
        String oldUsername = customer.getEmail();

        mapper.updateCustomerFromDto(dto, customer);

        repository.save(customer);

        if (customer.isActive()) {
            UserRequest userRequest = new UserRequest(
                    customer.getId(), UserType.CUSTOMER, customer.getEmail(), customer.getName(), "",
                    customer.getEmail() + "@123", customer.getEmail(), true
            );
            if (keycloakClient.userExits(oldUsername)) {
                keycloakClient.updateUser(oldUsername, userRequest);
            } else {
                keycloakClient.createUser(userRequest);
            }
        }

        return mapper.customerToResponse(customer);
    }

    @Override
    public void enableCustomer(Long customerId) {
        Customer customer = this.repository.findById(customerId)
                .orElseThrow(() -> new BusinessException(MessageUtil.getMessage("exception.customer.not.found"), HttpStatus.NOT_FOUND));

        if (ObjectUtils.isEmpty(customer.getEmail())) {
            throw new BusinessException(MessageUtil.getMessage("validation.email.not.empty"), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (!keycloakClient.userExits(customer.getEmail())) {
            String password = customer.getEmail() + "@123";
            keycloakClient.createUser(new UserRequest(
                    customer.getId(),
                    UserType.CUSTOMER,
                    customer.getEmail(),
                    customer.getName(),
                    "",
                    password,
                    customer.getEmail(),
                    true
            ));
            customer.setActive(true);
            repository.save(customer);
            sendEmailToEnabledCustomer(customer.getEmail(), customer.getId().toString(), password);
        }
    }

    private void isEmailExists(String email) {
        Optional<Customer> emailExists = repository.findFirstByEmail(email);
        if (emailExists.isPresent()) {
            throw new BusinessException(MessageUtil.getMessage("exception.email.exists"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private void isEmailExists(String email, Long id) {
        Optional<Customer> emailExists = repository.findFirstByEmailIgnoreId(email, id);
        if (emailExists.isPresent()) {
            throw new BusinessException(MessageUtil.getMessage("exception.email.exists"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private void isCodeExists(String code) {
        Optional<Customer> codeExists = repository.findFirstByCode(code);
        if (codeExists.isPresent()) {
            throw new BusinessException(MessageUtil.getMessage("exception.code.exists"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private Page<Customer> customers(CustomerPaginateDto dto) {
        int page = dto.getPage() != null ? dto.getPage() : 0;
        int size = dto.getSize() != null ? dto.getSize() : 0;
        PageRequest pageRequest = PageRequest.of(page, size);

        return
                ObjectUtils.isEmpty(dto.getSearch()) ?
                        repository.paginatedCustomers(pageRequest) :
                        repository.paginatedCustomers(dto.getSearch(), pageRequest);
    }

    private void sendEmailToEnabledCustomer(String emailTo, String code, String password) {
        String subject = "Customer Portal Credentials";
        String body = "Dear Customer,<br><br>" +
                "Your account has been activated. Please <a href=\"" + customerPortalUrl + "\">visit link</a> and find your credentials below:<br>" +
                "<br>" +
                "Username: " + code + "<br>" +
                "Password: " + password + "<br>" +
                "Link: " + customerPortalUrl + "<br>";
        asyncEmailService.sendHtmlEmailAsync(emailTo, subject, body);
    }
}
