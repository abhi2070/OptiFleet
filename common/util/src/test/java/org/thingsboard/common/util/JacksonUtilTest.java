
package org.thingsboard.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;
import org.thingsboard.server.common.data.asset.Asset;
import org.thingsboard.server.common.data.id.AssetId;

import java.util.UUID;

public class JacksonUtilTest {

    @Test
    public void allowUnquotedFieldMapperTest() {
        String data = "{data: 123}";
        JsonNode actualResult = JacksonUtil.toJsonNode(data, JacksonUtil.ALLOW_UNQUOTED_FIELD_NAMES_MAPPER); // should be: {"data": 123}
        ObjectNode expectedResult = JacksonUtil.newObjectNode();
        expectedResult.put("data", 123); // {"data": 123}
        Assert.assertEquals(expectedResult, actualResult);
        Assert.assertThrows(IllegalArgumentException.class, () -> JacksonUtil.toJsonNode(data)); // syntax exception due to missing quotes in the field name!
    }

    @Test
    public void failOnUnknownPropertiesMapperTest() {
        Asset asset = new Asset();
        asset.setId(new AssetId(UUID.randomUUID()));
        asset.setName("Test");
        asset.setType("type");
        String serializedAsset = JacksonUtil.toString(asset);
        JsonNode jsonNode = JacksonUtil.toJsonNode(serializedAsset);
        // case: add new field to serialized Asset string and check for backward compatibility with original Asset object
        Assert.assertNotNull(jsonNode);
        ((ObjectNode) jsonNode).put("test", (String) null);
        serializedAsset = JacksonUtil.toString(jsonNode);
        // deserialize with FAIL_ON_UNKNOWN_PROPERTIES = false
        Asset result = JacksonUtil.fromString(serializedAsset, Asset.class, true);
        Assert.assertNotNull(result);
        Assert.assertEquals(asset.getId(), result.getId());
        Assert.assertEquals(asset.getName(), result.getName());
        Assert.assertEquals(asset.getType(), result.getType());
    }
}
