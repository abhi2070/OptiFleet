
package org.thingsboard.monitoring.notification.channels.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.thingsboard.monitoring.notification.channels.NotificationChannel;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;

@Component
@ConditionalOnProperty(value = "monitoring.notifications.slack.enabled", havingValue = "true")
@Slf4j
public class SlackNotificationChannel implements NotificationChannel {

    @Value("${monitoring.notifications.slack.webhook_url}")
    private String webhookUrl;

    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(2))
                .build();
    }

    @Override
    public void sendNotification(String message) {
        restTemplate.postForObject(webhookUrl, Map.of("text", message), String.class);
    }

}
