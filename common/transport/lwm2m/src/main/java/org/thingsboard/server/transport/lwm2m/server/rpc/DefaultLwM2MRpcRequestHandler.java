
package org.thingsboard.server.transport.lwm2m.server.rpc;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.leshan.core.ResponseCode;
import org.eclipse.leshan.core.model.ResourceModel;
import org.eclipse.leshan.core.node.LwM2mPath;
import org.eclipse.leshan.core.request.ContentFormat;
import org.eclipse.leshan.server.model.LwM2mModelProvider;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.transport.TransportService;
import org.thingsboard.server.common.transport.util.JsonUtils;
import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.queue.util.TbLwM2mTransportComponent;
import org.thingsboard.server.transport.lwm2m.server.LwM2MOperationType;
import org.thingsboard.server.transport.lwm2m.server.client.LwM2mClient;
import org.thingsboard.server.transport.lwm2m.server.client.LwM2mClientContext;
import org.thingsboard.server.transport.lwm2m.server.downlink.LwM2mDownlinkMsgHandler;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MCancelAllObserveCallback;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MCancelAllRequest;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MCancelObserveCallback;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MCancelObserveRequest;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MCreateRequest;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MCreateResponseCallback;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MDeleteCallback;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MDeleteRequest;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MDiscoverAllRequest;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MDiscoverCallback;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MDiscoverRequest;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MExecuteCallback;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MExecuteRequest;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MObserveAllRequest;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MObserveCallback;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MObserveRequest;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MReadCallback;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MReadRequest;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MWriteAttributesCallback;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MWriteAttributesRequest;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MWriteReplaceRequest;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MWriteResponseCallback;
import org.thingsboard.server.transport.lwm2m.server.downlink.TbLwM2MWriteUpdateRequest;
import org.thingsboard.server.transport.lwm2m.server.downlink.composite.TbLwM2MReadCompositeCallback;
import org.thingsboard.server.transport.lwm2m.server.downlink.composite.TbLwM2MReadCompositeRequest;
import org.thingsboard.server.transport.lwm2m.server.downlink.composite.TbLwM2MWriteResponseCompositeCallback;
import org.thingsboard.server.transport.lwm2m.server.log.LwM2MTelemetryLogService;
import org.thingsboard.server.transport.lwm2m.server.rpc.composite.RpcReadCompositeRequest;
import org.thingsboard.server.transport.lwm2m.server.rpc.composite.RpcReadResponseCompositeCallback;
import org.thingsboard.server.transport.lwm2m.server.rpc.composite.RpcWriteCompositeRequest;
import org.thingsboard.server.transport.lwm2m.server.uplink.LwM2mUplinkMsgHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.thingsboard.server.transport.lwm2m.utils.LwM2MTransportUtil.convertMultiResourceValuesFromRpcBody;
import static org.thingsboard.server.transport.lwm2m.utils.LwM2MTransportUtil.fromVersionedIdToObjectId;

@Slf4j
@Service
@TbLwM2mTransportComponent
@RequiredArgsConstructor
public class DefaultLwM2MRpcRequestHandler implements LwM2MRpcRequestHandler {

    private final TransportService transportService;
    private final LwM2mClientContext clientContext;
    private final LwM2mUplinkMsgHandler uplinkHandler;
    private final LwM2mDownlinkMsgHandler downlinkHandler;
    private final LwM2MTelemetryLogService logService;
    private final LwM2mModelProvider modelProvider;

