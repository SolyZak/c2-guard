package com.eden.eden_crm_sec_crm_back.dto.cloud;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CloudProvisionC2GuardCustomerRequest {
    private Long cloudCustomerId;
    private UUID globalCustomerUuid;
    private Long subCloudAccountId;
    private Long cloudOperatorId;
    private String code;
    private String name;
    private String commercialRegistration;
    private String email;
    private String phone;
    private String countryCode;
    private String address;
    private String timezone;
    private String logoPath;
    private String customerAdminEmail;
    private String customerAdminUsername;
    private String customerAdminPassword;
    private List<String> enabledModules;
    private Long guardsCapacity;
}
