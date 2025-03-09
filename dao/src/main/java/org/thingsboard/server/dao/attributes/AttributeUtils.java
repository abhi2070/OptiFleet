
package org.thingsboard.server.dao.attributes;

import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.dao.exception.IncorrectParameterException;
import org.thingsboard.server.dao.service.Validator;
import org.thingsboard.server.dao.util.KvUtils;

import java.util.List;

public class AttributeUtils {

    public static void validate(EntityId id, String scope) {
        Validator.validateId(id.getId(), "Incorrect id " + id);
        Validator.validateString(scope, "Incorrect scope " + scope);
    }

    public static void validate(List<AttributeKvEntry> kvEntries,  boolean valueNoXssValidation) {
        kvEntries.forEach(tsKvEntry -> validate(tsKvEntry, valueNoXssValidation));
    }

    public static void validate(AttributeKvEntry kvEntry, boolean valueNoXssValidation) {
        KvUtils.validate(kvEntry, valueNoXssValidation);
        if (kvEntry.getDataType() == null) {
            throw new IncorrectParameterException("Incorrect kvEntry. Data type can't be null");
        } else {
            Validator.validateString(kvEntry.getKey(), "Incorrect kvEntry. Key can't be empty");
            Validator.validatePositiveNumber(kvEntry.getLastUpdateTs(), "Incorrect last update ts. Ts should be positive");
        }
    }
}
