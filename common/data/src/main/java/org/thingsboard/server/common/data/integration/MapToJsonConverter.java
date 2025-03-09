package org.thingsboard.server.common.data.integration;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class MapToJsonConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Error converting Map to JSON", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String s) {
        try {
            return objectMapper.readValue(s, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to Map", e);
        }
    }
}
