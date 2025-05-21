package com.eden.eden_crm_sec_crm_back.enums;

import lombok.Getter;

@Getter
public enum WeekDaysEnum {
    SUNDAY("SUNDAY", "الأحد", "Sunday"),
    MONDAY("MONDAY", "الإثنين", "Monday"),
    TUESDAY("TUESDAY", "الثلاثاء", "Tuesday"),
    WEDNESDAY("WEDNESDAY", "الأربعاء", "Wednesday"),
    THURSDAY("THURSDAY", "الخميس", "Thursday"),
    FRIDAY("FRIDAY", "الجمعة", "Friday"),
    SATURDAY("SATURDAY", "السبت", "Saturday");
    private final String code;
    private final String nameAr;
    private final String nameEn;

    WeekDaysEnum(String code, String nameAr, String nameEn) {
        this.code = code;
        this.nameAr = nameAr;
        this.nameEn = nameEn;
    }
}
