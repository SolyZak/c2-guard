package com.eden.eden_crm_sec_crm_back.dto.request;

import com.eden.eden_crm_sec_crm_back.utils.Constants;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCustomerUserDto {

    @Size(min = 1, max = 300, message = "{validation.name.max.length}")
    private String name;

    @Pattern(regexp = Constants.Regex.ONE_TO_TEN_DIGITS, message = "{validation.code.max.digits}")
    private String code;

    @Pattern(regexp = Constants.Regex.PHONE, message = "{validation.phone.valid}")
    private String phone;

    @Pattern(regexp = Constants.Regex.COUNTRY_CODE, message = "{validation.country.code.valid}")
    private String countryCode;

    private Integer roleId;
}
