
package org.thingsboard.rule.engine.util;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.EntityFieldsData;
import org.thingsboard.server.common.data.id.AlarmId;
import org.thingsboard.server.common.data.id.AssetId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.EntityViewId;
import org.thingsboard.server.common.data.id.RuleChainId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UUIDBased;
import org.thingsboard.server.common.data.id.UserId;

import java.util.NoSuchElementException;
import java.util.function.Function;

public class EntitiesFieldsAsyncLoader {

    public static ListenableFuture<EntityFieldsData> findAsync(TbContext ctx, EntityId originatorId) {
        switch (originatorId.getEntityType()) {  // TODO: use EntityServiceRegistry
            case TENANT:
                return toEntityFieldsDataAsync(ctx.getTenantService().findTenantByIdAsync(ctx.getTenantId(), (TenantId) originatorId),
                        EntityFieldsData::new, ctx);
            case CUSTOMER:
                return toEntityFieldsDataAsync(ctx.getCustomerService().findCustomerByIdAsync(ctx.getTenantId(), (CustomerId) originatorId),
                        EntityFieldsData::new, ctx);
            case USER:
                return toEntityFieldsDataAsync(ctx.getUserService().findUserByIdAsync(ctx.getTenantId(), (UserId) originatorId),
                        EntityFieldsData::new, ctx);
            case ASSET:
                return toEntityFieldsDataAsync(ctx.getAssetService().findAssetByIdAsync(ctx.getTenantId(), (AssetId) originatorId),
                        EntityFieldsData::new, ctx);
            case DEVICE:
                return toEntityFieldsDataAsync(Futures.immediateFuture(ctx.getDeviceService().findDeviceById(ctx.getTenantId(), (DeviceId) originatorId)),
                        EntityFieldsData::new, ctx);
            case ALARM:
                return toEntityFieldsDataAsync(ctx.getAlarmService().findAlarmByIdAsync(ctx.getTenantId(), (AlarmId) originatorId),
                        EntityFieldsData::new, ctx);
            case RULE_CHAIN:
                return toEntityFieldsDataAsync(ctx.getRuleChainService().findRuleChainByIdAsync(ctx.getTenantId(), (RuleChainId) originatorId),
                        EntityFieldsData::new, ctx);
            case ENTITY_VIEW:
                return toEntityFieldsDataAsync(ctx.getEntityViewService().findEntityViewByIdAsync(ctx.getTenantId(), (EntityViewId) originatorId),
                        EntityFieldsData::new, ctx);
            case EDGE:
                return toEntityFieldsDataAsync(ctx.getEdgeService().findEdgeByIdAsync(ctx.getTenantId(), (EdgeId) originatorId),
                        EntityFieldsData::new, ctx);
            default:
                return Futures.immediateFailedFuture(new TbNodeException("Unexpected originator EntityType: " + originatorId.getEntityType()));
        }
    }

    private static <T extends BaseData<? extends UUIDBased>> ListenableFuture<EntityFieldsData> toEntityFieldsDataAsync(
            ListenableFuture<T> future,
            Function<T, EntityFieldsData> converter,
            TbContext ctx
    ) {
        return Futures.transformAsync(future, in -> in != null ?
                Futures.immediateFuture(converter.apply(in))
                : Futures.immediateFailedFuture(new NoSuchElementException("Entity not found!")), ctx.getDbCallbackExecutor());
    }

}
