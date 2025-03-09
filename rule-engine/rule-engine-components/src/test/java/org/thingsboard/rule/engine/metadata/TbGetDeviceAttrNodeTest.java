
package org.thingsboard.rule.engine.metadata;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.util.TbPair;

public class TbGetDeviceAttrNodeTest {

    @Test
    public void givenOldConfig_whenUpgrade_thenShouldReturnTrueResultWithNewConfig() throws Exception {
        var defaultConfig = new TbGetDeviceAttrNodeConfiguration().defaultConfiguration();
        var node = new TbGetDeviceAttrNode();
        String oldConfig = "{\"fetchToData\":false," +
                "\"clientAttributeNames\":[]," +
                "\"sharedAttributeNames\":[]," +
                "\"serverAttributeNames\":[]," +
                "\"latestTsKeyNames\":[]," +
                "\"tellFailureIfAbsent\":true," +
                "\"getLatestValueWithTs\":false," +
                "\"deviceRelationsQuery\":{\"direction\":\"FROM\",\"maxLevel\":1,\"relationType\":\"Contains\",\"deviceTypes\":[\"default\"]," +
                "\"fetchLastLevelOnly\":false}}";
        JsonNode configJson = JacksonUtil.toJsonNode(oldConfig);
        TbPair<Boolean, JsonNode> upgrade = node.upgrade(0, configJson);
        Assertions.assertTrue(upgrade.getFirst());
        Assertions.assertEquals(defaultConfig, JacksonUtil.treeToValue(upgrade.getSecond(), defaultConfig.getClass()));
    }

    @Test
    public void givenOldConfigWithNoFetchToDataProperty_whenUpgrade_thenShouldReturnTrueResultWithNewConfig() throws Exception {
        var defaultConfig = new TbGetDeviceAttrNodeConfiguration().defaultConfiguration();
        var node = new TbGetDeviceAttrNode();
        String oldConfig = "{\"clientAttributeNames\":[]," +
                "\"sharedAttributeNames\":[]," +
                "\"serverAttributeNames\":[]," +
                "\"latestTsKeyNames\":[]," +
                "\"tellFailureIfAbsent\":true," +
                "\"getLatestValueWithTs\":false," +
                "\"deviceRelationsQuery\":{\"direction\":\"FROM\",\"maxLevel\":1,\"relationType\":\"Contains\",\"deviceTypes\":[\"default\"]," +
                "\"fetchLastLevelOnly\":false}}";
        JsonNode configJson = JacksonUtil.toJsonNode(oldConfig);
        TbPair<Boolean, JsonNode> upgrade = node.upgrade(0, configJson);
        Assertions.assertTrue(upgrade.getFirst());
        Assertions.assertEquals(defaultConfig, JacksonUtil.treeToValue(upgrade.getSecond(), defaultConfig.getClass()));
    }

    @Test
    public void givenOldConfigWithNullFetchToDataProperty_whenUpgrade_thenShouldReturnTrueResultWithNewConfig() throws Exception {
        var defaultConfig = new TbGetDeviceAttrNodeConfiguration().defaultConfiguration();
        var node = new TbGetDeviceAttrNode();
        String oldConfig = "{\"fetchToData\":null," +
                "\"clientAttributeNames\":[]," +
                "\"sharedAttributeNames\":[]," +
                "\"serverAttributeNames\":[]," +
                "\"latestTsKeyNames\":[]," +
                "\"tellFailureIfAbsent\":true," +
                "\"getLatestValueWithTs\":false," +
                "\"deviceRelationsQuery\":{\"direction\":\"FROM\",\"maxLevel\":1,\"relationType\":\"Contains\",\"deviceTypes\":[\"default\"]," +
                "\"fetchLastLevelOnly\":false}}";
        JsonNode configJson = JacksonUtil.toJsonNode(oldConfig);
        TbPair<Boolean, JsonNode> upgrade = node.upgrade(0, configJson);
        Assertions.assertTrue(upgrade.getFirst());
        Assertions.assertEquals(defaultConfig, JacksonUtil.treeToValue(upgrade.getSecond(), defaultConfig.getClass()));
    }

}
