package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.TriggerRequest;
import com.eden.eden_crm_sec_crm_back.dto.TriggerResponse;
import com.eden.eden_crm_sec_crm_back.entity.Trigger;
import com.eden.eden_crm_sec_crm_back.repository.TriggerRepository;
import com.eden.eden_crm_sec_crm_back.service.TriggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TriggerServiceImpl implements TriggerService {
    private final TriggerRepository triggerRepository;

    public List<TriggerResponse> getAllTriggers() {
        return TriggerResponse.of(triggerRepository.findAll());
    }

    public TriggerResponse getTriggerById(final Long triggerId) {
        Trigger trigger = triggerRepository.findById(triggerId)
                .orElseThrow(() -> new RuntimeException("Trigger not found"));
        return TriggerResponse.of(trigger);
    }

    public TriggerResponse addNewTrigger(final TriggerRequest triggerRequest) {
        Trigger trigger = new Trigger();
        trigger.setName(triggerRequest.getName());
        trigger.setCreationType(triggerRequest.getCreationType());
        return TriggerResponse.of(triggerRepository.save(trigger));
    }

    public void deleteTrigger(final Long triggerId) {
        triggerRepository.deleteById(triggerId);
    }

    public TriggerResponse updateTrigger(final Long triggerId, final TriggerRequest triggerRequest) {
        Trigger trigger = triggerRepository.findById(triggerId)
                .orElseThrow(() -> new RuntimeException("Trigger not found"));
        trigger.setName(triggerRequest.getName());
        trigger.setCreationType(triggerRequest.getCreationType());
        return TriggerResponse.of(triggerRepository.save(trigger));
    }
}
