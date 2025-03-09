package org.thingsboard.server.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thingsboard.rule.engine.api.MailService;
import org.thingsboard.server.common.data.TenantInfo;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.scheduler.EmailConfiguration;
import org.thingsboard.server.common.data.scheduler.Scheduler;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.controller.BaseController;
import org.thingsboard.server.dao.scheduler.SchedulerService;
import org.thingsboard.server.dao.tenant.TenantService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.report.ReportService;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@TbCoreComponent
public class EmailSchedulerService  {

    @Autowired
    private final MailService mailService;

    @Autowired
    private final SchedulerService schedulerService;

    @Autowired
    private final ReportService reportService;

    @Autowired
    private TenantService tenantService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Scheduled(fixedRate = 21600000)
    public void scheduledEmailSender() {

        PageLink tenantPageLink = new PageLink(100);
        PageData<TenantInfo> tenantInfoPage;

        do {
            tenantInfoPage = tenantService.findTenantInfos(tenantPageLink);

            for (TenantInfo tenantInfo : tenantInfoPage.getData()) {
                TenantId tenantId = tenantInfo.getId();
                processSchedulersForTenant(tenantId);
            }

            if (tenantInfoPage.hasNext()) {
                tenantPageLink = tenantPageLink.nextPageLink();
            }
        } while (tenantInfoPage.hasNext());

    }

    private void processSchedulersForTenant(TenantId tenantId) {
        SecurityUser securityUser = new SecurityUser();
        securityUser.setTenantId(tenantId);
        securityUser.setAuthority(Authority.TENANT_ADMIN);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                securityUser,
                null,
                Collections.singletonList(new SimpleGrantedAuthority(Authority.TENANT_ADMIN.name()))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            PageLink schedulerPageLink = new PageLink(100);
            PageData<Scheduler> schedulers;

            do {
                schedulers = schedulerService.findSchedulerByTenantId(tenantId, schedulerPageLink);

                for (Scheduler scheduler : schedulers.getData()) {
                    processScheduler(scheduler, authentication);
                }

                if (schedulers.hasNext()) {
                    schedulerPageLink = schedulerPageLink.nextPageLink();
                }
            } while (schedulers.hasNext());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void processScheduler(Scheduler scheduler, UsernamePasswordAuthenticationToken authentication) {
        Instant now = Instant.now();
        if (isSchedulerValid(scheduler, now)) {
            try {
                CompletableFuture<byte[]> reportFuture = reportService.generateReport(
                        scheduler.getReportType(),
                        Map.of("category", scheduler.getReportCategory(),
                                "available", scheduler.getReportAvailable()),
                        authentication
                );

                reportFuture.thenAccept(reportData -> {
                    if (reportData == null || reportData.length == 0) {
                        return;
                    }

                    EmailConfiguration emailConfig = new EmailConfiguration();
                    emailConfig.setToAddress(scheduler.getToAddress());
                    emailConfig.setSubject(scheduler.getSubject());
                    emailConfig.setBody(scheduler.getBody());
                    emailConfig.setCc(scheduler.getcc());
                    emailConfig.setBcc(scheduler.getbcc());

                    String reportName = "report." + (scheduler.getReportType().equalsIgnoreCase("CSV") ? "csv" : "xlsx");

                    try {
                        mailService.sendEmailAtScheduledTime(emailConfig, reportData, reportName);
                    } catch (Exception e) {
                        log.error("Error sending email for scheduler {}: {}", scheduler.getId(), e.getMessage(), e);
                    }
                }).exceptionally(e -> {
                    return null;
                });

            } catch (Exception e) {
                log.error("Error initiating report generation for scheduler {}: {}", scheduler.getId(), e.getMessage(), e);
            }
        } else {
        }
    }


    protected boolean isSchedulerValid(Scheduler scheduler, Instant now) {
        if (scheduler == null || !scheduler.getActive()) {
            return false;
        }

        String startDateStr = scheduler.getStart();
        String endDateStr = scheduler.getEndDate();

        if (startDateStr == null || endDateStr == null) {
            return false;
        }

        try {
            LocalDateTime start = LocalDateTime.parse(startDateStr, DATE_FORMATTER).atOffset(ZoneOffset.UTC).toLocalDateTime();
            LocalDateTime end = LocalDateTime.parse(endDateStr, DATE_FORMATTER).atOffset(ZoneOffset.UTC).toLocalDateTime();
            LocalDateTime currentDateTime = now.atOffset(ZoneOffset.UTC).toLocalDateTime();

            return !start.isAfter(currentDateTime) && !end.isBefore(currentDateTime);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}