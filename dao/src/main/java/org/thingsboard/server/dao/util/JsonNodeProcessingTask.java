
package org.thingsboard.server.dao.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class JsonNodeProcessingTask {
    private final String path;
    private final JsonNode node;

    public JsonNodeProcessingTask(String path, JsonNode node) {
        this.path = path;
        this.node = node;
    }
}
