
package org.thingsboard.rule.engine.notification;

import lombok.Data;
import org.thingsboard.rule.engine.api.NodeConfiguration;
import org.thingsboard.server.common.data.notification.targets.slack.SlackConversation;
import org.thingsboard.server.common.data.notification.targets.slack.SlackConversationType;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class TbSlackNodeConfiguration implements NodeConfiguration<TbSlackNodeConfiguration> {

    private String botToken;
    private boolean useSystemSettings;
    @NotEmpty
    private String messageTemplate;

    private SlackConversationType conversationType;
    @NotNull
    @Valid
    private SlackConversation conversation;

    @Override
    public TbSlackNodeConfiguration defaultConfiguration() {
        TbSlackNodeConfiguration config = new TbSlackNodeConfiguration();
        config.setUseSystemSettings(true);
        config.setBotToken("xoxb-");
        config.setMessageTemplate("Device ${deviceId}: temperature is $[temperature]");
        config.setConversationType(SlackConversationType.PUBLIC_CHANNEL);
        return config;
    }

}
