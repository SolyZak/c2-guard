package com.eden.eden_crm_sec_crm_back.enums;

public enum PatrolFrequencyRateEnum {
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly");

    private String rate;

    PatrolFrequencyRateEnum(String rate) {
        this.rate = rate;
    }

    public String getRate() {
        return rate;
    }
}
