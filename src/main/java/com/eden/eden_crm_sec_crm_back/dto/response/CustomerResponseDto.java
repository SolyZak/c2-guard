package com.eden.eden_crm_sec_crm_back.dto.response;

import com.eden.eden_crm_sec_crm_back.enums.CustomTimezone;
import lombok.Data;

@Data
public class CustomerResponseDto {
    private Long id;
    private String name;
    private String code;
    private String email;
    private String phone;
    private String countryCode;
    private String address;
    private String registrationNumber;
    private CustomTimezone timezone;
    private boolean active;
    private String logo;

}
