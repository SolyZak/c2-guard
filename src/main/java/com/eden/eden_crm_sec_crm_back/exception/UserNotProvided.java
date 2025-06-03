package com.eden.eden_crm_sec_crm_back.exception;

import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import org.springframework.http.HttpStatus;

public class UserNotProvided extends BusinessException {
    public UserNotProvided() {
        super(MessageUtil.getMessage("login-back-again"), HttpStatus.UNAUTHORIZED);
    }
}
