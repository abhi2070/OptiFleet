package org.thingsboard.server.dao.roles;



import org.thingsboard.server.common.data.id.RolesId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.common.data.roles.RolesInfo;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.ExportableEntityDao;
import org.thingsboard.server.dao.TenantEntityDao;
import java.util.Optional;
import java.util.UUID;


/**
 * The Interface RolesDao.
 *
 */
public interface RolesDao extends Dao<Roles>, TenantEntityDao, ExportableEntityDao<RolesId, Roles> {

    /**
     * Find roles info by id.
     *
     * @param tenantId the tenant id
     * @param rolesId the roles id
     * @return the roles info object
     */
    RolesInfo findRolesInfoById(TenantId tenantId, UUID rolesId);



    /**
     * Save or update roles object
     *
     * @param roles the roles object
     * @return saved roles object
     */
    Roles save(TenantId tenantId, Roles roles);

    /**
     * Save or update device object
     *
     * @param roles the roles object
     * @return saved roles object
     */
    Roles saveAndFlush(TenantId tenantId, Roles roles);



    /**
     * Find roles by tenantId and page link.
     *
     * @param tenantId the tenantId
     * @param pageLink the page link
     * @return the list of roles objects
     */
    PageData<Roles> findRolesByTenantId(UUID tenantId, PageLink pageLink);

    /**
     * Find roles by tenantId, type and page link.
     *
     * @param tenantId the tenantId
     * @param type the type
     * @param pageLink the page link
     * @return the list of roles objects
     */
    PageData<Roles> findRolesByTenantIdAndType(UUID tenantId, String type, PageLink pageLink);


    /**
     * Find roles by tenantId and roles name.
     *
     * @param tenantId the tenantId
     * @param name the roles name
     * @return the optional roles object
     */
    Optional<Roles> findRolesByTenantIdAndName(UUID tenantId, String name);


    }
