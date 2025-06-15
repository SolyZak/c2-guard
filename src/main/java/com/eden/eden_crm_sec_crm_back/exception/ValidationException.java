package com.eden.eden_crm_sec_crm_back.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ValidationException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String attribute;

    public ValidationException(String attribute, String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.attribute = attribute;
    }

}
