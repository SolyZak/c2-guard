package com.eden.eden_crm_sec_crm_back.exception;

import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import org.springframework.http.HttpStatus;

public class PremiseNotProvided extends BusinessException{
    public PremiseNotProvided() {
        super(MessageUtil.getMessage("validation.missing.premise.id"), HttpStatus.NOT_FOUND);
    }
}
