package com.eden.eden_crm_sec_crm_back.enums;

public enum PatrolFrequencyEnum {
    ONCE("once"),
    EVERY_PERIOD("every-period");
    private String freq;

    PatrolFrequencyEnum(String freq) {
        this.freq = freq;
    }

    public String getFreq() {
        return freq;
    }
}
