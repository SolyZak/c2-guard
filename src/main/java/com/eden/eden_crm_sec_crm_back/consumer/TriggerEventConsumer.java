package com.eden.eden_crm_sec_crm_back.consumer;

import com.eden.eden_crm_sec_crm_back.dto.TriggerEventDto;
import com.eden.eden_crm_sec_crm_back.entity.CrmTriggerLog;
import com.eden.eden_crm_sec_crm_back.service.impl.C2AlertEventService;
import com.eden.eden_crm_sec_crm_back.service.impl.CrmTriggerLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TriggerEventConsumer {
    private final CrmTriggerLogService crmTriggerLogService;
    private final C2AlertEventService c2AlertEventService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @KafkaListener(topics = "crm_topic", groupId = "crm-group")
    public void consume(ConsumerRecord<String, String> triggerEvent) throws JsonProcessingException {
        TriggerEventDto triggerEventDto = objectMapper.readValue(triggerEvent.value(), TriggerEventDto.class);
        // save log to db
        final CrmTriggerLog crmTriggerLog = crmTriggerLogService.addNewCrmTriggerLog(triggerEventDto);
        // send new c2 alert event
        c2AlertEventService.sendNewC2AlertEvent(crmTriggerLog);
    }
}

