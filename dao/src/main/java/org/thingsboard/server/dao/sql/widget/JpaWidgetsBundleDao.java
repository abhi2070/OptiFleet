
package org.thingsboard.server.dao.sql.widget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.WidgetsBundleId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.widget.WidgetsBundle;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.WidgetsBundleEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;
import org.thingsboard.server.dao.widget.WidgetsBundleDao;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.NULL_UUID;

/**
 * Created by Valerii Sosliuk on 4/23/2017.
 */
@Component
@SqlDao
public class JpaWidgetsBundleDao extends JpaAbstractDao<WidgetsBundleEntity, WidgetsBundle> implements WidgetsBundleDao {

    @Autowired
    private WidgetsBundleRepository widgetsBundleRepository;

    @Override
    protected Class<WidgetsBundleEntity> getEntityClass() {
        return WidgetsBundleEntity.class;
    }

    @Override
    protected JpaRepository<WidgetsBundleEntity, UUID> getRepository() {
        return widgetsBundleRepository;
    }

    @Override
    public WidgetsBundle findWidgetsBundleByTenantIdAndAlias(UUID tenantId, String alias) {
        return DaoUtil.getData(widgetsBundleRepository.findWidgetsBundleByTenantIdAndAlias(tenantId, alias));
    }

    @Override
    public PageData<WidgetsBundle> findSystemWidgetsBundles(TenantId tenantId, boolean fullSearch, PageLink pageLink) {
        if (fullSearch) {
            return DaoUtil.toPageData(
                    widgetsBundleRepository
                            .findSystemWidgetsBundlesFullSearch(
                                    NULL_UUID,
                                    pageLink.getTextSearch(),
                                    DaoUtil.toPageable(pageLink)));
        } else {
            return DaoUtil.toPageData(
                    widgetsBundleRepository
                            .findSystemWidgetsBundles(
                                    NULL_UUID,
                                    pageLink.getTextSearch(),
                                    DaoUtil.toPageable(pageLink)));
        }
    }

    @Override
    public PageData<WidgetsBundle> findTenantWidgetsBundlesByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                widgetsBundleRepository
                        .findTenantWidgetsBundlesByTenantId(
                                tenantId,
                                pageLink.getTextSearch(),
                                DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<WidgetsBundle> findAllTenantWidgetsBundlesByTenantId(UUID tenantId, boolean fullSearch, PageLink pageLink) {
        return findTenantWidgetsBundlesByTenantIds(Arrays.asList(tenantId, NULL_UUID), fullSearch, pageLink);
    }

    @Override
    public PageData<WidgetsBundle> findTenantWidgetsBundlesByTenantId(UUID tenantId, boolean fullSearch, PageLink pageLink) {
        return findTenantWidgetsBundlesByTenantIds(Collections.singletonList(tenantId), fullSearch, pageLink);
    }

    @Override
    public PageData<WidgetsBundle> findAllWidgetsBundles(PageLink pageLink) {
        return DaoUtil.toPageData(widgetsBundleRepository.findAll(DaoUtil.toPageable(pageLink)));
    }

    private PageData<WidgetsBundle> findTenantWidgetsBundlesByTenantIds(List<UUID> tenantIds, boolean fullSearch, PageLink pageLink) {
        if (fullSearch) {
            return DaoUtil.toPageData(
                    widgetsBundleRepository
                            .findAllTenantWidgetsBundlesByTenantIdsFullSearch(
                                    tenantIds,
                                    pageLink.getTextSearch(),
                                    DaoUtil.toPageable(pageLink)));
        } else {
            return DaoUtil.toPageData(
                    widgetsBundleRepository
                            .findAllTenantWidgetsBundlesByTenantIds(
                                    tenantIds,
                                    pageLink.getTextSearch(),
                                    DaoUtil.toPageable(pageLink)));
        }
    }

    @Override
    public WidgetsBundle findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
//        return DaoUtil.getData(widgetsBundleRepository.findByTenantIdAndExternalId(tenantId, externalId));
        return null;
    }

    @Override
    public WidgetsBundle findByTenantIdAndName(UUID tenantId, String name) {
        return DaoUtil.getData(widgetsBundleRepository.findFirstByTenantIdAndTitle(tenantId, name));
    }

    @Override
    public PageData<WidgetsBundle> findByTenantId(UUID tenantId, PageLink pageLink) {
        return findTenantWidgetsBundlesByTenantId(tenantId, pageLink);
    }

    @Override
    public WidgetsBundleId getExternalIdByInternal(WidgetsBundleId internalId) {
        return Optional.ofNullable(widgetsBundleRepository.getExternalIdById(internalId.getId()))
                .map(WidgetsBundleId::new).orElse(null);
    }

    @Override
    public List<WidgetsBundle> findByTenantAndImageLink(TenantId tenantId, String imageUrl, int limit) {
        return DaoUtil.convertDataList(widgetsBundleRepository.findByTenantAndImageUrl(tenantId.getId(), imageUrl, limit));
    }

    @Override
    public List<WidgetsBundle> findByImageLink(String imageUrl, int limit) {
         return DaoUtil.convertDataList(widgetsBundleRepository.findByImageUrl(imageUrl, limit));
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.WIDGETS_BUNDLE;
    }

}
