
package org.thingsboard.server.service.entity.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.rule.engine.api.MailService;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entity.AbstractTbEntityService;
import org.thingsboard.server.service.security.system.SystemSecurityService;

import javax.servlet.http.HttpServletRequest;

import static org.thingsboard.server.controller.UserController.ACTIVATE_URL_PATTERN;

@Service
@TbCoreComponent
@AllArgsConstructor
@Slf4j
public class DefaultUserService extends AbstractTbEntityService implements TbUserService {

    private final UserService userService;
    private final MailService mailService;
    private final SystemSecurityService systemSecurityService;

    @Override
    public User save(TenantId tenantId, CustomerId customerId, User tbUser, boolean sendActivationMail,
                     HttpServletRequest request, User user) throws ThingsboardException {
        ActionType actionType = tbUser.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        try {
            boolean sendEmail = tbUser.getId() == null && sendActivationMail;
            User savedUser = checkNotNull(userService.saveUser(tenantId, tbUser));
            if (sendEmail) {
                UserCredentials userCredentials = userService.findUserCredentialsByUserId(tenantId, savedUser.getId());
                String baseUrl = systemSecurityService.getBaseUrl(tenantId, customerId, request);
                String activateUrl = String.format(ACTIVATE_URL_PATTERN, baseUrl,
                        userCredentials.getActivateToken());
                String email = savedUser.getEmail();
                try {
                    mailService.sendActivationEmail(activateUrl, email);
                } catch (ThingsboardException e) {
                    userService.deleteUser(tenantId, savedUser);
                    throw e;
                }
            }
            notificationEntityService.logEntityAction(tenantId, savedUser.getId(), savedUser, customerId, actionType, user);
            return savedUser;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.USER), tbUser, actionType, user, e);
            throw e;
        }
    }

    @Override
    public void delete(TenantId tenantId, CustomerId customerId, User user, User responsibleUser) throws ThingsboardException {
        ActionType actionType = ActionType.DELETED;
        UserId userId = user.getId();

        try {
            userService.deleteUser(tenantId, user);
            notificationEntityService.logEntityAction(tenantId, userId, user, customerId, actionType, responsibleUser, customerId.toString());
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.USER),
                    actionType, responsibleUser, e, userId.toString());
            throw e;
        }
    }
}
