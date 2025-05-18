package com.eden.eden_crm_sec_crm_back.enums;

import lombok.Getter;

@Getter
public enum UnitEnum {

    PERSON("PERSON", "فرد", "Person"),
    PRODUCT("PRODUCT", "منتج", "Product");

    private final String code;
    private final String nameAr;
    private final String nameEn;

    UnitEnum(String code, String nameAr, String nameEn) {
        this.code = code;
        this.nameAr = nameAr;
        this.nameEn = nameEn;
    }

}
