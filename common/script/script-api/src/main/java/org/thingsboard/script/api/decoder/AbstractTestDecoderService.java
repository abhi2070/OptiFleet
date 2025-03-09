package org.thingsboard.script.api.decoder;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.script.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class AbstractTestDecoderService implements TestDecoderService {

    @Override
    public JsonNode executeScript(String script, JsonNode payload, Map<String, String> metadata) throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("nashorn");

        Bindings bindings = engine.createBindings();

        bindings.put("decodeToString", (Decoder) payloadBytes -> new String(payloadBytes, StandardCharsets.UTF_8));
        bindings.put("decodeToJson", (Decoder) payloadBytes -> {
            String payloadStr = new String(payloadBytes, StandardCharsets.UTF_8);
            return new Gson().fromJson(payloadStr, Map.class);
        });

        bindings.put("payload", payload);
        bindings.put("metadata", metadata);

        JsonNode result = (JsonNode) engine.eval(script, bindings);
        return result;
    }

    @FunctionalInterface
    interface Decoder {
        Object decode(byte[] payloadBytes);
    }
}
