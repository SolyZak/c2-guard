package com.eden.eden_crm_sec_crm_back.consumer;

import com.eden.eden_crm_sec_crm_back.dto.TriggerEventDto;
import com.eden.eden_crm_sec_crm_back.entity.CrmTriggerLog;
import com.eden.eden_crm_sec_crm_back.service.impl.C2AlertEventService;
import com.eden.eden_crm_sec_crm_back.service.impl.CrmTriggerLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TriggerEventConsumer {
    private final CrmTriggerLogService crmTriggerLogService;
    private final C2AlertEventService c2AlertEventService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @KafkaListener(topics = "crm_topic", groupId = "crm-group")
    public void consume(ConsumerRecord<String, String> triggerEvent) throws JsonProcessingException {
        log.info("[TriggerConsumer] RAW KAFKA PAYLOAD topic={} partition={} offset={} value={}",
                triggerEvent.topic(), triggerEvent.partition(), triggerEvent.offset(), triggerEvent.value());
        TriggerEventDto triggerEventDto = objectMapper.readValue(triggerEvent.value(), TriggerEventDto.class);
        log.info("[TriggerConsumer] DESERIALIZED triggerId={} eventDate={} eventTime={} (offset={}) operationSiteId={} customerId={}",
                triggerEventDto.getTriggerId(),
                triggerEventDto.getEventDate(),
                triggerEventDto.getEventTime(),
                triggerEventDto.getEventTime() != null ? triggerEventDto.getEventTime().getOffset() : null,
                triggerEventDto.getOperationSiteId(),
                triggerEventDto.getCustomerId());
        final CrmTriggerLog crmTriggerLog = crmTriggerLogService.addNewCrmTriggerLog(triggerEventDto);
        log.info("[TriggerConsumer] SAVED CrmTriggerLog id={} eventDate={} eventTime={} (offset={}) -> publishing C2 alert event",
                crmTriggerLog.getId(),
                crmTriggerLog.getEventDate(),
                crmTriggerLog.getEventTime(),
                crmTriggerLog.getEventTime() != null ? crmTriggerLog.getEventTime().getOffset() : null);
        c2AlertEventService.sendNewC2AlertEvent(crmTriggerLog);
    }
}

