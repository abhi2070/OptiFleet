
package org.thingsboard.server.transport.lwm2m.client;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.client.servers.ServerIdentity;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.thingsboard.common.util.ThingsBoardThreadFactory;

import javax.security.auth.Destroyable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class FwLwM2MDevice extends BaseInstanceEnabler implements Destroyable {

    private static final List<Integer> supportedResources = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 9);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(ThingsBoardThreadFactory.forName(getClass().getSimpleName() + "-test-scope"));

    private final AtomicInteger state = new AtomicInteger(0);

    private final AtomicInteger updateResult = new AtomicInteger(0);

    @Override
    public ReadResponse read(ServerIdentity identity, int resourceId) {
        if (!identity.isSystem())
            log.info("Read on Device resource /{}/{}/{}", getModel().id, getId(), resourceId);
        switch (resourceId) {
            case 3:
                return ReadResponse.success(resourceId, getState());
            case 5:
                return ReadResponse.success(resourceId, getUpdateResult());
            case 6:
                return ReadResponse.success(resourceId, getPkgName());
            case 7:
                return ReadResponse.success(resourceId, getPkgVersion());
            case 9:
                return ReadResponse.success(resourceId, getFirmwareUpdateDeliveryMethod());
            default:
                return super.read(identity, resourceId);
        }
    }

    @Override
    public ExecuteResponse execute(ServerIdentity identity, int resourceId, String params) {
        String withParams = null;
        if (params != null && params.length() != 0) {
            withParams = " with params " + params;
        }
        log.info("Execute on Device resource /{}/{}/{} {}", getModel().id, getId(), resourceId, withParams != null ? withParams : "");

        switch (resourceId) {
            case 2:
                startUpdating();
                return ExecuteResponse.success();
            default:
                return super.execute(identity, resourceId, params);
        }
    }

    @Override
    public WriteResponse write(ServerIdentity identity, boolean replace, int resourceId, LwM2mResource value) {
        log.info("Write on Device resource /{}/{}/{}", getModel().id, getId(), resourceId);

        switch (resourceId) {
            case 0:
                startDownloading();
                return WriteResponse.success();
            case 1:
                startDownloading();
                return WriteResponse.success();
            default:
                return super.write(identity, replace, resourceId, value);
        }
    }

    private int getState() {
        return state.get();
    }

    private int getUpdateResult() {
        return updateResult.get();
    }

    private String getPkgName() {
        return "firmware";
    }

    private String getPkgVersion() {
        return "1.0.0";
    }

    private int getFirmwareUpdateDeliveryMethod() {
        return 1;
    }

    @Override
    public List<Integer> getAvailableResourceIds(ObjectModel model) {
        return supportedResources;
    }

    @Override
    public void destroy() {
        scheduler.shutdownNow();
    }

    private void startDownloading() {
        scheduler.schedule(() -> {
            try {
                state.set(1);
                fireResourceChange(3);
                Thread.sleep(100);
                state.set(2);
                fireResourceChange(3);
            } catch (Exception e) {
            }
        }, 100, TimeUnit.MILLISECONDS);
    }

    private void startUpdating() {
        scheduler.schedule(() -> {
            try {
                state.set(3);
                fireResourceChange(3);
                Thread.sleep(100);
                updateResult.set(1);
                fireResourceChange(5);
            } catch (Exception e) {
            }
        }, 100, TimeUnit.MILLISECONDS);
    }

}
