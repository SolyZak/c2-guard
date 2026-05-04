package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.clients.DocumentsFeignClient;
import com.eden.eden_crm_sec_crm_back.clients.dto.UploadImageRequest;
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
import com.eden.eden_crm_sec_crm_back.repository.CustomerUserRepository;
import com.eden.eden_crm_sec_crm_back.service.AsyncEmailService;
import com.eden.eden_crm_sec_crm_back.service.CustomerService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.OracleStorageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final CustomerUserRepository customerUserRepository;
    private final CustomerMapper mapper;
    private final AsyncEmailService asyncEmailService;
    private final KeycloakClient keycloakClient;
    private final DocumentsFeignClient documentsFeignClient;
    private final OracleStorageUtil oracleStorageUtil;

    @Value("${customer-portal.url}")
    private String customerPortalUrl;

    // ─────────────────────────────────────────────────────────────────────
    //  CREATE
    // ─────────────────────────────────────────────────────────────────────
    @Transactional
    public CustomerResponseDto create(CustomerRequestDto dto, MultipartFile logo) {
        isEmailExists(dto.email());
        isCodeExists(dto.code());
        if (keycloakClient.userExits(dto.email())) {
            throw new BusinessException(
                    MessageUtil.getMessage("email-cant-login"),
                    HttpStatus.BAD_REQUEST);
        }

        Customer customer = mapper.requestToCustomer(dto);
        customer = repository.save(customer); // first save to get the ID

        // Upload logo (if provided) — needs the persisted ID for the path
        if (logo != null && !logo.isEmpty()) {
            String logoPath = uploadLogo(customer.getId(), logo);
            customer.setLogo(logoPath);
            customer = repository.save(customer);
        }

        return toResponse(customer);
    }

    // ─────────────────────────────────────────────────────────────────────
    //  UPDATE
    // ─────────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public CustomerResponseDto update(Long id, UpdateCustomerRequestDto dto, MultipartFile logo) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        MessageUtil.getMessage("exception.customer.not.found"), HttpStatus.NOT_FOUND));

        isEmailExists(dto.email(), id);
        if (keycloakClient.userExitsIgnoreUserId(dto.email(), id)) {
            throw new BusinessException(
                    MessageUtil.getMessage("email-cant-login"),
                    HttpStatus.BAD_REQUEST);
        }

        // Capture old values BEFORE the mapper mutates the entity
        String oldUsername = customer.getEmail();
        String oldName     = customer.getName();

        mapper.updateCustomerFromDto(dto, customer);

        // Replace logo if a new file was uploaded
        boolean logoChanged = false;
        if (logo != null && !logo.isEmpty()) {
            String newLogoPath = uploadLogo(customer.getId(), logo);
            customer.setLogo(newLogoPath);
            logoChanged = true;
        }

        repository.save(customer);

        // Sync Keycloak only if customer is active (= already has a Keycloak user)
        if (customer.isActive()) {
            String fullLogoUrl = buildFullLogoUrl(customer.getLogo());
            boolean nameChanged = !Objects.equals(customer.getName(), oldName);

            UserRequest userRequest = new UserRequest(
                    customer.getId(),
                    customer.getId(),     // customerId == userId for CUSTOMER
                    UserType.CUSTOMER,
                    customer.getEmail(),
                    customer.getName(),
                    "",
                    customer.getEmail() + "@123",
                    customer.getEmail(),
                    true,
                    fullLogoUrl,
                    customer.getName()    // customer_name claim
            );

            if (keycloakClient.userExits(oldUsername)) {
                keycloakClient.updateUser(oldUsername, userRequest);
            } else {
                keycloakClient.createUser(userRequest);
            }

            // Fan-out: refresh customer_logo / customer_name on every CustomerUser
            if (logoChanged || nameChanged) {
                fanOutToCustomerUsers(customer.getId(), fullLogoUrl, customer.getName(),
                        logoChanged, nameChanged);
            }
        }

        return toResponse(customer);
    }

    // ─────────────────────────────────────────────────────────────────────
    //  ENABLE — pushes customer_logo + customer_name into the new Keycloak user
    // ─────────────────────────────────────────────────────────────────────
    @Override
    public void enableCustomer(Long customerId) {
        Customer customer = this.repository.findById(customerId)
                .orElseThrow(() -> new BusinessException(
                        MessageUtil.getMessage("exception.customer.not.found"), HttpStatus.NOT_FOUND));

        if (ObjectUtils.isEmpty(customer.getEmail())) {
            throw new BusinessException(
                    MessageUtil.getMessage("validation.email.not.empty"),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (!keycloakClient.userExits(customer.getEmail())) {
            String password = customer.getEmail() + "@123";
            String fullLogoUrl = buildFullLogoUrl(customer.getLogo());

            keycloakClient.createUser(new UserRequest(
                    customer.getId(),
                    customer.getId(),
                    UserType.CUSTOMER,
                    customer.getEmail(),
                    customer.getName(),
                    "",
                    password,
                    customer.getEmail(),
                    true,
                    fullLogoUrl,
                    customer.getName()    // customer_name claim
            ));
            customer.setActive(true);
            repository.save(customer);
            sendEmailToEnabledCustomer(customer.getEmail(), customer.getId().toString(), password);
        } else {
            if (!customer.isActive()) {
                customer.setActive(true);
                repository.save(customer);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Other methods (unchanged behavior, but use toResponse helper)
    // ─────────────────────────────────────────────────────────────────────
    @Override
    public PaginateResponse<CustomerResponseDto> paginate(CustomerPaginateDto dto) {
        Page<Customer> customers = customers(dto);
        return new PaginateResponse<>(
                customers.getContent().stream().map(this::toResponse).toList(),
                dto.getPage(),
                dto.getSize(),
                customers.getTotalElements(),
                (long) customers.getTotalPages());
    }

    @Override
    public List<CustomerResponseDto> all(String keyword) {
        List<Customer> customers = ObjectUtils.isEmpty(keyword)
                ? repository.findAll() : repository.findAll(keyword);
        return customers.stream().map(this::toResponse).toList();
    }

    @Override
    public CustomerResponseDto show(Long id) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        MessageUtil.getMessage("exception.customer.not.found"), HttpStatus.NOT_FOUND));
        return toResponse(customer);
    }

    @Transactional
    @Override
    public MessageResponse delete(Long id) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        MessageUtil.getMessage("exception.customer.not.found"), HttpStatus.NOT_FOUND));

        if (keycloakClient.userExits(customer.getEmail())) {
            keycloakClient.deleteUser(customer.getEmail());
        }
        repository.delete(customer);
        return new MessageResponse(MessageUtil.getMessage("success.customer.deleted"));
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Private helpers
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Upload the logo to the Documents service. Returns the storage *path*
     * (relative; full URL is built via OracleStorageUtil#getStorageUrl).
     */
    private String uploadLogo(Long customerId, MultipartFile file) {
        String path = String.format("customer/%d/logo/logo%s",
                customerId, extractFileExtension(file));
        try {
            documentsFeignClient.uploadImage(
                    UploadImageRequest.builder()
                            .image(file)
                            .path(path)
                            .build());
        } catch (Exception e) {
            log.error("Failed to upload logo for customer {}: {}", customerId, e.getMessage());
            throw new BusinessException(
                    MessageUtil.getMessage("customer.logo.upload.failed"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return path;
    }

    private String extractFileExtension(MultipartFile file) {
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            return original.substring(original.lastIndexOf('.'));
        }
        return "";
    }

    /** Builds the full public URL or returns null if no logo is set. */
    private String buildFullLogoUrl(String storedPath) {
        if (!StringUtils.hasText(storedPath)) return null;
        return oracleStorageUtil.getStorageUrl() + storedPath;
    }

    /**
     * After a logo or name change, propagate the new values to all CustomerUser
     * Keycloak accounts belonging to this customer. Best-effort.
     */
    private void fanOutToCustomerUsers(Long customerId,
                                       String fullLogoUrl,
                                       String customerName,
                                       boolean updateLogo,
                                       boolean updateName) {
        List<String> emails = customerUserRepository.findEmailsByCustomerId(customerId);
        for (String email : emails) {
            try {
                if (updateLogo) keycloakClient.updateCustomerLogo(email, fullLogoUrl);
                if (updateName) keycloakClient.updateCustomerName(email, customerName);
            } catch (Exception e) {
                log.warn("Could not propagate customer attrs to {}: {}", email, e.getMessage());
            }
        }
    }

    /** Maps entity → response and injects the full logo URL. */
    private CustomerResponseDto toResponse(Customer customer) {
        CustomerResponseDto resp = mapper.customerToResponse(customer);
        resp.setLogo(buildFullLogoUrl(customer.getLogo()));
        return resp;
    }

    // ─── existing validation / pagination helpers unchanged ───
    private void isEmailExists(String email) {
        Optional<Customer> emailExists = repository.findFirstByEmail(email);
        if (emailExists.isPresent()) {
            throw new BusinessException(
                    MessageUtil.getMessage("exception.email.exists"),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private void isEmailExists(String email, Long id) {
        Optional<Customer> emailExists = repository.findFirstByEmailIgnoreId(email, id);
        if (emailExists.isPresent()) {
            throw new BusinessException(
                    MessageUtil.getMessage("exception.email.exists"),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private void isCodeExists(String code) {
        Optional<Customer> codeExists = repository.findFirstByCode(code);
        if (codeExists.isPresent()) {
            throw new BusinessException(
                    MessageUtil.getMessage("exception.code.exists"),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private Page<Customer> customers(CustomerPaginateDto dto) {
        int page = dto.getPage() != null ? dto.getPage() : 0;
        int size = dto.getSize() != null ? dto.getSize() : 0;
        PageRequest pageRequest = PageRequest.of(page, size);
        return ObjectUtils.isEmpty(dto.getSearch())
                ? repository.paginatedCustomers(pageRequest)
                : repository.paginatedCustomers(dto.getSearch(), pageRequest);
    }

    private void sendEmailToEnabledCustomer(String emailTo, String code, String password) {
        String subject = "Customer Portal Credentials";
        String body = "Dear Customer,<br><br>"
                + "Your account has been activated. Please <a href=\"" + customerPortalUrl + "\">visit link</a> "
                + "and find your credentials below:<br><br>"
                + "Username: " + code + "<br>"
                + "Password: " + password + "<br>"
                + "Link: " + customerPortalUrl + "<br>";
        asyncEmailService.sendHtmlEmailAsync(emailTo, subject, body);
    }
}