    @Override
    public void onToDeviceRpcRequest(TransportProtos.ToDeviceRpcRequestMsg rpcRequest, TransportProtos.SessionInfoProto sessionInfo) {
        log.debug("Received params: {}", rpcRequest.getParams());
        try {
            LwM2MOperationType operationType = LwM2MOperationType.fromType(rpcRequest.getMethodName());
            if (operationType == null) {
                this.sendErrorRpcResponse(sessionInfo, rpcRequest.getRequestId(), ResponseCode.METHOD_NOT_ALLOWED, "Unsupported operation type: " + rpcRequest.getMethodName());
                return;
            }
            LwM2mClient client = clientContext.getClientBySessionInfo(sessionInfo);

            if (client == null) {
                log.warn("Missing client for session: [{}]", sessionInfo);
                return;
            }

            if (client.getRegistration() == null) {
                this.sendErrorRpcResponse(sessionInfo, rpcRequest.getRequestId(), ResponseCode.INTERNAL_SERVER_ERROR, "Registration is empty");
                return;
            }
            UUID rpcId = new UUID(rpcRequest.getRequestIdMSB(), rpcRequest.getRequestIdLSB());

            if (rpcId.equals(client.getLastSentRpcId())) {
                log.debug("[{}]][{}] Rpc has already sent!", client.getEndpoint(), rpcId);
                return;
            }
            try {
                if (operationType.isHasObjectId()) {
                    LwM2MRpcRequestHeader header = JacksonUtil.fromString(rpcRequest.getParams(), LwM2MRpcRequestHeader.class);
                    String objectId = getIdFromParameters(client, header);
                    switch (operationType) {
                        case READ:
                            sendReadRequest(client, rpcRequest, objectId);
                            break;
                        case OBSERVE:
                            sendObserveRequest(client, rpcRequest, objectId);
                            break;
                        case DISCOVER:
                            sendDiscoverRequest(client, rpcRequest, objectId);
                            break;
                        case EXECUTE:
                            sendExecuteRequest(client, rpcRequest, objectId);
                            break;
                        case WRITE_ATTRIBUTES:
                            sendWriteAttributesRequest(client, rpcRequest, objectId);
                            break;
                        case OBSERVE_CANCEL:
                            sendCancelObserveRequest(client, rpcRequest, objectId);
                            break;
                        case DELETE:
                            sendDeleteRequest(client, rpcRequest, objectId);
                            break;
                        case WRITE_UPDATE:
                            sendWriteUpdateRequest(client, rpcRequest, objectId);
                            break;
                        case WRITE_REPLACE:
                            sendWriteReplaceRequest(client, rpcRequest, objectId);
                            break;
                        case CREATE:
                            sendCreateRequest(client, rpcRequest, objectId);
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported operation: " + operationType.name());
                    }
                } else if (operationType.isComposite()) {
                    ContentFormat contentFormatComposite = this.getCompositeContentFormat(client);
                    if (contentFormatComposite != null) {
                        switch (operationType) {
                            case READ_COMPOSITE:
                                sendReadCompositeRequest(client, rpcRequest, contentFormatComposite);
                                break;
                            case WRITE_COMPOSITE:
                                sendWriteCompositeRequest(client, rpcRequest, contentFormatComposite);
                                break;
                            default:
                                throw new IllegalArgumentException("Unsupported operation: " + operationType.name());
                        }
                    } else {
                        this.sendErrorRpcResponse(sessionInfo, rpcRequest.getRequestId(),
                                ResponseCode.INTERNAL_SERVER_ERROR, "This device does not support Composite Operation");
                    }
                } else {
                    switch (operationType) {
                        case OBSERVE_CANCEL_ALL:
                            sendCancelAllObserveRequest(client, rpcRequest);
                            break;
                        case OBSERVE_READ_ALL:
                            sendObserveAllRequest(client, rpcRequest);
                            break;
                        case DISCOVER_ALL:
                            sendDiscoverAllRequest(client, rpcRequest);
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported operation: " + operationType.name());
                    }
                }
            } catch (IllegalArgumentException e) {
                this.sendErrorRpcResponse(sessionInfo, rpcRequest.getRequestId(), ResponseCode.BAD_REQUEST, e.getMessage());
            }
        } catch (Exception e) {
            log.error("[{}] Failed to send RPC: [{}]", sessionInfo, rpcRequest, e);
            this.sendErrorRpcResponse(sessionInfo, rpcRequest.getRequestId(), ResponseCode.INTERNAL_SERVER_ERROR, ExceptionUtils.getRootCauseMessage(e));
        }
    }

    private void sendReadRequest(LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg, String versionedId) {
        TbLwM2MReadRequest request = TbLwM2MReadRequest.builder().versionedId(versionedId).timeout(clientContext.getRequestTimeout(client)).build();
        var mainCallback = new TbLwM2MReadCallback(uplinkHandler, logService, client, versionedId);
        var rpcCallback = new RpcReadResponseCallback<>(transportService, client, requestMsg, mainCallback);
        downlinkHandler.sendReadRequest(client, request, rpcCallback);
    }

    private void sendReadCompositeRequest(LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg, ContentFormat contentFormatComposite) {
        String[] versionedIds = getIdsFromParameters(client, requestMsg);
        TbLwM2MReadCompositeRequest request = TbLwM2MReadCompositeRequest.builder().versionedIds(versionedIds).timeout(clientContext.getRequestTimeout(client)).build();
        var mainCallback = new TbLwM2MReadCompositeCallback(uplinkHandler, logService, client, versionedIds);
        var rpcCallback = new RpcReadResponseCompositeCallback(transportService, client, requestMsg, mainCallback);
        downlinkHandler.sendReadCompositeRequest(client, request, rpcCallback, contentFormatComposite);
    }

