
package org.thingsboard.server.service.ttl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.queue.ServiceType;
import org.thingsboard.server.queue.discovery.PartitionService;


@Slf4j
@RequiredArgsConstructor
public abstract class AbstractCleanUpService {

    private final PartitionService partitionService;

    protected boolean isSystemTenantPartitionMine(){
        return partitionService.resolve(ServiceType.TB_CORE, TenantId.SYS_TENANT_ID, TenantId.SYS_TENANT_ID).isMyPartition();
    }
}
