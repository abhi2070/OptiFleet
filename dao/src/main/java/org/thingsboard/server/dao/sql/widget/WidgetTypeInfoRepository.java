
package org.thingsboard.server.dao.sql.widget;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.model.sql.WidgetTypeInfoEntity;

import java.util.List;
import java.util.UUID;

public interface WidgetTypeInfoRepository extends JpaRepository<WidgetTypeInfoEntity, UUID>  {

    @Query(nativeQuery = true,
            value = "SELECT * FROM widget_type_info_view wti WHERE wti.tenant_id = :systemTenantId " +
                    "AND ((:deprecatedFilterEnabled) IS FALSE OR wti.deprecated = :deprecatedFilter) " +
                    "AND ((:widgetTypesEmpty) IS TRUE OR wti.widget_type IN (:widgetTypes)) " +
                    "AND (wti.name ILIKE CONCAT('%', :searchText, '%') " +
                    "OR ((:fullSearch) IS TRUE AND (wti.description ILIKE CONCAT('%', :searchText, '%') " +
                    "OR EXISTS (" +
                        "SELECT 1 " +
                        "FROM unnest(wti.tags) AS currentTag " +
                        "WHERE :searchText ILIKE '%' || currentTag || '%' " +
                            "AND (length(:searchText) = length(currentTag) " +
                            "OR :searchText ILIKE currentTag || ' %' " +
                            "OR :searchText ILIKE '% ' || currentTag " +
                            "OR :searchText ILIKE '% ' || currentTag || ' %')" +
                    "))))",
            countQuery = "SELECT count(*) FROM widget_type_info_view wti WHERE wti.tenant_id = :systemTenantId " +
                    "AND ((:deprecatedFilterEnabled) IS FALSE OR wti.deprecated = :deprecatedFilter) " +
                    "AND ((:widgetTypesEmpty) IS TRUE OR wti.widget_type IN (:widgetTypes)) " +
                    "AND (wti.name ILIKE CONCAT('%', :searchText, '%') " +
                    "OR ((:fullSearch) IS TRUE AND (wti.description ILIKE CONCAT('%', :searchText, '%') " +
                    "OR EXISTS (" +
                        "SELECT 1 " +
                        "FROM unnest(wti.tags) AS currentTag " +
                        "WHERE :searchText ILIKE '%' || currentTag || '%' " +
                            "AND (length(:searchText) = length(currentTag) " +
                            "OR :searchText ILIKE currentTag || ' %' " +
                            "OR :searchText ILIKE '% ' || currentTag " +
                            "OR :searchText ILIKE '% ' || currentTag || ' %')" +
                    "))))"
    )
    Page<WidgetTypeInfoEntity> findSystemWidgetTypes(@Param("systemTenantId") UUID systemTenantId,
                                                          @Param("searchText") String searchText,
                                                          @Param("fullSearch") boolean fullSearch,
                                                          @Param("deprecatedFilterEnabled") boolean deprecatedFilterEnabled,
                                                          @Param("deprecatedFilter") boolean deprecatedFilter,
                                                          @Param("widgetTypesEmpty") boolean widgetTypesEmpty,
                                                          @Param("widgetTypes") List<String> widgetTypes,
                                                          Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT * FROM widget_type_info_view wti WHERE wti.tenant_id IN (:tenantId, :nullTenantId) " +
                    "AND ((:deprecatedFilterEnabled) IS FALSE OR wti.deprecated = :deprecatedFilter) " +
                    "AND ((:widgetTypesEmpty) IS TRUE OR wti.widget_type IN (:widgetTypes)) " +
                    "AND (wti.name ILIKE CONCAT('%', :searchText, '%') " +
                    "OR ((:fullSearch) IS TRUE AND (wti.description ILIKE CONCAT('%', :searchText, '%') " +
                    "OR EXISTS (" +
                        "SELECT 1 " +
                        "FROM unnest(wti.tags) AS currentTag " +
                        "WHERE :searchText ILIKE '%' || currentTag || '%' " +
                            "AND (length(:searchText) = length(currentTag) " +
                            "OR :searchText ILIKE currentTag || ' %' " +
                            "OR :searchText ILIKE '% ' || currentTag " +
                            "OR :searchText ILIKE '% ' || currentTag || ' %')" +
                    "))))",
            countQuery = "SELECT count(*) FROM widget_type_info_view wti WHERE wti.tenant_id IN (:tenantId, :nullTenantId) " +
                    "AND ((:deprecatedFilterEnabled) IS FALSE OR wti.deprecated = :deprecatedFilter) " +
                    "AND ((:widgetTypesEmpty) IS TRUE OR wti.widget_type IN (:widgetTypes)) " +
                    "AND (wti.name ILIKE CONCAT('%', :searchText, '%') " +
                    "OR ((:fullSearch) IS TRUE AND (wti.description ILIKE CONCAT('%', :searchText, '%') " +
                    "OR EXISTS (" +
                        "SELECT 1 " +
                        "FROM unnest(wti.tags) AS currentTag " +
                        "WHERE :searchText ILIKE '%' || currentTag || '%' " +
                            "AND (length(:searchText) = length(currentTag) " +
                            "OR :searchText ILIKE currentTag || ' %' " +
                            "OR :searchText ILIKE '% ' || currentTag " +
                            "OR :searchText ILIKE '% ' || currentTag || ' %')" +
                    "))))"
    )
    Page<WidgetTypeInfoEntity> findAllTenantWidgetTypesByTenantId(@Param("tenantId") UUID tenantId,
                                                                  @Param("nullTenantId") UUID nullTenantId,
                                                                  @Param("searchText") String searchText,
                                                                  @Param("fullSearch") boolean fullSearch,
                                                                  @Param("deprecatedFilterEnabled") boolean deprecatedFilterEnabled,
                                                                  @Param("deprecatedFilter") boolean deprecatedFilter,
                                                                  @Param("widgetTypesEmpty") boolean widgetTypesEmpty,
                                                                  @Param("widgetTypes") List<String> widgetTypes,
                                                                  Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT * FROM widget_type_info_view wti WHERE wti.tenant_id = :tenantId " +
                    "AND ((:deprecatedFilterEnabled) IS FALSE OR wti.deprecated = :deprecatedFilter) " +
                    "AND ((:widgetTypesEmpty) IS TRUE OR wti.widget_type IN (:widgetTypes)) " +
                    "AND (wti.name ILIKE CONCAT('%', :searchText, '%') " +
                    "OR ((:fullSearch) IS TRUE AND (wti.description ILIKE CONCAT('%', :searchText, '%') " +
                    "OR EXISTS (" +
                        "SELECT 1 " +
                        "FROM unnest(wti.tags) AS currentTag " +
                        "WHERE :searchText ILIKE '%' || currentTag || '%' " +
                            "AND (length(:searchText) = length(currentTag) " +
                            "OR :searchText ILIKE currentTag || ' %' " +
                            "OR :searchText ILIKE '% ' || currentTag " +
                            "OR :searchText ILIKE '% ' || currentTag || ' %')" +
                    "))))",
            countQuery = "SELECT count(*) FROM widget_type_info_view wti WHERE wti.tenant_id = :tenantId " +
                    "AND ((:deprecatedFilterEnabled) IS FALSE OR wti.deprecated = :deprecatedFilter) " +
                    "AND ((:widgetTypesEmpty) IS TRUE OR wti.widget_type IN (:widgetTypes)) " +
                    "AND (wti.name ILIKE CONCAT('%', :searchText, '%') " +
                    "OR ((:fullSearch) IS TRUE AND (wti.description ILIKE CONCAT('%', :searchText, '%') " +
                    "OR EXISTS (" +
                        "SELECT 1 " +
                        "FROM unnest(wti.tags) AS currentTag " +
                        "WHERE :searchText ILIKE '%' || currentTag || '%' " +
                            "AND (length(:searchText) = length(currentTag) " +
                            "OR :searchText ILIKE currentTag || ' %' " +
                            "OR :searchText ILIKE '% ' || currentTag " +
                            "OR :searchText ILIKE '% ' || currentTag || ' %')" +
                    "))))"
    )
    Page<WidgetTypeInfoEntity> findTenantWidgetTypesByTenantId(@Param("tenantId") UUID tenantId,
                                                               @Param("searchText") String searchText,
                                                               @Param("fullSearch") boolean fullSearch,
                                                               @Param("deprecatedFilterEnabled") boolean deprecatedFilterEnabled,
                                                               @Param("deprecatedFilter") boolean deprecatedFilter,
                                                               @Param("widgetTypesEmpty") boolean widgetTypesEmpty,
                                                               @Param("widgetTypes") List<String> widgetTypes,
                                                               Pageable pageable);