    private void sendObserveRequest(LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg, String versionedId) {
        TbLwM2MObserveRequest request = TbLwM2MObserveRequest.builder().versionedId(versionedId).timeout(clientContext.getRequestTimeout(client)).build();
        var mainCallback = new TbLwM2MObserveCallback(uplinkHandler, logService, client, versionedId);
        var rpcCallback = new RpcReadResponseCallback<>(transportService, client, requestMsg, mainCallback);
        downlinkHandler.sendObserveRequest(client, request, rpcCallback);
    }

    private void sendObserveAllRequest(LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg) {
        TbLwM2MObserveAllRequest request = TbLwM2MObserveAllRequest.builder().timeout(clientContext.getRequestTimeout(client)).build();
        downlinkHandler.sendObserveAllRequest(client, request, new RpcLinkSetCallback<>(transportService, client, requestMsg, null));
    }

    private void sendDiscoverAllRequest(LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg) {
        TbLwM2MDiscoverAllRequest request = TbLwM2MDiscoverAllRequest.builder().timeout(clientContext.getRequestTimeout(client)).build();
        downlinkHandler.sendDiscoverAllRequest(client, request, new RpcLinkSetCallback<>(transportService, client, requestMsg, null));
    }

    private void sendDiscoverRequest(LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg, String versionedId) {
        TbLwM2MDiscoverRequest request = TbLwM2MDiscoverRequest.builder().versionedId(versionedId).timeout(clientContext.getRequestTimeout(client)).build();
        var mainCallback = new TbLwM2MDiscoverCallback(logService, client, versionedId);
        var rpcCallback = new RpcDiscoverCallback(transportService, client, requestMsg, mainCallback);
        downlinkHandler.sendDiscoverRequest(client, request, rpcCallback);
    }

    private void sendExecuteRequest(LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg, String versionedId) {
        TbLwM2MExecuteRequest downlink = TbLwM2MExecuteRequest.builder().versionedId(versionedId).timeout(clientContext.getRequestTimeout(client)).build();
        var mainCallback = new TbLwM2MExecuteCallback(logService, client, versionedId);
        var rpcCallback = new RpcEmptyResponseCallback<>(transportService, client, requestMsg, mainCallback);
        downlinkHandler.sendExecuteRequest(client, downlink, rpcCallback);
    }

    private void sendWriteAttributesRequest(LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg, String versionedId) {
        RpcWriteAttributesRequest requestBody = JacksonUtil.fromString(requestMsg.getParams(), RpcWriteAttributesRequest.class);
        TbLwM2MWriteAttributesRequest request = TbLwM2MWriteAttributesRequest.builder().versionedId(versionedId)
                .attributes(requestBody.getAttributes())
                .timeout(clientContext.getRequestTimeout(client)).build();
        var mainCallback = new TbLwM2MWriteAttributesCallback(logService, client, versionedId);
        var rpcCallback = new RpcEmptyResponseCallback<>(transportService, client, requestMsg, mainCallback);
        downlinkHandler.sendWriteAttributesRequest(client, request, rpcCallback);
    }

    private void sendWriteUpdateRequest(LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg, String versionedId) {
        RpcWriteUpdateRequest requestBody = JacksonUtil.fromString(requestMsg.getParams(), RpcWriteUpdateRequest.class);
        TbLwM2MWriteUpdateRequest.TbLwM2MWriteUpdateRequestBuilder builder = TbLwM2MWriteUpdateRequest.builder().versionedId(versionedId);
        builder.value(requestBody.getValue()).timeout(clientContext.getRequestTimeout(client));
        var mainCallback = new TbLwM2MWriteResponseCallback(uplinkHandler, logService, client, versionedId);
        var rpcCallback = new RpcEmptyResponseCallback<>(transportService, client, requestMsg, mainCallback);
        downlinkHandler.sendWriteUpdateRequest(client, builder.build(), rpcCallback);
    }

    private void sendCreateRequest(LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg, String versionedId) {
        RpcCreateRequest requestBody = JacksonUtil.fromString(requestMsg.getParams(), RpcCreateRequest.class);
        TbLwM2MCreateRequest.TbLwM2MCreateRequestBuilder builder = TbLwM2MCreateRequest.builder().versionedId(versionedId);
        builder.value(requestBody.getValue()).nodes(requestBody.getNodes()).timeout(clientContext.getRequestTimeout(client));
        var mainCallback = new TbLwM2MCreateResponseCallback(uplinkHandler, logService, client, versionedId);
        var rpcCallback = new RpcCreateResponseCallback<>(transportService, client, requestMsg, mainCallback);
        downlinkHandler.sendCreateRequest(client, builder.build(), rpcCallback);
    }

