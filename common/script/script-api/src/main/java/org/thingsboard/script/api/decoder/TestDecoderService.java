package org.thingsboard.script.api.decoder;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;

public interface TestDecoderService {
    JsonNode executeScript(String script, JsonNode payload, Map<String, String> metadata) throws Exception;
}
