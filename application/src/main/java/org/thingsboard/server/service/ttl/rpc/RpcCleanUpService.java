
package org.thingsboard.server.service.ttl.rpc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import org.thingsboard.server.common.msg.queue.ServiceType;
import org.thingsboard.server.dao.rpc.RpcDao;
import org.thingsboard.server.dao.tenant.TbTenantProfileCache;
import org.thingsboard.server.dao.tenant.TenantService;
import org.thingsboard.server.queue.discovery.PartitionService;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@TbCoreComponent
@Service
@Slf4j
@RequiredArgsConstructor
public class RpcCleanUpService {
    @Value("${sql.ttl.rpc.enabled}")
    private boolean ttlTaskExecutionEnabled;

    private final TenantService tenantService;
    private final PartitionService partitionService;
    private final TbTenantProfileCache tenantProfileCache;
    private final RpcDao rpcDao;

    @Scheduled(initialDelayString = "#{T(org.apache.commons.lang3.RandomUtils).nextLong(0, ${sql.ttl.rpc.checking_interval})}", fixedDelayString = "${sql.ttl.rpc.checking_interval}")
    public void cleanUp() {
        if (ttlTaskExecutionEnabled) {
            PageLink tenantsBatchRequest = new PageLink(10_000, 0);
            PageData<TenantId> tenantsIds;
            do {
                tenantsIds = tenantService.findTenantsIds(tenantsBatchRequest);
                for (TenantId tenantId : tenantsIds.getData()) {
                    if (!partitionService.resolve(ServiceType.TB_CORE, tenantId, tenantId).isMyPartition()) {
                        continue;
                    }

                    Optional<DefaultTenantProfileConfiguration> tenantProfileConfiguration = tenantProfileCache.get(tenantId).getProfileConfiguration();
                    if (tenantProfileConfiguration.isEmpty() || tenantProfileConfiguration.get().getRpcTtlDays() == 0) {
                        continue;
                    }

                    long ttl = TimeUnit.DAYS.toMillis(tenantProfileConfiguration.get().getRpcTtlDays());
                    long expirationTime = System.currentTimeMillis() - ttl;

                    long totalRemoved = rpcDao.deleteOutdatedRpcByTenantId(tenantId, expirationTime);

                    if (totalRemoved > 0) {
                        log.info("Removed {} outdated rpc(s) for tenant {} older than {}", totalRemoved, tenantId, new Date(expirationTime));
                    }
                }

                tenantsBatchRequest = tenantsBatchRequest.nextPageLink();
            } while (tenantsIds.hasNext());
        }
    }

}