    private void sendWriteReplaceRequest(LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg, String versionedId) {
        RpcWriteReplaceRequest requestBody = JacksonUtil.fromString(requestMsg.getParams(), RpcWriteReplaceRequest.class);
        LwM2mPath path = new LwM2mPath(fromVersionedIdToObjectId(versionedId));
        if (path.isResource()) {
            ResourceModel resourceModel = client.getResourceModel(versionedId, modelProvider);
            if (resourceModel != null && resourceModel.multiple) {
                try {
                    Map<Integer, Object> value = convertMultiResourceValuesFromRpcBody(requestBody.getValue(), resourceModel.type, versionedId);
                    requestBody.setValue(value);
                } catch (Exception e) {
                }
            }
        }
        TbLwM2MWriteReplaceRequest request = TbLwM2MWriteReplaceRequest.builder().versionedId(versionedId)
                .value(requestBody.getValue())
                .timeout(clientContext.getRequestTimeout(client)).build();
        var mainCallback = new TbLwM2MWriteResponseCallback(uplinkHandler, logService, client, versionedId);
        var rpcCallback = new RpcEmptyResponseCallback<>(transportService, client, requestMsg, mainCallback);
        downlinkHandler.sendWriteReplaceRequest(client, request, rpcCallback);
    }

    /**
     * WriteComposite {"nodes":{"/3/0/14":"+04", "/1/0/2":100, "/5/0/1":"coap://localhost:5685"}}
     * {"result":"CHANGED"}
     * Map<String, Object> nodes = new HashMap<>();
     * nodes.put("/3/0/14", "+02");
     * nodes.put("/1/0/2", 100);
     * nodes.put("/5/0/1", "coap://localhost:5685");
     */
    private void sendWriteCompositeRequest(LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg, ContentFormat contentFormatComposite) {
        RpcWriteCompositeRequest rpcWriteCompositeRequest = JacksonUtil.fromString(requestMsg.getParams(), RpcWriteCompositeRequest.class);
        Map<String, Object> validNodes = validateNodes(client, rpcWriteCompositeRequest.getNodes());
        if (validNodes.size() > 0) {
            rpcWriteCompositeRequest.setNodes(validNodes);
            var mainCallback = new TbLwM2MWriteResponseCompositeCallback(uplinkHandler, logService, client, null);
            var rpcCallback = new RpcEmptyResponseCallback<>(transportService, client, requestMsg, mainCallback);
            downlinkHandler.sendWriteCompositeRequest(client, rpcWriteCompositeRequest, rpcCallback, contentFormatComposite);
        } else {
            throw new IllegalArgumentException(String.format("nodes: %s is not validate value", rpcWriteCompositeRequest.getNodes().toString()));
        }
    }

    private Map<String, Object> validateNodes(LwM2mClient client, Map<String, Object> nodes) {
        Map<String, Object> newNodes = new LinkedHashMap<>();
        nodes.forEach((key, value) -> {
            String versionedId;
            try {
                LwM2mPath path = new LwM2mPath(fromVersionedIdToObjectId(key));
                if (path.isResource() || path.isResourceInstance()) {
                    versionedId = key;
                }
                else {
                    throw new IllegalArgumentException(String.format("nodes: %s is not validate value. " +
                            "The WriteComposite operation is only used for SingleResources or/and ResourceInstance.", nodes.toString()));
                }
            } catch (Exception e) {
                versionedId = clientContext.getObjectIdByKeyNameFromProfile(client, key);
            }
            // validate value. Must be only primitive, not JsonObject or JsonArray
            try {
                JsonElement element = JsonUtils.parse(value.toString());
                if (!element.isJsonNull() && !element.isJsonPrimitive()) {
                    throw new IllegalArgumentException(String.format("nodes: %s is not validate value. " +
                            "The WriteComposite operation is only used for SingleResources or/and ResourceInstance.", nodes.toString()));
                }
                else if (versionedId != null) {
                    newNodes.put(fromVersionedIdToObjectId(versionedId), value);
                }
            } catch (JsonSyntaxException jse) {
                if (versionedId != null) {
                    newNodes.put(fromVersionedIdToObjectId(versionedId), value);
                }
            }
        });
        return newNodes;
    }

