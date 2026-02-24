package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.converter;

import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.checkvalue.TaskCheckValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;

/**
 * JPA AttributeConverter that serializes/deserializes {@link TaskCheckValue}
 * to/from a JSONB string.
 *
 * <p>
 * Jackson uses the {@code "type"} discriminator field (declared via
 * {@code @JsonTypeInfo} on
 * {@link TaskCheckValue}) to automatically select the correct concrete subclass
 * at read time.
 * No {@code instanceof} or {@code switch} is ever needed by callers.
 *
 * <p>
 * To add a new check type, create a new subclass and register it in
 * {@code @JsonSubTypes} on
 * {@link TaskCheckValue} — zero changes required here.
 */
@Converter
public class TaskCheckValueConverter implements AttributeConverter<TaskCheckValue, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(TaskCheckValue attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error serializing TaskCheckValue to JSON", e);
        }
    }

    @Override
    public TaskCheckValue convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(dbData, TaskCheckValue.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error deserializing TaskCheckValue from JSON: " + dbData, e);
        }
    }
}
