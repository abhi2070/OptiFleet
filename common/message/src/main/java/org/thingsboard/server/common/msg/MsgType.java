
package org.thingsboard.server.common.msg;

import lombok.Getter;
import org.thingsboard.server.common.msg.queue.PartitionChangeMsg;
import org.thingsboard.server.common.msg.queue.QueueToRuleEngineMsg;

/**
 * Created by ashvayka on 15.03.18.
 */
public enum MsgType {

    /**
     * ADDED/UPDATED/DELETED events for server nodes.
     *
     * See {@link PartitionChangeMsg}
     */
    PARTITION_CHANGE_MSG(true),

    APP_INIT_MSG,

    /**
     * ADDED/UPDATED/DELETED events for main entities.
     *
     * See {@link org.thingsboard.server.common.msg.plugin.ComponentLifecycleMsg}
     */
    COMPONENT_LIFE_CYCLE_MSG,

    /**
     * Special message to indicate rule node update request
     */
    RULE_NODE_UPDATED_MSG,

    /**
     * Misc messages consumed from the Queue and forwarded to Rule Engine Actor.
     *
     * See {@link QueueToRuleEngineMsg}
     */
    QUEUE_TO_RULE_ENGINE_MSG,

    /**
     * Message that is sent by RuleChainActor to RuleActor with command to process TbMsg.
     */
    RULE_CHAIN_TO_RULE_MSG,

    /**
     * Message that is sent by RuleChainActor to other RuleChainActor with command to process TbMsg.
     */
    RULE_CHAIN_TO_RULE_CHAIN_MSG,

    /**
     * Message that is sent by RuleNodeActor as input to other RuleChain with command to process TbMsg.
     */
    RULE_CHAIN_INPUT_MSG,

    /**
     * Message that is sent by RuleNodeActor as output to RuleNode in other RuleChain with command to process TbMsg.
     */
    RULE_CHAIN_OUTPUT_MSG,

    /**
     * Message that is sent by RuleActor to RuleChainActor with command to process TbMsg by next nodes in chain.
     */
    RULE_TO_RULE_CHAIN_TELL_NEXT_MSG,

    /**
     * Message forwarded from original rule chain to remote rule chain due to change in the cluster structure or originator entity of the TbMsg.
     */
    REMOTE_TO_RULE_CHAIN_TELL_NEXT_MSG,

    /**
     * Message that is sent by RuleActor implementation to RuleActor itself to process the message.
     */
    RULE_TO_SELF_MSG,

    DEVICE_ATTRIBUTES_UPDATE_TO_DEVICE_ACTOR_MSG,

    DEVICE_CREDENTIALS_UPDATE_TO_DEVICE_ACTOR_MSG,

    DEVICE_NAME_OR_TYPE_UPDATE_TO_DEVICE_ACTOR_MSG,

    DEVICE_DELETE_TO_DEVICE_ACTOR_MSG,

    DEVICE_EDGE_UPDATE_TO_DEVICE_ACTOR_MSG,

    DEVICE_RPC_REQUEST_TO_DEVICE_ACTOR_MSG,

    DEVICE_RPC_RESPONSE_TO_DEVICE_ACTOR_MSG,

    SERVER_RPC_RESPONSE_TO_DEVICE_ACTOR_MSG,

    DEVICE_ACTOR_SERVER_SIDE_RPC_TIMEOUT_MSG,

    REMOVE_RPC_TO_DEVICE_ACTOR_MSG,

    /**
     * Message that is sent from the Device Actor to Rule Engine. Requires acknowledgement
     */

    SESSION_TIMEOUT_MSG(true),

    STATS_PERSIST_TICK_MSG,

    STATS_PERSIST_MSG,

    /**
     * Message that is sent by TransportRuleEngineService to Device Actor. Represents messages from the device itself.
     */
    TRANSPORT_TO_DEVICE_ACTOR_MSG,

    /**
     * Message that is sent on Edge Event to Edge Session
     */
    EDGE_EVENT_UPDATE_TO_EDGE_SESSION_MSG,

    /**
     * Messages that are sent to and from edge session to start edge synchronization process
     */
    EDGE_SYNC_REQUEST_TO_EDGE_SESSION_MSG,
    EDGE_SYNC_RESPONSE_FROM_EDGE_SESSION_MSG;

    @Getter
    private final boolean ignoreOnStart;

    MsgType() {
        this.ignoreOnStart = false;
    }

    MsgType(boolean ignoreOnStart) {
        this.ignoreOnStart = ignoreOnStart;
    }
}