    @Query("SELECT wti FROM WidgetTypeInfoEntity wti, WidgetsBundleWidgetEntity wbw " +
            "WHERE wbw.widgetsBundleId = :widgetsBundleId " +
            "AND wbw.widgetTypeId = wti.id ORDER BY wbw.widgetTypeOrder")
    List<WidgetTypeInfoEntity> findWidgetTypesInfosByWidgetsBundleId(@Param("widgetsBundleId") UUID widgetsBundleId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM widget_type_info_view wti, widgets_bundle_widget wbw " +
                    "WHERE wbw.widgets_bundle_id = :widgetsBundleId " +
                    "AND wbw.widget_type_id = wti.id " +
                    "AND ((:deprecatedFilterEnabled) IS FALSE OR wti.deprecated = :deprecatedFilter) " +
                    "AND ((:widgetTypesEmpty) IS TRUE OR wti.widget_type IN (:widgetTypes)) " +
                    "AND (wti.name ILIKE CONCAT('%', :searchText, '%') " +
                    "OR ((:fullSearch) IS TRUE AND (wti.description ILIKE CONCAT('%', :searchText, '%') " +
                    "OR EXISTS (" +
                        "SELECT 1 " +
                        "FROM unnest(wti.tags) AS currentTag " +
                        "WHERE :searchText ILIKE '%' || currentTag || '%' " +
                            "AND (length(:searchText) = length(currentTag) " +
                            "OR :searchText ILIKE currentTag || ' %' " +
                            "OR :searchText ILIKE '% ' || currentTag " +
                            "OR :searchText ILIKE '% ' || currentTag || ' %')" +
                    ")))) " +
                    "ORDER BY wbw.widget_type_order",
            countQuery = "SELECT count(*) FROM widget_type_info_view wti, widgets_bundle_widget wbw " +
                    "WHERE wbw.widgets_bundle_id = :widgetsBundleId " +
                    "AND wbw.widget_type_id = wti.id " +
                    "AND ((:deprecatedFilterEnabled) IS FALSE OR wti.deprecated = :deprecatedFilter) " +
                    "AND ((:widgetTypesEmpty) IS TRUE OR wti.widget_type IN (:widgetTypes)) " +
                    "AND (wti.name ILIKE CONCAT('%', :searchText, '%') " +
                    "OR ((:fullSearch) IS TRUE AND (wti.description ILIKE CONCAT('%', :searchText, '%') " +
                    "OR EXISTS (" +
                        "SELECT 1 " +
                        "FROM unnest(wti.tags) AS currentTag " +
                        "WHERE :searchText ILIKE '%' || currentTag || '%' " +
                            "AND (length(:searchText) = length(currentTag) " +
                            "OR :searchText ILIKE currentTag || ' %' " +
                            "OR :searchText ILIKE '% ' || currentTag " +
                            "OR :searchText ILIKE '% ' || currentTag || ' %')" +
                    "))))"
    )
    Page<WidgetTypeInfoEntity> findWidgetTypesInfosByWidgetsBundleId(@Param("widgetsBundleId") UUID widgetsBundleId,
                                                                     @Param("searchText") String searchText,
                                                                     @Param("fullSearch") boolean fullSearch,
                                                                     @Param("deprecatedFilterEnabled") boolean deprecatedFilterEnabled,
                                                                     @Param("deprecatedFilter") boolean deprecatedFilter,
                                                                     @Param("widgetTypesEmpty") boolean widgetTypesEmpty,
                                                                     @Param("widgetTypes") List<String> widgetTypes,
                                                                     Pageable pageable);


    @Query(nativeQuery = true,
            value = "SELECT * FROM widget_type_info_view wti WHERE wti.id IN " +
                    "(select id from widget_type where tenant_id = :tenantId " +
                    "and (image = :imageLink or descriptor ILIKE CONCAT('%\"', :imageLink, '\"%')) limit :lmt)"
    )
    List<WidgetTypeInfoEntity> findByTenantAndImageUrl(@Param("tenantId") UUID tenantId, @Param("imageLink") String imageLink, @Param("lmt") int lmt);

    @Query(nativeQuery = true,
            value = "SELECT * FROM widget_type_info_view wti WHERE wti.id IN " +
                    "(select id from widget_type where image = :imageLink or descriptor ILIKE CONCAT('%', :imageLink, '%') limit :lmt)"
    )
    List<WidgetTypeInfoEntity> findByImageUrl(@Param("imageLink") String imageLink, @Param("lmt") int lmt);
}
