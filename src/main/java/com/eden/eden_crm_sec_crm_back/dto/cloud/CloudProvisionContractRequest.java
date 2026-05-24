package com.eden.eden_crm_sec_crm_back.dto.cloud;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class CloudProvisionContractRequest {
    private Long cloudContractId;
    private Long cloudCustomerId;
    private UUID globalCustomerUuid;
    private Long subCloudAccountId;
    private Long cloudOperatorId;
    private String contractNumber;
    private String contractName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String currency;         // "USD" | "EGP" | "SAR"
    private String platformCode;     // always "C2GUARD" for this endpoint
    private Long licenseCount;
    private List<String> moduleCodes;
    private CustomerSnapshot customer;

    @Data
    public static class CustomerSnapshot {
        private String code;
        private String name;
        private String contactPerson;
        private String email;
        private String phone;
        private String phoneCountryCode;
        private String countryOfOperation;
        private String address;
        private String taxNumber;
        private String commercialNumber;
        private String logoPath;
    }
}
