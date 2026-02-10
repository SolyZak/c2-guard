package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.TriggerRequest;
import com.eden.eden_crm_sec_crm_back.dto.TriggerResponse;

import java.util.List;

public interface TriggerService {

    List<TriggerResponse> getAllTriggers();

    TriggerResponse getTriggerById(Long triggerId);

    TriggerResponse addNewTrigger(TriggerRequest triggerRequest);

    void deleteTrigger(Long triggerId);

    TriggerResponse updateTrigger(Long triggerId, TriggerRequest triggerRequest);
}
