
package org.thingsboard.server.queue.provider;

import org.thingsboard.server.gen.js.JsInvokeProtos;
import org.thingsboard.server.gen.transport.TransportProtos.ToCoreMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToCoreNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToOtaPackageStateServiceMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToRuleEngineMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToRuleEngineNotificationMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToTransportMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToUsageStatsServiceMsg;
import org.thingsboard.server.gen.transport.TransportProtos.ToVersionControlServiceMsg;
import org.thingsboard.server.gen.transport.TransportProtos.TransportApiRequestMsg;
import org.thingsboard.server.gen.transport.TransportProtos.TransportApiResponseMsg;
import org.thingsboard.server.queue.TbQueueConsumer;
import org.thingsboard.server.queue.TbQueueProducer;
import org.thingsboard.server.queue.TbQueueRequestTemplate;
import org.thingsboard.server.queue.common.TbProtoJsQueueMsg;
import org.thingsboard.server.queue.common.TbProtoQueueMsg;

/**
 * Responsible for initialization of various Producers and Consumers used by TB Core Node.
 * Implementation Depends on the queue queue.type from yml or TB_QUEUE_TYPE environment variable
 */
public interface TbCoreQueueFactory extends TbUsageStatsClientQueueFactory {

    /**
     * Used to push messages to instances of TB Transport Service
     *
     * @return
     */
    TbQueueProducer<TbProtoQueueMsg<ToTransportMsg>> createTransportNotificationsMsgProducer();

    /**
     * Used to push messages to instances of TB RuleEngine Service
     *
     * @return
     */
    TbQueueProducer<TbProtoQueueMsg<ToRuleEngineMsg>> createRuleEngineMsgProducer();

    /**
     * Used to push notifications to instances of TB RuleEngine Service
     *
     * @return
     */
    TbQueueProducer<TbProtoQueueMsg<ToRuleEngineNotificationMsg>> createRuleEngineNotificationsMsgProducer();

    /**
     * Used to push messages to other instances of TB Core Service
     *
     * @return
     */
    TbQueueProducer<TbProtoQueueMsg<ToCoreMsg>> createTbCoreMsgProducer();

    /**
     * Used to push notifications to other instances of TB Core Service
     *
     * @return
     */
    TbQueueProducer<TbProtoQueueMsg<ToCoreNotificationMsg>> createTbCoreNotificationsMsgProducer();

    /**
     * Used to consume messages by TB Core Service
     *
     * @return
     */
    TbQueueConsumer<TbProtoQueueMsg<ToCoreMsg>> createToCoreMsgConsumer();

    /**
     * Used to consume messages about usage statistics by TB Core Service
     *
     * @return
     */
    TbQueueConsumer<TbProtoQueueMsg<ToUsageStatsServiceMsg>> createToUsageStatsServiceMsgConsumer();

    /**
     * Used to consume messages about firmware update notifications by TB Core Service
     *
     * @return
     */
    TbQueueConsumer<TbProtoQueueMsg<ToOtaPackageStateServiceMsg>> createToOtaPackageStateServiceMsgConsumer();

    /**
     * Used to consume messages about firmware update notifications by TB Core Service
     *
     * @return
     */
    TbQueueProducer<TbProtoQueueMsg<ToOtaPackageStateServiceMsg>> createToOtaPackageStateServiceMsgProducer();

    /**
     * Used to consume high priority messages by TB Core Service
     *
     * @return
     */
    TbQueueConsumer<TbProtoQueueMsg<ToCoreNotificationMsg>> createToCoreNotificationsMsgConsumer();

    /**
     * Used to consume Transport API Calls
     *
     * @return
     */
    TbQueueConsumer<TbProtoQueueMsg<TransportApiRequestMsg>> createTransportApiRequestConsumer();

    /**
     * Used to push replies to Transport API Calls
     *
     * @return
     */
    TbQueueProducer<TbProtoQueueMsg<TransportApiResponseMsg>> createTransportApiResponseProducer();

    TbQueueRequestTemplate<TbProtoJsQueueMsg<JsInvokeProtos.RemoteJsRequest>, TbProtoQueueMsg<JsInvokeProtos.RemoteJsResponse>> createRemoteJsRequestTemplate();

    /**
     * Used to push messages to instances of TB Version Control Service
     *
     * @return
     */
    TbQueueProducer<TbProtoQueueMsg<ToVersionControlServiceMsg>> createVersionControlMsgProducer();
}
