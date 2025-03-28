
package org.thingsboard.server.dao.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.settings.UserSettings;
import org.thingsboard.server.common.data.settings.UserSettingsCompositeKey;
import org.thingsboard.server.common.data.settings.UserSettingsType;
import org.thingsboard.server.dao.entity.AbstractCachedService;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.service.ConstraintValidator;

import java.util.Iterator;
import java.util.List;

import static org.thingsboard.server.dao.service.Validator.validateId;

@Service("UserSettingsDaoService")
@Slf4j
@RequiredArgsConstructor
public class UserSettingsServiceImpl extends AbstractCachedService<UserSettingsCompositeKey, UserSettings, UserSettingsEvictEvent> implements UserSettingsService {
    public static final String INCORRECT_USER_ID = "Incorrect userId ";
    private final UserSettingsDao userSettingsDao;

    @Override
    public UserSettings saveUserSettings(TenantId tenantId, UserSettings userSettings) {
        log.trace("Executing saveUserSettings for user [{}], [{}]", userSettings.getUserId(), userSettings);
        validateId(userSettings.getUserId(), INCORRECT_USER_ID + userSettings.getUserId());
        return doSaveUserSettings(tenantId, userSettings);
    }

    @Override
    public void updateUserSettings(TenantId tenantId, UserId userId, UserSettingsType type, JsonNode settings) {
        log.trace("Executing updateUserSettings for user [{}], [{}]", userId, settings);
        validateId(userId, INCORRECT_USER_ID + userId);

        var key = new UserSettingsCompositeKey(userId.getId(), type.name());
        UserSettings oldSettings = userSettingsDao.findById(tenantId, key);
        JsonNode oldSettingsJson = oldSettings != null ? oldSettings.getSettings() : JacksonUtil.newObjectNode();

        UserSettings newUserSettings = new UserSettings();
        newUserSettings.setUserId(userId);
        newUserSettings.setType(type);
        newUserSettings.setSettings(update(oldSettingsJson, settings));
        doSaveUserSettings(tenantId, newUserSettings);
    }

    @Override
    public UserSettings findUserSettings(TenantId tenantId, UserId userId, UserSettingsType type) {
        log.trace("Executing findUserSettings for user [{}]", userId);
        validateId(userId, INCORRECT_USER_ID + userId);

        var key = new UserSettingsCompositeKey(userId.getId(), type.name());
        return cache.getAndPutInTransaction(key,
                () -> userSettingsDao.findById(tenantId, key), true);
    }

    @Override
    public void deleteUserSettings(TenantId tenantId, UserId userId, UserSettingsType type, List<String> jsonPaths) {
        log.trace("Executing deleteUserSettings for user [{}]", userId);
        validateId(userId, INCORRECT_USER_ID + userId);
        var key = new UserSettingsCompositeKey(userId.getId(), type.name());
        UserSettings userSettings = userSettingsDao.findById(tenantId, key);
        if (userSettings == null) {
            return;
        }
        try {
            DocumentContext dcSettings = JsonPath.parse(userSettings.getSettings().toString());
            for (String s : jsonPaths) {
                dcSettings = dcSettings.delete("$." + s);
            }
            userSettings.setSettings(JacksonUtil.fromString(dcSettings.jsonString(), ObjectNode.class));
        } catch (Exception t) {
            handleEvictEvent(new UserSettingsEvictEvent(key));
            throw new RuntimeException(t);
        }
        doSaveUserSettings(tenantId, userSettings);
    }

    private UserSettings doSaveUserSettings(TenantId tenantId, UserSettings userSettings) {
        try {
            ConstraintValidator.validateFields(userSettings);
            validateJsonKeys(userSettings.getSettings());
            UserSettings saved = userSettingsDao.save(tenantId, userSettings);
            publishEvictEvent(new UserSettingsEvictEvent(new UserSettingsCompositeKey(userSettings)));
            return saved;
        } catch (Exception t) {
            handleEvictEvent(new UserSettingsEvictEvent(new UserSettingsCompositeKey(userSettings)));
            throw t;
        }
    }

    @TransactionalEventListener(classes = UserSettingsEvictEvent.class)
    @Override
    public void handleEvictEvent(UserSettingsEvictEvent event) {
        cache.evict(event.getKey());
    }

    private void validateJsonKeys(JsonNode userSettings) {
        Iterator<String> fieldNames = userSettings.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if (fieldName.contains(".") || fieldName.contains(",")) {
                throw new DataValidationException("Json field name should not contain \".\" or \",\" symbols");
            }
        }
    }

    public JsonNode update(JsonNode mainNode, JsonNode updateNode) {
        Iterator<String> fieldNames = updateNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldExpression = fieldNames.next();
            String[] fieldPath = fieldExpression.trim().split("\\.");
            var node = (ObjectNode) mainNode;
            for (int i = 0; i < fieldPath.length; i++) {
                var fieldName = fieldPath[i];
                var last = i == (fieldPath.length - 1);
                if (last) {
                    node.set(fieldName, updateNode.get(fieldExpression));
                } else {
                    if (!node.has(fieldName)) {
                        node.set(fieldName, JacksonUtil.newObjectNode());
                    }
                    node = (ObjectNode) node.get(fieldName);
                }
            }
        }
        return mainNode;
    }

}
