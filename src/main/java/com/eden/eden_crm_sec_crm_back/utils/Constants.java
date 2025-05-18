package com.eden.eden_crm_sec_crm_back.utils;


public class Constants {
    public static final Object COMPLAINT_PATH = "customer/complaints/";
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "20";

    public static final class ErrorKeys {
        public static final String EXCEPTION_RECORD_NOT_FOUND = "EXCEPTION_RECORD_NOT_FOUND";
        public static final String EXCEPTION_NOT_SENT_SUCCESSFULLY = "EXCEPTION_NOT_SENT_SUCCESSFULLY";
        public static final String EXCEPTION_EMAIL_NOT_SENT = "EXCEPTION_EMAIL_NOT_SENT";

    }
    public static final class Regex {
        public static final String ONE_TO_TEN_DIGITS = "^\\d{1,10}$";
        public static final String TEN_DIGITS = "^(\\d{10})?$";
        public static final String PHONE = "^\\d{9,15}$";
        public static final String COUNTRY_CODE = "^(?:\\+|00)[1-9]\\d{0,3}$";
    }
}