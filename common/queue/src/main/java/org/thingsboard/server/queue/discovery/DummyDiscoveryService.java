
package org.thingsboard.server.queue.discovery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.util.AfterStartUp;

import java.util.Collections;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "zk", value = "enabled", havingValue = "false", matchIfMissing = true)
@Slf4j
@DependsOn("environmentLogService")
public class DummyDiscoveryService implements DiscoveryService {

    private final TbServiceInfoProvider serviceInfoProvider;
    private final PartitionService partitionService;


    public DummyDiscoveryService(TbServiceInfoProvider serviceInfoProvider, PartitionService partitionService) {
        this.serviceInfoProvider = serviceInfoProvider;
        this.partitionService = partitionService;
    }

    @AfterStartUp(order = AfterStartUp.DISCOVERY_SERVICE)
    public void onApplicationEvent(ApplicationReadyEvent event) {
        partitionService.recalculatePartitions(serviceInfoProvider.getServiceInfo(), Collections.emptyList());
    }

    @Override
    public List<TransportProtos.ServiceInfo> getOtherServers() {
        return Collections.emptyList();
    }

    @Override
    public boolean isMonolith() {
        return true;
    }
}
