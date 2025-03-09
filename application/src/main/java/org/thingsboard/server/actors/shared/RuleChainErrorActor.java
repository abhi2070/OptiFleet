
package org.thingsboard.server.actors.shared;

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.actors.ActorSystemContext;
import org.thingsboard.server.actors.TbActor;
import org.thingsboard.server.actors.TbActorId;
import org.thingsboard.server.actors.TbStringActorId;
import org.thingsboard.server.actors.service.ContextAwareActor;
import org.thingsboard.server.actors.service.ContextBasedCreator;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.msg.TbActorMsg;
import org.thingsboard.server.common.msg.aware.RuleChainAwareMsg;
import org.thingsboard.server.common.msg.queue.RuleEngineException;

import java.util.UUID;

@Slf4j
public class RuleChainErrorActor extends ContextAwareActor {

    private final TenantId tenantId;
    private final RuleEngineException error;

    private RuleChainErrorActor(ActorSystemContext systemContext, TenantId tenantId, RuleEngineException error) {
        super(systemContext);
        this.tenantId = tenantId;
        this.error = error;
    }

    @Override
    protected boolean doProcess(TbActorMsg msg) {
        if (msg instanceof RuleChainAwareMsg) {
            log.debug("[{}] Reply with {} for message {}", tenantId, error.getMessage(), msg);
            var rcMsg = (RuleChainAwareMsg) msg;
            rcMsg.getMsg().getCallback().onFailure(error);
            return true;
        } else {
            return false;
        }
    }

    public static class ActorCreator extends ContextBasedCreator {

        private final TenantId tenantId;
        private final RuleEngineException error;

        public ActorCreator(ActorSystemContext context, TenantId tenantId, RuleEngineException error) {
            super(context);
            this.tenantId = tenantId;
            this.error = error;
        }

        @Override
        public TbActorId createActorId() {
            return new TbStringActorId(UUID.randomUUID().toString());
        }

        @Override
        public TbActor createActor() {
            return new RuleChainErrorActor(context, tenantId, error);
        }
    }

}
