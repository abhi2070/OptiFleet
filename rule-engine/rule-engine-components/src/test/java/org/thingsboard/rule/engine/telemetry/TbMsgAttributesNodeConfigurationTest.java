
package org.thingsboard.rule.engine.telemetry;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TbMsgAttributesNodeConfigurationTest {

    @Test
    void testDefaultConfig_givenUpdateAttributesOnlyOnValueChange_thenTrue_sinceVersion1() {
        assertThat(new TbMsgAttributesNodeConfiguration().defaultConfiguration().isUpdateAttributesOnlyOnValueChange()).isTrue();
    }

}
