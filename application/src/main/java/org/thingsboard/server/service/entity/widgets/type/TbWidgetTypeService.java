
package org.thingsboard.server.service.entity.widgets.type;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.service.entity.SimpleTbEntityService;

public interface TbWidgetTypeService extends SimpleTbEntityService<WidgetTypeDetails> {

    WidgetTypeDetails save(WidgetTypeDetails widgetTypeDetails, boolean updateExistingByFqn, User user) throws Exception;

}
