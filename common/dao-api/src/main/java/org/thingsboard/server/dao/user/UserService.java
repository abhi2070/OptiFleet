
package org.thingsboard.server.dao.user;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.mobile.MobileSessionInfo;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.TenantProfileId;
import org.thingsboard.server.common.data.id.UserCredentialsId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserService extends EntityDaoService {

    User findUserById(TenantId tenantId, UserId userId);

    ListenableFuture<User> findUserByIdAsync(TenantId tenantId, UserId userId);

    User findUserByEmail(TenantId tenantId, String email);

    User findUserByTenantIdAndEmail(TenantId tenantId, String email);

    User saveUser(TenantId tenantId, User user);

    UserCredentials findUserCredentialsByUserId(TenantId tenantId, UserId userId);

    UserCredentials findUserCredentialsByActivateToken(TenantId tenantId, String activateToken);

    UserCredentials findUserCredentialsByResetToken(TenantId tenantId, String resetToken);

    UserCredentials saveUserCredentials(TenantId tenantId, UserCredentials userCredentials);

    UserCredentials activateUserCredentials(TenantId tenantId, String activateToken, String password);

    UserCredentials requestPasswordReset(TenantId tenantId, String email);

    UserCredentials requestExpiredPasswordReset(TenantId tenantId, UserCredentialsId userCredentialsId);

    UserCredentials replaceUserCredentials(TenantId tenantId, UserCredentials userCredentials);

    void deleteUser(TenantId tenantId, User user);

    PageData<User> findUsersByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<User> findTenantAdmins(TenantId tenantId, PageLink pageLink);

    PageData<User> findSysAdmins(PageLink pageLink);

    PageData<User> findAllTenantAdmins(PageLink pageLink);

    PageData<User> findTenantAdminsByTenantsIds(List<TenantId> tenantsIds, PageLink pageLink);

    PageData<User> findTenantAdminsByTenantProfilesIds(List<TenantProfileId> tenantProfilesIds, PageLink pageLink);

    PageData<User> findAllUsers(PageLink pageLink);

    void deleteTenantAdmins(TenantId tenantId);

    PageData<User> findCustomerUsers(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    PageData<User> findUsersByCustomerIds(TenantId tenantId, List<CustomerId> customerIds, PageLink pageLink);

    void deleteCustomerUsers(TenantId tenantId, CustomerId customerId);

    void setUserCredentialsEnabled(TenantId tenantId, UserId userId, boolean enabled);

    void resetFailedLoginAttempts(TenantId tenantId, UserId userId);

    int increaseFailedLoginAttempts(TenantId tenantId, UserId userId);

    void setLastLoginTs(TenantId tenantId, UserId userId);

    void saveMobileSession(TenantId tenantId, UserId userId, String mobileToken, MobileSessionInfo sessionInfo);

    Map<String, MobileSessionInfo> findMobileSessions(TenantId tenantId, UserId userId);

    MobileSessionInfo findMobileSession(TenantId tenantId, UserId userId, String mobileToken);

    void removeMobileSession(TenantId tenantId, String mobileToken);

}
