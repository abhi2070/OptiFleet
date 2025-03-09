package org.thingsboard.server.service.report;

import com.datastax.oss.driver.api.core.cql.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.controller.DeviceController;
import org.thingsboard.server.controller.TelemetryController;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.scheduler.SchedulerService;
import org.thingsboard.server.service.telemetry.TsData;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private TelemetryController telemetryController;

    @Autowired
    private DeviceController deviceController;

    @Autowired
    private SchedulerService schedulerService;

    public CompletableFuture<byte[]> generateReport(String reportType, Map<String, String> filterOptions, UsernamePasswordAuthenticationToken authentication) {
        return CompletableFuture.supplyAsync(() -> {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            try {
                List<Map<String, Object>> data = fetchDataBasedOnFilter(filterOptions);
                if (data == null || data.isEmpty()) {
                    return null;
                }
                byte[] report = createReport(data, reportType);
                return report;
            } catch (Exception e) {
                return null;
            } finally {
                SecurityContextHolder.clearContext();
            }
        });
    }

    private List<Map<String, Object>> fetchDataBasedOnFilter(Map<String, String> filterOptions) throws Exception {
        String category = filterOptions.get("category");
        String available = filterOptions.get("available");


        if ("1".equals(category)) {
            return fetchDataForDevice(available);
        } else if ("2".equals(category)) {
            return fetchDataForDeviceType(available);
        } else {
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> fetchDataForDevice(String deviceId) throws Exception {

        Device device = deviceController.getDeviceInfoById(deviceId);
        if (device == null) {
            return Collections.emptyList();
        }

        log.info("Device info retrieved: {}", device);

        DeferredResult<ResponseEntity> deferredResult = telemetryController.getLatestTimeseries(
                device.getId().getEntityType().name(),
                device.getId().getId().toString(),
                null,
                false
        );

        ResponseEntity response = null;
        try {
            CompletableFuture<Object> future = new CompletableFuture<>();
            deferredResult.setResultHandler(future::complete);
            response = (ResponseEntity) future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
        }

        if (response == null) {
            return Collections.emptyList();
        }

        Map<String, List<TsData>> timeseriesData = (Map<String, List<TsData>>) response.getBody();

        if (timeseriesData == null || timeseriesData.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("deviceId", device.getId().getId().toString());
        deviceData.put("deviceType", device.getType());

        for (Map.Entry<String, List<TsData>> entry : timeseriesData.entrySet()) {
            String key = entry.getKey();
            List<TsData> values = entry.getValue();
            if (values != null && !values.isEmpty()) {
                deviceData.put(key, values.get(0).getValue());
            } else {
                deviceData.put(key, null);
            }
        }

        return Collections.singletonList(deviceData);
    }

    private List<Map<String, Object>> fetchDataForDeviceType(String deviceType) {
        List<String> deviceIds = deviceService.findAllIdByDeviceType(deviceType);

        return deviceIds.stream()
                .map(deviceId -> {
                    try {
                        DeferredResult<ResponseEntity> deferredResult = telemetryController.getLatestTimeseries(
                                deviceService.getEntityType().toString(),
                                deviceId,
                                null,
                                false
                        );

                        CompletableFuture<Object> future = new CompletableFuture<>();
                        deferredResult.setResultHandler(future::complete);

                        ResponseEntity<Map<String, List<TsData>>> latestTelemetry =
                                (ResponseEntity<Map<String, List<TsData>>>) future.get(5, TimeUnit.SECONDS);

                        if (latestTelemetry == null) {
                            return null;
                        }

                        Map<String, Object> deviceData = new HashMap<>();
                        deviceData.put("deviceId", deviceId);
                        deviceData.put("deviceType", deviceType);

                        if (latestTelemetry.getBody() != null) {
                            latestTelemetry.getBody().forEach((key, value) -> {
                                if (!value.isEmpty()) {
                                    deviceData.put(key, value.get(0).getValue());
                                }
                            });
                        }

                        return deviceData;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private byte[] createReport(List<Map<String, Object>> data, String type) throws IOException {
        if ("CSV".equalsIgnoreCase(type)) {
            return createCSV(data);
        }
        else if("Excel".equalsIgnoreCase(type)){
            return createCSV(data);
        }
        return null;
    }

    private byte[] createCSV(List<Map<String, Object>> data) {
        StringBuilder csv = new StringBuilder();
        Set<String> headers = data.get(0).keySet();
        csv.append(String.join(",", headers)).append("\n");

        for (Map<String, Object> row : data) {
            csv.append(headers.stream()
                    .map(header -> {
                        Object value = row.get(header);
                        return value != null ? "\"" + value.toString().replace("\"", "\"\"") + "\"" : "";
                    })
                    .collect(Collectors.joining(","))
            ).append("\n");
        }

        byte[] reportData = csv.toString().getBytes();
        log.info("Created CSV report. Size: {} bytes", reportData.length);
        return reportData;
    }
}