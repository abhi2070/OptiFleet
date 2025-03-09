
package org.thingsboard.server.queue.util;

import org.thingsboard.server.common.data.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PropertyUtils {

    public static Map<String, String> getProps(String properties) {
        Map<String, String> configs = new HashMap<>();
        if (StringUtils.isNotEmpty(properties)) {
            for (String property : properties.split(";")) {
                if (StringUtils.isNotEmpty(property)) {
                    int delimiterPosition = property.indexOf(":");
                    String key = property.substring(0, delimiterPosition);
                    String value = property.substring(delimiterPosition + 1);
                    configs.put(key, value);
                }
            }
        }
        return configs;
    }

    public static Map<String, String> getProps(Map<String, String> defaultProperties, String propertiesStr) {
        return getProps(defaultProperties, propertiesStr, PropertyUtils::getProps);
    }

    public static Map<String, String> getProps(Map<String, String> defaultProperties, String propertiesStr, Function<String, Map<String, String>> parser) {
        Map<String, String> properties = defaultProperties;
        if (StringUtils.isNotBlank(propertiesStr)) {
            properties = new HashMap<>(properties);
            properties.putAll(parser.apply(propertiesStr));
        }
        return properties;
    }

}
