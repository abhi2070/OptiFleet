
package org.thingsboard.server.dao.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.thingsboard.server.dao.dashboard.DashboardServiceImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Data
public class JsonPathProcessingTask {
    private final String[] tokens;
    private final Map<String, String> variables;
    private final JsonNode node;

    public JsonPathProcessingTask(String[] tokens, Map<String, String> variables, JsonNode node) {
        this.tokens = tokens;
        this.variables = variables;
        this.node = node;
    }

    public boolean isLast() {
        return tokens.length == 1;
    }

    public String currentToken() {
        return tokens[0];
    }

    public JsonPathProcessingTask next(JsonNode next) {
        return new JsonPathProcessingTask(
                Arrays.copyOfRange(tokens, 1, tokens.length),
                variables,
                next);
    }

    public JsonPathProcessingTask next(JsonNode next, String key, String value) {
        Map<String, String> variables = new HashMap<>(this.variables);
        variables.put(key, value);
        return new JsonPathProcessingTask(
                Arrays.copyOfRange(tokens, 1, tokens.length),
                variables,
                next);
    }

    @Override
    public String toString() {
        return "JsonPathProcessingTask{" +
                "tokens=" + Arrays.toString(tokens) +
                ", variables=" + variables +
                ", node=" + node.toString().substring(0, 20) +
                '}';
    }
}
