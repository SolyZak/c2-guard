package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Data;

@Data
public class CustomerUserData {
    private Long id;
    private String name;
    private String code;
    private String email;
    private String phone;
    private String countryCode;
}
