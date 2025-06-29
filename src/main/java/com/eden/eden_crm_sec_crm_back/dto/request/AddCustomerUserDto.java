package com.eden.eden_crm_sec_crm_back.dto.request;

import com.eden.eden_crm_sec_crm_back.utils.Constants;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddCustomerUserDto {
    @NotBlank(message = "{validation.name.not.empty}")
    @NotNull(message = "{validation.name.not.empty}")
    @Size(max = 300, message = "{validation.name.max.length}")
    private String name;

    @NotBlank(message = "{validation.code.not.empty}")
    @NotNull(message = "{validation.code.not.empty}")
    @Pattern(regexp = Constants.Regex.ONE_TO_TEN_DIGITS, message = "{validation.code.max.digits}")
    private String code;

    @NotBlank(message = "{validation.email.not.empty}")
    @Email(message = "{validation.email.valid}")
    @Size(max = 300, message = "{validation.email.max.length}")
    private String email;

    @NotBlank(message = "{validation.phone.valid}")
    @Pattern(regexp = Constants.Regex.PHONE, message = "{validation.phone.valid}")
    private String phone;

    @Pattern(regexp = Constants.Regex.COUNTRY_CODE, message = "{validation.country.code.valid}")
    @NotBlank(message = "{validation.country.code.valid}")
    private String countryCode;

    @NotBlank(message = "{validation.password.not-empty}")
    @NotNull(message = "{validation.password.not-empty}")
    String password;
}
