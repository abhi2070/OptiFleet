
package org.thingsboard.server.dao.sql.widget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.WidgetTypeId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.widget.DeprecatedFilter;
import org.thingsboard.server.common.data.widget.WidgetType;
import org.thingsboard.server.common.data.widget.WidgetTypeDetails;
import org.thingsboard.server.common.data.widget.WidgetTypeInfo;
import org.thingsboard.server.common.data.widget.WidgetsBundleWidget;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.WidgetTypeDetailsEntity;
import org.thingsboard.server.dao.model.sql.WidgetTypeInfoEntity;
import org.thingsboard.server.dao.model.sql.WidgetsBundleWidgetCompositeKey;
import org.thingsboard.server.dao.model.sql.WidgetsBundleWidgetEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;
import org.thingsboard.server.dao.widget.WidgetTypeDao;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.thingsboard.server.dao.model.ModelConstants.NULL_UUID;

/**
 * Created by Valerii Sosliuk on 4/29/2017.
 */
@Component
@SqlDao
public class JpaWidgetTypeDao extends JpaAbstractDao<WidgetTypeDetailsEntity, WidgetTypeDetails> implements WidgetTypeDao {

    @Autowired
    private WidgetTypeRepository widgetTypeRepository;

    @Autowired
    private WidgetTypeInfoRepository widgetTypeInfoRepository;

    @Autowired
    private WidgetsBundleWidgetRepository widgetsBundleWidgetRepository;

    @Override
    protected Class<WidgetTypeDetailsEntity> getEntityClass() {
        return WidgetTypeDetailsEntity.class;
    }

    @Override
    protected JpaRepository<WidgetTypeDetailsEntity, UUID> getRepository() {
        return widgetTypeRepository;
    }

    @Override
    public WidgetType findWidgetTypeById(TenantId tenantId, UUID widgetTypeId) {
        return DaoUtil.getData(widgetTypeRepository.findWidgetTypeById(widgetTypeId));
    }

    @Override
    public boolean existsByTenantIdAndId(TenantId tenantId, UUID widgetTypeId) {
        return widgetTypeRepository.existsByTenantIdAndId(tenantId.getId(), widgetTypeId);
    }

    @Override
    public PageData<WidgetTypeInfo> findSystemWidgetTypes(TenantId tenantId, boolean fullSearch, DeprecatedFilter deprecatedFilter, List<String> widgetTypes, PageLink pageLink) {
        boolean deprecatedFilterEnabled = !DeprecatedFilter.ALL.equals(deprecatedFilter);
        boolean deprecatedFilterBool = DeprecatedFilter.DEPRECATED.equals(deprecatedFilter);
        boolean widgetTypesEmpty = widgetTypes == null || widgetTypes.isEmpty();
        return DaoUtil.toPageData(
                widgetTypeInfoRepository
                        .findSystemWidgetTypes(
                                NULL_UUID,
                                pageLink.getTextSearch(),
                                fullSearch,
                                deprecatedFilterEnabled,
                                deprecatedFilterBool,
                                widgetTypesEmpty,
                                widgetTypes == null ? Collections.emptyList() : widgetTypes,
                                DaoUtil.toPageable(pageLink, WidgetTypeInfoEntity.SEARCH_COLUMNS_MAP)));
    }

    @Override
    public PageData<WidgetTypeInfo> findAllTenantWidgetTypesByTenantId(UUID tenantId, boolean fullSearch, DeprecatedFilter deprecatedFilter, List<String> widgetTypes, PageLink pageLink) {
        boolean deprecatedFilterEnabled = !DeprecatedFilter.ALL.equals(deprecatedFilter);
        boolean deprecatedFilterBool = DeprecatedFilter.DEPRECATED.equals(deprecatedFilter);
        boolean widgetTypesEmpty = widgetTypes == null || widgetTypes.isEmpty();
        return DaoUtil.toPageData(
                widgetTypeInfoRepository
                        .findAllTenantWidgetTypesByTenantId(
                                tenantId,
                                NULL_UUID,
                                pageLink.getTextSearch(),
                                fullSearch,
                                deprecatedFilterEnabled,
                                deprecatedFilterBool,
                                widgetTypesEmpty,
                                widgetTypes == null ? Collections.emptyList() : widgetTypes,
                                DaoUtil.toPageable(pageLink, WidgetTypeInfoEntity.SEARCH_COLUMNS_MAP)));
    }

    @Override
    public PageData<WidgetTypeInfo> findTenantWidgetTypesByTenantId(UUID tenantId, boolean fullSearch, DeprecatedFilter deprecatedFilter, List<String> widgetTypes, PageLink pageLink) {
        boolean deprecatedFilterEnabled = !DeprecatedFilter.ALL.equals(deprecatedFilter);
        boolean deprecatedFilterBool = DeprecatedFilter.DEPRECATED.equals(deprecatedFilter);
        boolean widgetTypesEmpty = widgetTypes == null || widgetTypes.isEmpty();
        return DaoUtil.toPageData(
                widgetTypeInfoRepository
                        .findTenantWidgetTypesByTenantId(
                                tenantId,
                                pageLink.getTextSearch(),
                                fullSearch,
                                deprecatedFilterEnabled,
                                deprecatedFilterBool,
                                widgetTypesEmpty,
                                widgetTypes == null ? Collections.emptyList() : widgetTypes,
                                DaoUtil.toPageable(pageLink, WidgetTypeInfoEntity.SEARCH_COLUMNS_MAP)));
    }

    @Override
    public List<WidgetType> findWidgetTypesByWidgetsBundleId(UUID tenantId, UUID widgetsBundleId) {
        return DaoUtil.convertDataList(widgetTypeRepository.findWidgetTypesByWidgetsBundleId(widgetsBundleId));
    }

