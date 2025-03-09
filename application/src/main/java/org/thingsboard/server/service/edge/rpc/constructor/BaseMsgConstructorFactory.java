
package org.thingsboard.server.service.edge.rpc.constructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public abstract class BaseMsgConstructorFactory<T extends MsgConstructor, U extends MsgConstructor> {

    @Autowired
    protected T v1Constructor;

    @Autowired
    protected U v2Constructor;

    public MsgConstructor getMsgConstructorByEdgeVersion(EdgeVersion edgeVersion) {
        switch (edgeVersion) {
            case V_3_3_0:
            case V_3_3_3:
            case V_3_4_0:
            case V_3_6_0:
            case V_3_6_1:
                return v1Constructor;
            case V_3_6_2:
            default:
                return v2Constructor;
        }
    }
}
