
package org.thingsboard.server.controller;

import com.google.common.util.concurrent.FutureCallback;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.server.ResponseStatusException;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.RpcId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.msg.TbMsgType;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.rpc.Rpc;
import org.thingsboard.server.common.data.rpc.RpcStatus;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;
import org.thingsboard.server.exception.ToErrorResponseEntity;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.common.msg.rpc.RemoveRpcActorMsg;
import org.thingsboard.server.service.security.permission.Operation;

import javax.annotation.Nullable;
import java.util.UUID;

import static org.thingsboard.server.controller.ControllerConstants.DEVICE_ID;
import static org.thingsboard.server.controller.ControllerConstants.DEVICE_ID_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.MARKDOWN_CODE_BLOCK_END;
import static org.thingsboard.server.controller.ControllerConstants.MARKDOWN_CODE_BLOCK_START;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_NUMBER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.PAGE_SIZE_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.RPC_ID;
import static org.thingsboard.server.controller.ControllerConstants.RPC_ID_PARAM_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.RPC_SORT_PROPERTY_ALLOWABLE_VALUES;
import static org.thingsboard.server.controller.ControllerConstants.RPC_STATUS_ALLOWABLE_VALUES;
import static org.thingsboard.server.controller.ControllerConstants.RPC_TEXT_SEARCH_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_ALLOWABLE_VALUES;
import static org.thingsboard.server.controller.ControllerConstants.SORT_ORDER_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.SORT_PROPERTY_DESCRIPTION;
import static org.thingsboard.server.controller.ControllerConstants.TENANT_AUTHORITY_PARAGRAPH;
import static org.thingsboard.server.controller.ControllerConstants.TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH;

@RestController
@TbCoreComponent
@RequestMapping(TbUrlConstants.RPC_V2_URL_PREFIX)
@Slf4j
public class RpcV2Controller extends AbstractRpcController {

    private static final String RPC_REQUEST_DESCRIPTION = "Sends the one-way remote-procedure call (RPC) request to device. " +
            "The RPC call is A JSON that contains the method name ('method'), parameters ('params') and multiple optional fields. " +
            "See example below. We will review the properties of the RPC call one-by-one below. " +
            "\n\n" + MARKDOWN_CODE_BLOCK_START +
            "{\n" +
            "  \"method\": \"setGpio\",\n" +
            "  \"params\": {\n" +
            "    \"pin\": 7,\n" +
            "    \"value\": 1\n" +
            "  },\n" +
            "  \"persistent\": false,\n" +
            "  \"timeout\": 5000\n" +
            "}" +
            MARKDOWN_CODE_BLOCK_END +
            "\n\n### Server-side RPC structure\n" +
            "\n" +
            "The body of server-side RPC request consists of multiple fields:\n" +
            "\n" +
            "* **method** - mandatory, name of the method to distinct the RPC calls.\n" +
            "  For example, \"getCurrentTime\" or \"getWeatherForecast\". The value of the parameter is a string.\n" +
            "* **params** - mandatory, parameters used for processing of the request. The value is a JSON. Leave empty JSON \"{}\" if no parameters needed.\n" +
            "* **timeout** - optional, value of the processing timeout in milliseconds. The default value is 10000 (10 seconds). The minimum value is 5000 (5 seconds).\n" +
            "* **expirationTime** - optional, value of the epoch time (in milliseconds, UTC timezone). Overrides **timeout** if present.\n" +
            "* **persistent** - optional, indicates persistent RPC. The default value is \"false\".\n" +
            "* **retries** - optional, defines how many times persistent RPC will be re-sent in case of failures on the network and/or device side.\n" +
            "* **additionalInfo** - optional, defines metadata for the persistent RPC that will be added to the persistent RPC events.";

    private static final String ONE_WAY_RPC_RESULT = "\n\n### RPC Result\n" +
            "In case of persistent RPC, the result of this call is 'rpcId' UUID. In case of lightweight RPC, " +
            "the result of this call is either 200 OK if the message was sent to device, or 504 Gateway Timeout if device is offline.";

    private static final String TWO_WAY_RPC_RESULT = "\n\n### RPC Result\n" +
            "In case of persistent RPC, the result of this call is 'rpcId' UUID. In case of lightweight RPC, " +
            "the result of this call is the response from device, or 504 Gateway Timeout if device is offline.";

    private static final String ONE_WAY_RPC_REQUEST_DESCRIPTION = "Sends the one-way remote-procedure call (RPC) request to device. " + RPC_REQUEST_DESCRIPTION + ONE_WAY_RPC_RESULT + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH;

    private static final String TWO_WAY_RPC_REQUEST_DESCRIPTION = "Sends the two-way remote-procedure call (RPC) request to device. " + RPC_REQUEST_DESCRIPTION + TWO_WAY_RPC_RESULT + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH;

    @ApiOperation(value = "Send one-way RPC request", notes = ONE_WAY_RPC_REQUEST_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Persistent RPC request was saved to the database or lightweight RPC request was sent to the device."),
            @ApiResponse(code = 400, message = "Invalid structure of the request."),
            @ApiResponse(code = 401, message = "User is not authorized to send the RPC request. Most likely, User belongs to different Customer or Tenant."),
            @ApiResponse(code = 504, message = "Timeout to process the RPC call. Most likely, device is offline."),
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/oneway/{deviceId}", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<ResponseEntity> handleOneWayDeviceRPCRequest(
            @ApiParam(value = DEVICE_ID_PARAM_DESCRIPTION)
            @PathVariable("deviceId") String deviceIdStr,
            @ApiParam(value = "A JSON value representing the RPC request.")
            @RequestBody String requestBody) throws ThingsboardException {
        return handleDeviceRPCRequest(true, new DeviceId(UUID.fromString(deviceIdStr)), requestBody, HttpStatus.GATEWAY_TIMEOUT, HttpStatus.GATEWAY_TIMEOUT);
    }

