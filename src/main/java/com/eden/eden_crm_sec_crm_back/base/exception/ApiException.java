package com.eden.eden_crm_sec_crm_back.base.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ApiException {
    private String message;
    private String ar_message;
    private HttpStatus httpStatus;
    private String zonedDateTime;
}
