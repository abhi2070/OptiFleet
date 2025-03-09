
package org.thingsboard.server.common.data.notification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.thingsboard.server.common.data.notification.targets.NotificationRecipient;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class NotificationRequestStats {

    private final Map<NotificationDeliveryMethod, AtomicInteger> sent;
    private final Map<NotificationDeliveryMethod, Map<String, String>> errors;
    private String error;
    @JsonIgnore
    private final Map<NotificationDeliveryMethod, Set<Object>> processedRecipients;

    public NotificationRequestStats() {
        this.sent = new ConcurrentHashMap<>();
        this.errors = new ConcurrentHashMap<>();
        this.processedRecipients = new ConcurrentHashMap<>();
    }

    @JsonCreator
    public NotificationRequestStats(@JsonProperty("sent") Map<NotificationDeliveryMethod, AtomicInteger> sent,
                                    @JsonProperty("errors") Map<NotificationDeliveryMethod, Map<String, String>> errors,
                                    @JsonProperty("error") String error) {
        this.sent = sent;
        this.errors = errors;
        this.error = error;
        this.processedRecipients = Collections.emptyMap();
    }

    public void reportSent(NotificationDeliveryMethod deliveryMethod, NotificationRecipient recipient) {
        sent.computeIfAbsent(deliveryMethod, k -> new AtomicInteger()).incrementAndGet();
    }

    public void reportError(NotificationDeliveryMethod deliveryMethod, Throwable error, NotificationRecipient recipient) {
        if (error instanceof AlreadySentException) {
            return;
        }
        String errorMessage = error.getMessage();
        if (errorMessage == null) {
            errorMessage = error.getClass().getSimpleName();
        }
        errors.computeIfAbsent(deliveryMethod, k -> new ConcurrentHashMap<>()).put(recipient.getTitle(), errorMessage);
    }

    public void reportProcessed(NotificationDeliveryMethod deliveryMethod, Object recipientId) {
        processedRecipients.computeIfAbsent(deliveryMethod, k -> ConcurrentHashMap.newKeySet()).add(recipientId);
    }

    public boolean contains(NotificationDeliveryMethod deliveryMethod, Object recipientId) {
        Set<Object> processedRecipients = this.processedRecipients.get(deliveryMethod);
        return processedRecipients != null && processedRecipients.contains(recipientId);
    }

}
