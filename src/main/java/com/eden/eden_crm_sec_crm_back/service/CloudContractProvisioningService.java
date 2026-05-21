package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.cloud.CloudProvisionContractRequest;
import com.eden.eden_crm_sec_crm_back.dto.cloud.CloudProvisionContractResponse;
import com.eden.eden_crm_sec_crm_back.enums.ContractStatus;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudContractProvisioningService {

    private final CustomerContractRepository customerContractRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public CloudProvisionContractResponse provision(CloudProvisionContractRequest request) {
        // 1. Idempotency
        if (request.getCloudContractId() != null) {
            Optional<CustomerContract> existing =
                    customerContractRepository.findByCloudContractId(request.getCloudContractId());
            if (existing.isPresent()) {
                log.info("Contract already provisioned for cloudContractId: {}",
                        request.getCloudContractId());
                return CloudProvisionContractResponse.builder()
                        .externalContractId(existing.get().getId())
                        .status("PROVISIONED")
                        .build();
            }
        }

        // 2. Resolve/create local Customer
        Customer customer = resolveOrCreateCustomer(request);

        // 3. Create CustomerContract
        CustomerContract contract = new CustomerContract();
        contract.setAgreementNumber(request.getContractNumber());
        contract.setAgreementName(request.getContractName());
        contract.setStartAgreementDate(request.getStartDate());
        contract.setEndAgreementDate(request.getEndDate());
        contract.setStatus(ContractStatus.SAVED);
        contract.setCurrencyCode(request.getCurrency());
        contract.setCustomer(customer);

        // Cloud-link fields
        contract.setCloudContractId(request.getCloudContractId());
        contract.setCloudCustomerId(request.getCloudCustomerId());
        contract.setGlobalCustomerUuid(request.getGlobalCustomerUuid());
        contract.setSubCloudAccountId(request.getSubCloudAccountId());

        // Module codes
        if (request.getModuleCodes() != null && !request.getModuleCodes().isEmpty()) {
            contract.setCloudModuleCodes(new HashSet<>(request.getModuleCodes()));
        }

        // securityCompanyId: no obvious owner at provisioning time; left null.

        contract = customerContractRepository.save(contract);

        log.info("Cloud contract provisioned: cloudContractId={} -> CRM CustomerContract id={}",
                request.getCloudContractId(), contract.getId());

        return CloudProvisionContractResponse.builder()
                .externalContractId(contract.getId())
                .status("PROVISIONED")
                .build();
    }

    private Customer resolveOrCreateCustomer(CloudProvisionContractRequest request) {
        // Prefer globalCustomerUuid, then cloudCustomerId via paired lookup via UUID,
        // falling back to code/email or creating a stub.
        if (request.getGlobalCustomerUuid() != null) {
            Optional<Customer> byUuid =
                    customerRepository.findByGlobalCustomerUuid(request.getGlobalCustomerUuid());
            if (byUuid.isPresent()) {
                return byUuid.get();
            }
        }

        CloudProvisionContractRequest.CustomerSnapshot snap = request.getCustomer();
        if (snap != null && snap.getCode() != null) {
            Optional<Customer> byCode = customerRepository.findFirstByCode(snap.getCode());
            if (byCode.isPresent()) {
                return byCode.get();
            }
        }
        if (snap != null && snap.getEmail() != null) {
            Optional<Customer> byEmail = customerRepository.findFirstByEmail(snap.getEmail());
            if (byEmail.isPresent()) {
                return byEmail.get();
            }
        }

        // Create a stub Customer mirroring CloudCustomerProvisioningService field mapping.
        Customer customer = new Customer();
        if (snap != null) {
            customer.setCode(snap.getCode());
            customer.setName(snap.getName());
            customer.setEmail(snap.getEmail());
            customer.setPhone(snap.getPhone());
            if (snap.getPhoneCountryCode() != null) {
                customer.setCountryCode(snap.getPhoneCountryCode());
            }
            customer.setAddress(snap.getAddress());
            customer.setRegistrationNumber(snap.getCommercialNumber());
            customer.setLogo(snap.getLogoPath());
        }
        customer.setActive(true);
        customer.setCloudCustomerId(request.getCloudCustomerId());
        customer.setSubCloudAccountId(request.getSubCloudAccountId());
        customer.setGlobalCustomerUuid(request.getGlobalCustomerUuid());
        customer.setCloudOperatorId(request.getCloudOperatorId());

        Customer saved = customerRepository.save(customer);
        log.info("Stub Customer created during contract provisioning: id={} code={}",
                saved.getId(), saved.getCode());
        return saved;
    }
}
