package org.thingsboard.server.dao.sql.roles;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.RolesId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.roles.Roles;
import org.thingsboard.server.common.data.roles.RolesInfo;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.RolesEntity;
import org.thingsboard.server.dao.roles.RolesDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.*;


import static org.thingsboard.server.dao.roles.BaseRolesService.TB_SERVICE_QUEUE;

/**
 * Created by Utsav on 7/5/2024.
 */
@Component
@SqlDao
@Slf4j
public class JpaRolesDao extends JpaAbstractDao<RolesEntity, Roles> implements RolesDao {


    @Autowired
    private RolesRepository rolesRepository;


    @Override
    protected Class<RolesEntity> getEntityClass() {
        return RolesEntity.class;
    }

    @Override
    protected JpaRepository<RolesEntity, UUID> getRepository() {
        return rolesRepository;
    }

    @Override
    public RolesInfo findRolesInfoById(TenantId tenantId, UUID rolesId) {
        return DaoUtil.getData(rolesRepository.findRolesInfoById(rolesId));
    }



    @Override
    public PageData<Roles> findRolesByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(rolesRepository.findByTenantId(tenantId, pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }

    @Override
    public Optional<Roles> findRolesByTenantIdAndName(UUID tenantId, String name) {
        Roles roles = DaoUtil.getData(rolesRepository.findByTenantIdAndName(tenantId, name));
        return Optional.ofNullable(roles);
    }


    @Override
    @Transactional
    public Roles saveAndFlush(TenantId tenantId, Roles roles) {
       Roles result = save(tenantId, roles);
        rolesRepository.flush();
       return result;

    }



    @Override
    public PageData<Roles> findRolesByTenantIdAndType(UUID tenantId, String type, PageLink pageLink) {
        return DaoUtil.toPageData(rolesRepository.findByTenantIdAndType( tenantId,type,pageLink.getTextSearch(), DaoUtil.toPageable(pageLink)));
    }


    @Override
    public Long countByTenantId(TenantId tenantId) {
        return rolesRepository.countByTenantIdAndTypeIsNot(tenantId.getId(), TB_SERVICE_QUEUE);
    }

    @Override
    public Roles findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
//        return DaoUtil.getData(rolesRepository.findByTenantIdAndExternalId(tenantId, externalId));
        return null;
    }

    @Override
    public Roles findByTenantIdAndName(UUID tenantId, String name) {
        return findRolesByTenantIdAndName(tenantId, name).orElse(null);
    }

    @Override
    public PageData<Roles> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findRolesByTenantId(tenantId, pageLink);
    }



    @Override
    public RolesId getExternalIdByInternal(RolesId internalId) {
        return Optional.ofNullable(rolesRepository.getExternalIdById(internalId.getId()))
                .map(RolesId::new).orElse(null);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ROLES;
    }

}
