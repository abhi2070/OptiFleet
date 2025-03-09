
package org.thingsboard.server.service.edge.rpc.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.server.gen.edge.v1.EdgeVersion;
import org.thingsboard.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public abstract class BaseEdgeProcessorFactory<T extends EdgeProcessor, U extends EdgeProcessor> {

    @Autowired
    protected T v1Processor;

    @Autowired
    protected U v2Processor;

    public EdgeProcessor getProcessorByEdgeVersion(EdgeVersion edgeVersion) {
        switch (edgeVersion) {
            case V_3_3_0:
            case V_3_3_3:
            case V_3_4_0:
            case V_3_6_0:
            case V_3_6_1:
                return v1Processor;
            case V_3_6_2:
            default:
                return v2Processor;
        }
    }
}
