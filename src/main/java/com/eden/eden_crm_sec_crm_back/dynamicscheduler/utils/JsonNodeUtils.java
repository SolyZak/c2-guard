package com.eden.eden_crm_sec_crm_back.dynamicscheduler.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Optional;

public class JsonNodeUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonNodeUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ObjectNode createObjectNode() {
        return objectMapper.createObjectNode();
    }

    public static <T> T readArguments(JsonNode args, Class<T> clazz) {
        if (args == null || args.isNull())
            return null;
        try {
            return objectMapper.treeToValue(args, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse task arguments", e);
        }
    }

    public static Optional<String> getStringArg(JsonNode args, String fieldName) {
        if (args == null) return Optional.empty();
        JsonNode node = args.get(fieldName);
        return node != null && node.isTextual() ? Optional.of(node.asText()) : Optional.empty();
    }

    public static Optional<Integer> getIntegerArg(JsonNode args, String fieldName) {
        if (args == null) return Optional.empty();
        JsonNode node = args.get(fieldName);
        return node != null && node.isInt() ? Optional.of(node.asInt()) : Optional.empty();
    }

    public static Optional<Long> getLongArg(JsonNode args, String fieldName) {
        if (args == null) return Optional.empty();
        JsonNode node = args.get(fieldName);
        return node != null && node.isLong() ? Optional.of(node.asLong()) : Optional.empty();
    }
}
