package com.eden.eden_crm_sec_crm_back.enums;

import lombok.Getter;

@Getter
public enum CurrencyEnum {

    USD("USD", "دولار أمريكي", "USD"),
    SAR("SAR", "ريال سعودي", "SAR"),
    EGP("EGP", "جنيه مصري", "EGP");

    private final String code;
    private final String nameAr;
    private final String nameEn;

    CurrencyEnum(String code, String nameAr, String nameEn) {
        this.code = code;
        this.nameAr = nameAr;
        this.nameEn = nameEn;
    }
}
