
package org.thingsboard.server.service.edge.rpc.processor.alarm;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.edge.EdgeEvent;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.gen.edge.v1.AlarmCommentUpdateMsg;
import org.thingsboard.server.gen.edge.v1.AlarmUpdateMsg;
import org.thingsboard.server.gen.edge.v1.DownlinkMsg;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.service.edge.rpc.processor.EdgeProcessor;

public interface AlarmProcessor extends EdgeProcessor {

    ListenableFuture<Void> processAlarmMsgFromEdge(TenantId tenantId, EdgeId edgeId, AlarmUpdateMsg alarmUpdateMsg);

    DownlinkMsg convertAlarmEventToDownlink(EdgeEvent edgeEvent, EdgeVersion edgeVersion);

    ListenableFuture<Void> processAlarmCommentMsgFromEdge(TenantId tenantId, EdgeId edgeId, AlarmCommentUpdateMsg alarmCommentUpdateMsg);

    DownlinkMsg convertAlarmCommentEventToDownlink(EdgeEvent edgeEvent, EdgeVersion edgeVersion);
}