    private void sendCancelObserveRequest(LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg, String versionedId) {
        TbLwM2MCancelObserveRequest downlink = TbLwM2MCancelObserveRequest.builder().versionedId(versionedId).timeout(clientContext.getRequestTimeout(client)).build();
        var mainCallback = new TbLwM2MCancelObserveCallback(logService, client, versionedId);
        var rpcCallback = new RpcCancelObserveCallback(transportService, client, requestMsg, mainCallback);
        downlinkHandler.sendCancelObserveRequest(client, downlink, rpcCallback);
    }

    private void sendDeleteRequest(LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg, String versionedId) {
        TbLwM2MDeleteRequest downlink = TbLwM2MDeleteRequest.builder().versionedId(versionedId).timeout(clientContext.getRequestTimeout(client)).build();
        var mainCallback = new TbLwM2MDeleteCallback(logService, client, versionedId);
        var rpcCallback = new RpcEmptyResponseCallback<>(transportService, client, requestMsg, mainCallback);
        downlinkHandler.sendDeleteRequest(client, downlink, rpcCallback);
    }

    private void sendCancelAllObserveRequest(LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg requestMsg) {
        TbLwM2MCancelAllRequest downlink = TbLwM2MCancelAllRequest.builder().timeout(clientContext.getRequestTimeout(client)).build();
        var mainCallback = new TbLwM2MCancelAllObserveCallback(logService, client);
        var rpcCallback = new RpcCancelAllObserveCallback(transportService, client, requestMsg, mainCallback);
        downlinkHandler.sendCancelAllRequest(client, downlink, rpcCallback);
    }

    private String getIdFromParameters(LwM2mClient client, LwM2MRpcRequestHeader header) {
        String targetId;
        if (StringUtils.isNotEmpty(header.getKey())) {
            targetId = clientContext.getObjectIdByKeyNameFromProfile(client, header.getKey());
        } else if (StringUtils.isNotEmpty(header.getId())) {
            targetId = header.getId();
        } else {
            throw new IllegalArgumentException("Can't find 'key' or 'id' in the requestParams parameters!");
        }
        return targetId;
    }

    private String[] getIdsFromParameters(LwM2mClient client, TransportProtos.ToDeviceRpcRequestMsg rpcRequest) {
        RpcReadCompositeRequest requestParams = JacksonUtil.fromString(rpcRequest.getParams(), RpcReadCompositeRequest.class);
        if (requestParams.getKeys() != null && requestParams.getKeys().length > 0) {
            Set<String> targetIds = ConcurrentHashMap.newKeySet();
            for (String key : requestParams.getKeys()) {
                String targetId = clientContext.getObjectIdByKeyNameFromProfile(client, key);
                if (targetId != null) {
                    targetIds.add(targetId);
                }
            }
            return targetIds.toArray(String[]::new);
        } else if (requestParams.getIds() != null && requestParams.getIds().length > 0) {
            return requestParams.getIds();
        } else {
            throw new IllegalArgumentException("Can't find 'key' or 'id' in the requestParams parameters!");
        }
    }

    private void sendErrorRpcResponse(TransportProtos.SessionInfoProto sessionInfo, int requestId, ResponseCode result, String error) {
        String payload = JacksonUtil.toString(LwM2MRpcResponseBody.builder().result(result.getName()).error(error).build());
        TransportProtos.ToDeviceRpcResponseMsg msg = TransportProtos.ToDeviceRpcResponseMsg.newBuilder().setRequestId(requestId).setError(payload).build();
        transportService.process(sessionInfo, msg, null);
    }

    @Override
    public void onToDeviceRpcResponse(TransportProtos.ToDeviceRpcResponseMsg toDeviceResponse, TransportProtos.SessionInfoProto sessionInfo) {
        log.debug("OnToDeviceRpcResponse: [{}], sessionUUID: [{}]", toDeviceResponse, new UUID(sessionInfo.getSessionIdMSB(), sessionInfo.getSessionIdLSB()));
        transportService.process(sessionInfo, toDeviceResponse, null);
    }

    public void onToServerRpcResponse(TransportProtos.ToServerRpcResponseMsg toServerResponse) {
        log.info("[{}] toServerRpcResponse", toServerResponse);
    }

    private ContentFormat getCompositeContentFormat(LwM2mClient client) {
        if (client.getClientSupportContentFormats().contains(ContentFormat.SENML_JSON)) {
            return ContentFormat.SENML_JSON;
        }
        else if (client.getClientSupportContentFormats().contains(ContentFormat.SENML_CBOR)) {
            return ContentFormat.SENML_CBOR;
        }
        else {
            return null;
        }
    }
}
