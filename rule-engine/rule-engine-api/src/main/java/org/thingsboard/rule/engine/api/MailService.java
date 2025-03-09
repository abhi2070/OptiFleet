
package org.thingsboard.rule.engine.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.ApiFeature;
import org.thingsboard.server.common.data.ApiUsageRecordState;
import org.thingsboard.server.common.data.ApiUsageStateValue;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.scheduler.EmailConfiguration;

import javax.mail.MessagingException;

public interface MailService {

    void updateMailConfiguration();

    void sendEmail(TenantId tenantId, String email, String subject, String message) throws ThingsboardException;

    void emailScheduler(EmailConfiguration emailConfiguration, MultipartFile[] attachments) throws ThingsboardException;

    void sendEmailAtScheduledTime(EmailConfiguration emailConfiguration, byte[] report, String reportName) throws ThingsboardException, MessagingException;

    void sendTestMail(JsonNode config, String email) throws ThingsboardException;

    void sendActivationEmail(String activationLink, String email) throws ThingsboardException;

    void sendAccountActivatedEmail(String loginLink, String email) throws ThingsboardException;

    void sendResetPasswordEmail(String passwordResetLink, String email) throws ThingsboardException;

    void sendResetPasswordEmailAsync(String passwordResetLink, String email);

    void sendPasswordWasResetEmail(String loginLink, String email) throws ThingsboardException;

    void sendAccountLockoutEmail(String lockoutEmail, String email, Integer maxFailedLoginAttempts) throws ThingsboardException;

    void sendTwoFaVerificationEmail(String email, String verificationCode, int expirationTimeSeconds) throws ThingsboardException;

    void send(TenantId tenantId, CustomerId customerId, TbEmail tbEmail) throws ThingsboardException;

    void send(TenantId tenantId, CustomerId customerId, TbEmail tbEmail, JavaMailSender javaMailSender, long timeout) throws ThingsboardException;

    void sendApiFeatureStateEmail(ApiFeature apiFeature, ApiUsageStateValue stateValue, String email, ApiUsageRecordState recordState) throws ThingsboardException;

    void testConnection(TenantId tenantId) throws Exception;

    boolean isConfigured(TenantId tenantId);

}
