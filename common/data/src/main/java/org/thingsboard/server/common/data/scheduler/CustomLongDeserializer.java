package org.thingsboard.server.common.data.scheduler;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class CustomLongDeserializer extends JsonDeserializer<Long> {
    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        if (node.isNumber()) {
            return node.asLong();
        } else if (node.isObject() && node.size() == 0) {
            // Handle empty object case, return a default value or throw an exception
            return null; // Or return a default value like 0L
        } else {
            throw new IOException("Expected number but found: " + node.toString());
        }
    }
}