    @ApiOperation(value = "Send two-way RPC request", notes = TWO_WAY_RPC_REQUEST_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Persistent RPC request was saved to the database or lightweight RPC response received."),
            @ApiResponse(code = 400, message = "Invalid structure of the request."),
            @ApiResponse(code = 401, message = "User is not authorized to send the RPC request. Most likely, User belongs to different Customer or Tenant."),
            @ApiResponse(code = 504, message = "Timeout to process the RPC call. Most likely, device is offline."),
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/twoway/{deviceId}", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<ResponseEntity> handleTwoWayDeviceRPCRequest(
            @ApiParam(value = DEVICE_ID_PARAM_DESCRIPTION)
            @PathVariable(DEVICE_ID) String deviceIdStr,
            @ApiParam(value = "A JSON value representing the RPC request.")
            @RequestBody String requestBody) throws ThingsboardException {
        return handleDeviceRPCRequest(false, new DeviceId(UUID.fromString(deviceIdStr)), requestBody, HttpStatus.GATEWAY_TIMEOUT, HttpStatus.GATEWAY_TIMEOUT);
    }

    @ApiOperation(value = "Get persistent RPC request", notes = "Get information about the status of the RPC call." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/persistent/{rpcId}", method = RequestMethod.GET)
    @ResponseBody
    public Rpc getPersistedRpc(
            @ApiParam(value = RPC_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable(RPC_ID) String strRpc) throws ThingsboardException {
        checkParameter("RpcId", strRpc);
        RpcId rpcId = new RpcId(UUID.fromString(strRpc));
        return checkRpcId(rpcId, Operation.READ);
    }

    @ApiOperation(value = "Get persistent RPC requests", notes = "Allows to query RPC calls for specific device using pagination." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/persistent/device/{deviceId}", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<ResponseEntity> getPersistedRpcByDevice(
            @ApiParam(value = DEVICE_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable(DEVICE_ID) String strDeviceId,
            @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @ApiParam(value = "Status of the RPC", allowableValues = RPC_STATUS_ALLOWABLE_VALUES)
            @RequestParam(required = false) RpcStatus rpcStatus,
            @ApiParam(value = RPC_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = RPC_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        checkParameter("DeviceId", strDeviceId);
        if (rpcStatus != null && rpcStatus.equals(RpcStatus.DELETED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RpcStatus: DELETED");
        }

        TenantId tenantId = getCurrentUser().getTenantId();
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        DeviceId deviceId = new DeviceId(UUID.fromString(strDeviceId));
        final DeferredResult<ResponseEntity> response = new DeferredResult<>();

        accessValidator.validate(getCurrentUser(), Operation.RPC_CALL, deviceId, new HttpValidationCallback(response, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable DeferredResult<ResponseEntity> result) {
                PageData<Rpc> rpcCalls;
                if (rpcStatus != null) {
                    rpcCalls = rpcService.findAllByDeviceIdAndStatus(tenantId, deviceId, rpcStatus, pageLink);
                } else {
                    rpcCalls = rpcService.findAllByDeviceId(tenantId, deviceId, pageLink);
                }
                response.setResult(new ResponseEntity<>(rpcCalls, HttpStatus.OK));
            }

            @Override
            public void onFailure(Throwable e) {
                ResponseEntity entity;
                if (e instanceof ToErrorResponseEntity) {
                    entity = ((ToErrorResponseEntity) e).toErrorResponseEntity();
                } else {
                    entity = new ResponseEntity(HttpStatus.UNAUTHORIZED);
                }
                response.setResult(entity);
            }
        }));
        return response;
    }

    @ApiOperation(value = "Delete persistent RPC", notes = "Deletes the persistent RPC request." + TENANT_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/persistent/{rpcId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteRpc(
            @ApiParam(value = RPC_ID_PARAM_DESCRIPTION, required = true)
            @PathVariable(RPC_ID) String strRpc) throws ThingsboardException {
        checkParameter("RpcId", strRpc);
        RpcId rpcId = new RpcId(UUID.fromString(strRpc));
        Rpc rpc = checkRpcId(rpcId, Operation.DELETE);

        if (rpc != null) {
            if (rpc.getStatus().isPushDeleteNotificationToCore()) {
                RemoveRpcActorMsg removeMsg = new RemoveRpcActorMsg(getTenantId(), rpc.getDeviceId(), rpc.getUuidId());
                log.trace("[{}] Forwarding msg {} to queue actor!", rpc.getDeviceId(), rpc);
                tbClusterService.pushMsgToCore(removeMsg, null);
            }

            rpcService.deleteRpc(getTenantId(), rpcId);
            rpc.setStatus(RpcStatus.DELETED);

            TbMsg msg = TbMsg.newMsg(TbMsgType.RPC_DELETED, rpc.getDeviceId(), TbMsgMetaData.EMPTY, JacksonUtil.toString(rpc));
            tbClusterService.pushMsgToRuleEngine(getTenantId(), rpc.getDeviceId(), msg, null);
        }
    }
}
