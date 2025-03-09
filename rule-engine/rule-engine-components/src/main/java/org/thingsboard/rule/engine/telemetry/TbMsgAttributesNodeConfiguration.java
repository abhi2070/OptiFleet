
package org.thingsboard.rule.engine.telemetry;

import lombok.Data;
import org.thingsboard.rule.engine.api.NodeConfiguration;
import org.thingsboard.server.common.data.DataConstants;

@Data
public class TbMsgAttributesNodeConfiguration implements NodeConfiguration<TbMsgAttributesNodeConfiguration> {

    private String scope;

    private boolean notifyDevice;
    private boolean sendAttributesUpdatedNotification;
    private boolean updateAttributesOnlyOnValueChange;

    @Override
    public TbMsgAttributesNodeConfiguration defaultConfiguration() {
        TbMsgAttributesNodeConfiguration configuration = new TbMsgAttributesNodeConfiguration();
        configuration.setScope(DataConstants.SERVER_SCOPE);
        configuration.setNotifyDevice(false);
        configuration.setSendAttributesUpdatedNotification(false);
        // Since version 1. For an existing rule nodes for version 0. See the TbNode implementation
        configuration.setUpdateAttributesOnlyOnValueChange(true);
        return configuration;
    }
}
