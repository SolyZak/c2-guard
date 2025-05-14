package com.eden.eden_crm_sec_crm_back.base.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final HttpStatus httpStatus;
    @Getter
    private String[] params;
    public BusinessException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public BusinessException(String message, HttpStatus httpStatus, String[] params) {
        super(message);
        this.httpStatus = httpStatus;
        this.params = params;
    }

    public BusinessException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }


}
