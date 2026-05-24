package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.cloud.CloudProvisionC2GuardCustomerRequest;
import com.eden.eden_crm_sec_crm_back.dto.cloud.CloudProvisionCustomerResponse;
import com.eden.eden_crm_sec_crm_back.enums.CustomTimezone;
import com.eden.eden_crm_sec_crm_back.identity.KeycloakClient;
import com.eden.eden_crm_sec_crm_back.identity.dto.UserRequest;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.objects.UserType;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.service.AsyncEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudCustomerProvisioningService {

    private final CustomerRepository customerRepository;
    private final KeycloakClient keycloakClient;
    private final AsyncEmailService asyncEmailService;

    @Transactional
    public CloudProvisionCustomerResponse provision(CloudProvisionC2GuardCustomerRequest request) {
        // Idempotent: check if already provisioned by globalCustomerUuid
        Optional<Customer> existing = customerRepository.findByGlobalCustomerUuid(request.getGlobalCustomerUuid());
        if (existing.isPresent()) {
            log.info("Customer already provisioned for globalCustomerUuid: {}", request.getGlobalCustomerUuid());
            return CloudProvisionCustomerResponse.builder()
                    .externalCustomerId(existing.get().getId())
                    .status("ALREADY_EXISTS")
                    .message("Customer already provisioned")
                    .build();
        }

        // Create customer
        Customer customer = new Customer();
        customer.setCode(request.getCode());
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setCountryCode(request.getCountryCode() != null ? request.getCountryCode() : "00966");
        customer.setAddress(request.getAddress());
        customer.setRegistrationNumber(request.getCommercialRegistration());
        customer.setActive(true);
        customer.setLogo(request.getLogoPath());
        customer.setCloudCustomerId(request.getCloudCustomerId());
        customer.setSubCloudAccountId(request.getSubCloudAccountId());
        customer.setGlobalCustomerUuid(request.getGlobalCustomerUuid());
        customer.setCloudOperatorId(request.getCloudOperatorId());

        if (request.getTimezone() != null) {
            try {
                customer.setTimezone(CustomTimezone.valueOf(request.getTimezone()));
            } catch (IllegalArgumentException e) {
                log.warn("Unknown timezone: {}, defaulting to EGYPT", request.getTimezone());
                customer.setTimezone(CustomTimezone.EGYPT);
            }
        }

        customer = customerRepository.save(customer);

        // Create Keycloak user for customer admin
        if (request.getCustomerAdminEmail() != null) {
            try {
                String username = request.getCustomerAdminUsername() != null
                        ? request.getCustomerAdminUsername()
                        : request.getCustomerAdminEmail();

                keycloakClient.createUser(new UserRequest(
                        null,
                        customer.getId(),
                        UserType.CUSTOMER,
                        username,
                        request.getName(),
                        "",
                        request.getCustomerAdminPassword(),
                        request.getCustomerAdminEmail(),
                        true,
                        request.getLogoPath(),
                        request.getName()
                ));

                log.info("Created Keycloak user for cloud customer: {}", request.getCustomerAdminEmail());
            } catch (Exception e) {
                log.error("Failed to create Keycloak user for cloud customer {}: {}",
                        request.getCustomerAdminEmail(), e.getMessage());
            }
        }

        log.info("Cloud customer provisioned successfully: {} -> CRM ID {}", request.getCode(), customer.getId());

        return CloudProvisionCustomerResponse.builder()
                .externalCustomerId(customer.getId())
                .status("SUCCESS")
                .message("Customer provisioned successfully")
                .build();
    }
}
