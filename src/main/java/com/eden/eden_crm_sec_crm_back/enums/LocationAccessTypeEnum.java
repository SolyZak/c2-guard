package com.eden.eden_crm_sec_crm_back.enums;

public enum LocationAccessTypeEnum {
    SPECIFIC_POINT("specific-point"),
    QR_CODE("qr-code");
    private String type;

    LocationAccessTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
