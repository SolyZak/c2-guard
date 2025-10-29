package com.eden.eden_crm_sec_crm_back.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Converter
public class MapJsonConverter implements AttributeConverter<Map<Long, List<Long>>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<Long, List<Long>> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting map to JSON string", e);
        }
    }

    @Override
    public Map<Long, List<Long>> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();

            // Construct proper JavaType for Map<Long, List<Long>>
            JavaType keyType = typeFactory.constructType(Long.class);
            JavaType valueType = typeFactory.constructCollectionType(List.class, Long.class);
            JavaType mapType = typeFactory.constructMapType(Map.class, keyType, valueType);

            return objectMapper.readValue(dbData, mapType);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading JSON string to map", e);
        }
    }
}
