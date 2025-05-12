package com.eden.eden_crm_sec_crm_back.dto.request;

import com.eden.eden_crm_sec_crm_back.utils.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequestDto(
        @NotBlank(message = "{validation.name.not.empty}")
        @Size(max = 300, message = "{validation.name.max.length}")
        String name,

        @NotBlank(message = "{validation.email.not.empty}")
        @Email(message = "{validation.email.valid}")
        @Size(max = 300, message = "{validation.email.max.length}")
        String email,

        @Pattern(regexp = Constants.Regex.PHONE, message = "{validation.phone.valid}")
        @NotBlank(message = "{validation.phone.valid}")
        String phone,

        @Pattern(regexp = Constants.Regex.COUNTRY_CODE, message = "{validation.country.code.valid}")
        @NotBlank(message = "{validation.country.code.valid}")
        String countryCode,

        @Size(max = 700, message = "{validation.address.max.length}")
        String address,

        @Pattern(regexp = Constants.Regex.TEN_DIGITS, message = "{validation.registration.number.ten.digits.only}")
        @NotBlank(message = "{validation.registration.number.ten.digits.only}")
        String registrationNumber
) {}
