
package org.thingsboard.server.dao.model.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WidgetsBundleWidgetCompositeKey implements Serializable {

    @Transient
    private static final long serialVersionUID = -245388185894468455L;

    private UUID widgetsBundleId;
    private UUID widgetTypeId;

}
