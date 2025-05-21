package com.eden.eden_crm_sec_crm_back.enums;

public enum PresenceMode {
    CHECK_IN_ONLY("CHECK_IN_ONLY", "تسجيل الدخول فقط", "Check-in only"), CHECK_OUT_ONLY("CHECK_OUT_ONLY", "تسجيل الخروج فقط", "Check-out only"), CHECK_IN_AND_OUT("CHECK_IN_AND_OUT", "تسجيل الدخول والخروج", "Check-in and check-out");
    private final String code;
    private final String nameAr;
    private final String nameEn;

    PresenceMode(String code, String nameAr, String nameEn) {
        this.code = code;
        this.nameAr = nameAr;
        this.nameEn = nameEn;
    }
}
