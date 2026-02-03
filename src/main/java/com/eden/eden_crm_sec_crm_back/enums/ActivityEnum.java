package com.eden.eden_crm_sec_crm_back.enums;

import lombok.Getter;

@Getter
public enum ActivityEnum {
    ATTENDANCE("ATTENDANCE", "حضور وانصراف", "Attendance and absence"),
    PATROLS("PATROLS", "تسجيل دوريات", "Patrols registration"),
    VISITORS("VISITORS", "تسجيل زوار", "Visitor registration"),
    INCIDENTS("INCIDENTS", "حوادث", "Incidents") ,
    INSTANT_TASKS("INSTANT_TASKS", "تنفيذ مهام لحظيه", "Instant tasks");

    private final String code;
    private final String nameAr;
    private final String nameEn;

    ActivityEnum(String code, String nameAr, String nameEn) {
        this.code = code;
        this.nameAr = nameAr;
        this.nameEn = nameEn;
    }
}
