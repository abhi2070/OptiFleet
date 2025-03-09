
package org.thingsboard.server.service.edge.rpc.constructor.resource;

import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.TbResource;
import org.thingsboard.server.gen.edge.v1.ResourceUpdateMsg;
import org.thingsboard.server.gen.edge.v1.UpdateMsgType;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class ResourceMsgConstructorV2 extends BaseResourceMsgConstructor {

    @Override
    public ResourceUpdateMsg constructResourceUpdatedMsg(UpdateMsgType msgType, TbResource tbResource) {
        return ResourceUpdateMsg.newBuilder().setMsgType(msgType).setEntity(JacksonUtil.toString(tbResource))
                .setIdMSB(tbResource.getId().getId().getMostSignificantBits())
                .setIdLSB(tbResource.getId().getId().getLeastSignificantBits()).build();
    }
}
