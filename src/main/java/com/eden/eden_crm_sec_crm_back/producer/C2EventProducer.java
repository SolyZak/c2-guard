package com.eden.eden_crm_sec_crm_back.producer;

import com.eden.eden_crm_sec_crm_back.dto.C2AlertEventDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class C2EventProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);

    public void publishC2Events(final C2AlertEventDto c2AlertEventDto){
        String c2AlertEvent = null;
        try {
            c2AlertEvent = objectMapper.writeValueAsString(c2AlertEventDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        this.kafkaTemplate.send("c2_topic", c2AlertEvent);
    }
}
