
package org.thingsboard.rule.engine.math;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.msg.TbMsgMetaData;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TbMathArgumentValueTest {

    @Test
    public void test_fromMessageBody_then_defaultValue() {
        TbMathArgument tbMathArgument = new TbMathArgument(TbMathArgumentType.MESSAGE_BODY, "TestKey");
        tbMathArgument.setDefaultValue(5.0);
        TbMathArgumentValue result = TbMathArgumentValue.fromMessageBody(tbMathArgument, tbMathArgument.getKey(), Optional.ofNullable(JacksonUtil.newObjectNode()));
        Assert.assertEquals(5.0, result.getValue(), 0d);
    }

    @Test
    public void test_fromMessageBody_then_emptyBody() {
        TbMathArgument tbMathArgument = new TbMathArgument(TbMathArgumentType.MESSAGE_BODY, "TestKey");
        Throwable thrown = assertThrows(RuntimeException.class, () -> {
            TbMathArgumentValue result = TbMathArgumentValue.fromMessageBody(tbMathArgument, tbMathArgument.getKey(), Optional.empty());
        });
        Assert.assertNotNull(thrown.getMessage());
    }

    @Test
    public void test_fromMessageBody_then_noKey() {
        TbMathArgument tbMathArgument = new TbMathArgument(TbMathArgumentType.MESSAGE_BODY, "TestKey");
        Throwable thrown = assertThrows(RuntimeException.class, () -> TbMathArgumentValue.fromMessageBody(tbMathArgument, tbMathArgument.getKey(), Optional.ofNullable(JacksonUtil.newObjectNode())));
        Assert.assertNotNull(thrown.getMessage());
    }

    @Test
    public void test_fromMessageBody_then_valueEmpty() {
        TbMathArgument tbMathArgument = new TbMathArgument(TbMathArgumentType.MESSAGE_BODY, "TestKey");
        ObjectNode msgData = JacksonUtil.newObjectNode();
        msgData.putNull("TestKey");

        //null value
        Throwable thrown = assertThrows(RuntimeException.class, () -> TbMathArgumentValue.fromMessageBody(tbMathArgument, tbMathArgument.getKey(), Optional.of(msgData)));
        Assert.assertNotNull(thrown.getMessage());

        //empty value
        msgData.put("TestKey", "");
        thrown = assertThrows(RuntimeException.class, () -> TbMathArgumentValue.fromMessageBody(tbMathArgument, tbMathArgument.getKey(), Optional.of(msgData)));
        Assert.assertNotNull(thrown.getMessage());
    }

    @Test
    public void test_fromMessageBody_then_valueCantConvert_to_double() {
        TbMathArgument tbMathArgument = new TbMathArgument(TbMathArgumentType.MESSAGE_BODY, "TestKey");
        ObjectNode msgData = JacksonUtil.newObjectNode();
        msgData.put("TestKey", "Test");

        //string value
        Throwable thrown = assertThrows(RuntimeException.class, () -> TbMathArgumentValue.fromMessageBody(tbMathArgument, tbMathArgument.getKey(), Optional.of(msgData)));
        Assert.assertNotNull(thrown.getMessage());

        //object value
        msgData.set("TestKey", JacksonUtil.newObjectNode());
        thrown = assertThrows(RuntimeException.class, () -> TbMathArgumentValue.fromMessageBody(tbMathArgument, tbMathArgument.getKey(), Optional.of(msgData)));
        Assert.assertNotNull(thrown.getMessage());
    }

    @Test
    public void test_fromMessageMetadata_then_noKey() {
        TbMathArgument tbMathArgument = new TbMathArgument(TbMathArgumentType.MESSAGE_BODY, "TestKey");
        Throwable thrown = assertThrows(RuntimeException.class, () -> TbMathArgumentValue.fromMessageMetadata(tbMathArgument, tbMathArgument.getKey(), new TbMsgMetaData()));
        Assert.assertNotNull(thrown.getMessage());
    }

    @Test
    public void test_fromMessageMetadata_then_valueEmpty() {
        TbMathArgument tbMathArgument = new TbMathArgument(TbMathArgumentType.MESSAGE_BODY, "TestKey");
        Throwable thrown = assertThrows(RuntimeException.class, () -> TbMathArgumentValue.fromMessageMetadata(tbMathArgument, tbMathArgument.getKey(), null));
        Assert.assertNotNull(thrown.getMessage());
    }

    @Test
    public void test_fromString_thenOK() {
        var value = "5.0";
        TbMathArgumentValue result = TbMathArgumentValue.fromString(value);
        Assert.assertNotNull(result);
        Assert.assertEquals(5.0, result.getValue(), 0d);
    }

    @Test
    public void test_fromString_then_failure() {
        var value = "Test";
        Throwable thrown = assertThrows(RuntimeException.class, () -> TbMathArgumentValue.fromString(value));
        Assert.assertNotNull(thrown.getMessage());
    }
}