    @Override
    public List<WidgetTypeDetails> findWidgetTypesDetailsByWidgetsBundleId(UUID tenantId, UUID widgetsBundleId) {
        return DaoUtil.convertDataList(widgetTypeRepository.findWidgetTypesDetailsByWidgetsBundleId(widgetsBundleId));
    }

    @Override
    public PageData<WidgetTypeInfo> findWidgetTypesInfosByWidgetsBundleId(UUID tenantId, UUID widgetsBundleId, boolean fullSearch, DeprecatedFilter deprecatedFilter, List<String> widgetTypes, PageLink pageLink) {
        boolean deprecatedFilterEnabled = !DeprecatedFilter.ALL.equals(deprecatedFilter);
        boolean deprecatedFilterBool = DeprecatedFilter.DEPRECATED.equals(deprecatedFilter);
        boolean widgetTypesEmpty = widgetTypes == null || widgetTypes.isEmpty();
        return DaoUtil.toPageData(
                widgetTypeInfoRepository
                        .findWidgetTypesInfosByWidgetsBundleId(
                                widgetsBundleId,
                                Objects.toString(pageLink.getTextSearch(), ""),
                                fullSearch,
                                deprecatedFilterEnabled,
                                deprecatedFilterBool,
                                widgetTypesEmpty,
                                widgetTypes == null ? Collections.emptyList() : widgetTypes,
                                DaoUtil.toPageable(pageLink, WidgetTypeInfoEntity.SEARCH_COLUMNS_MAP)));
    }

    @Override
    public List<String> findWidgetFqnsByWidgetsBundleId(UUID tenantId, UUID widgetsBundleId) {
        return widgetTypeRepository.findWidgetFqnsByWidgetsBundleId(widgetsBundleId);
    }

    @Override
    public WidgetType findByTenantIdAndFqn(UUID tenantId, String fqn) {
        return DaoUtil.getData(widgetTypeRepository.findWidgetTypeByTenantIdAndFqn(tenantId, fqn));
    }

    @Override
    public List<WidgetTypeDetails> findWidgetTypesInfosByTenantIdAndResourceId(UUID tenantId, UUID tbResourceId) {
        return DaoUtil.convertDataList(widgetTypeRepository.findWidgetTypesInfosByTenantIdAndResourceId(tenantId, tbResourceId));
    }

    @Override
    public List<WidgetTypeId> findWidgetTypeIdsByTenantIdAndFqns(UUID tenantId, List<String> widgetFqns) {
        var idFqnPairs = widgetTypeRepository.findWidgetTypeIdsByTenantIdAndFqns(tenantId, widgetFqns);
        idFqnPairs.sort(Comparator.comparingInt(o -> widgetFqns.indexOf(o.getFqn())));
        return idFqnPairs.stream()
                .map(id -> new WidgetTypeId(id.getId())).collect(Collectors.toList());
    }

    @Override
    public WidgetTypeDetails findByTenantIdAndExternalId(UUID tenantId, UUID externalId) {
//        return DaoUtil.getData(widgetTypeRepository.findByTenantIdAndExternalId(tenantId, externalId));
        return null;
    }

    @Override
    public PageData<WidgetTypeDetails> findByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(
                widgetTypeRepository
                        .findTenantWidgetTypeDetailsByTenantId(
                                tenantId,
                                pageLink.getTextSearch(),
                                DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<WidgetTypeId> findIdsByTenantId(UUID tenantId, PageLink pageLink) {
        return DaoUtil.pageToPageData(widgetTypeRepository.findIdsByTenantId(tenantId, DaoUtil.toPageable(pageLink))
                .map(WidgetTypeId::new));
    }

    @Override
    public WidgetTypeId getExternalIdByInternal(WidgetTypeId internalId) {
        return Optional.ofNullable(widgetTypeRepository.getExternalIdById(internalId.getId()))
                .map(WidgetTypeId::new).orElse(null);
    }

    @Override
    public List<WidgetsBundleWidget> findWidgetsBundleWidgetsByWidgetsBundleId(UUID tenantId, UUID widgetsBundleId) {
        return DaoUtil.convertDataList(widgetsBundleWidgetRepository.findAllByWidgetsBundleId(widgetsBundleId));
    }

    @Override
    public void saveWidgetsBundleWidget(WidgetsBundleWidget widgetsBundleWidget) {
        widgetsBundleWidgetRepository.save(new WidgetsBundleWidgetEntity(widgetsBundleWidget));
    }

    @Override
    public void removeWidgetTypeFromWidgetsBundle(UUID widgetsBundleId, UUID widgetTypeId) {
        widgetsBundleWidgetRepository.deleteById(new WidgetsBundleWidgetCompositeKey(widgetsBundleId, widgetTypeId));
    }

    @Override
    public PageData<WidgetTypeId> findAllWidgetTypesIds(PageLink pageLink) {
        return DaoUtil.pageToPageData(widgetTypeRepository.findAllIds(DaoUtil.toPageable(pageLink)).map(WidgetTypeId::new));
    }

    @Override
    public List<WidgetTypeInfo> findByTenantAndImageLink(TenantId tenantId, String imageUrl, int limit) {
        return DaoUtil.convertDataList(widgetTypeInfoRepository.findByTenantAndImageUrl(tenantId.getId(), imageUrl, limit));
    }

    @Override
    public List<WidgetTypeInfo> findByImageLink(String imageUrl, int limit) {
        return DaoUtil.convertDataList(widgetTypeInfoRepository.findByImageUrl(imageUrl, limit));
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.WIDGET_TYPE;
    }


}
