
package org.thingsboard.monitoring.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.thingsboard.monitoring.client.TbClient;
import org.thingsboard.monitoring.client.WsClient;
import org.thingsboard.monitoring.config.MonitoringConfig;
import org.thingsboard.monitoring.config.MonitoringTarget;
import org.thingsboard.monitoring.data.Latencies;
import org.thingsboard.monitoring.data.MonitoredServiceKey;
import org.thingsboard.monitoring.data.ServiceFailureException;
import org.thingsboard.monitoring.util.TbStopWatch;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public abstract class BaseHealthChecker<C extends MonitoringConfig, T extends MonitoringTarget> {

    @Getter
    protected final C config;
    @Getter
    protected final T target;

    private Object info;

    @Autowired
    private MonitoringReporter reporter;
    @Autowired
    private TbStopWatch stopWatch;
    @Value("${monitoring.check_timeout_ms}")
    private int resultCheckTimeoutMs;

    @Getter
    private final Map<String, BaseHealthChecker<C, T>> associates = new HashMap<>();

    public static final String TEST_TELEMETRY_KEY = "testData";

    @PostConstruct
    private void init() {
        info = getInfo();
    }

    protected abstract void initialize(TbClient tbClient);

    public final void check(WsClient wsClient) {
        log.debug("[{}] Checking", info);
        try {
            wsClient.registerWaitForUpdate();

            String testValue = UUID.randomUUID().toString();
            String testPayload = createTestPayload(testValue);
            try {
                initClient();
                stopWatch.start();
                sendTestPayload(testPayload);
                reporter.reportLatency(Latencies.request(getKey()), stopWatch.getTime());
                log.trace("[{}] Sent test payload ({})", info, testPayload);
            } catch (Throwable e) {
                throw new ServiceFailureException(e);
            }

            log.trace("[{}] Waiting for WS update", info);
            checkWsUpdate(wsClient, testValue);

            reporter.serviceIsOk(info);
            reporter.serviceIsOk(MonitoredServiceKey.GENERAL);
        } catch (ServiceFailureException serviceFailureException) {
            reporter.serviceFailure(info, serviceFailureException);
        } catch (Exception e) {
            reporter.serviceFailure(MonitoredServiceKey.GENERAL, e);
        }

        associates.values().forEach(healthChecker -> {
            healthChecker.check(wsClient);
        });
    }

    private void checkWsUpdate(WsClient wsClient, String testValue) {
        stopWatch.start();
        wsClient.waitForUpdate(resultCheckTimeoutMs);
        log.trace("[{}] Waited for WS update. Last WS msg: {}", info, wsClient.lastMsg);
        Object update = wsClient.getTelemetryUpdate(target.getDeviceId(), TEST_TELEMETRY_KEY);
        if (update == null) {
            throw new ServiceFailureException("No WS update arrived within " + resultCheckTimeoutMs + " ms");
        } else if (!update.toString().equals(testValue)) {
            throw new ServiceFailureException("Was expecting value " + testValue + " but got " + update);
        }
        reporter.reportLatency(Latencies.wsUpdate(getKey()), stopWatch.getTime());
    }

    protected abstract void initClient() throws Exception;

    protected abstract String createTestPayload(String testValue);

    protected abstract void sendTestPayload(String payload) throws Exception;

    @PreDestroy
    protected abstract void destroyClient() throws Exception;

    protected abstract Object getInfo();
    protected abstract String getKey();

}
