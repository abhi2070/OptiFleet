
package org.thingsboard.server.service.edge.rpc.constructor.settings;

import org.thingsboard.server.common.data.AdminSettings;
import org.thingsboard.server.gen.edge.v1.AdminSettingsUpdateMsg;
import org.thingsboard.server.service.edge.rpc.constructor.MsgConstructor;

public interface AdminSettingsMsgConstructor extends MsgConstructor {

    AdminSettingsUpdateMsg constructAdminSettingsUpdateMsg(AdminSettings adminSettings);
}
