
package org.thingsboard.server.dao.relation;

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntityRelationInfo;
import org.thingsboard.server.common.data.relation.EntityRelationsQuery;
import org.thingsboard.server.common.data.relation.RelationTypeGroup;
import org.thingsboard.server.common.data.rule.RuleChainType;

import java.util.List;

/**
 * Created by ashvayka on 27.04.17.
 */
public interface RelationService {

    ListenableFuture<Boolean> checkRelationAsync(TenantId tenantId, EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup);

    boolean checkRelation(TenantId tenantId, EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup);

    EntityRelation getRelation(TenantId tenantId, EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup);

    boolean saveRelation(TenantId tenantId, EntityRelation relation);

    void saveRelations(TenantId tenantId, List<EntityRelation> relations);

    ListenableFuture<Boolean> saveRelationAsync(TenantId tenantId, EntityRelation relation);

    boolean deleteRelation(TenantId tenantId, EntityRelation relation);

    ListenableFuture<Boolean> deleteRelationAsync(TenantId tenantId, EntityRelation relation);

    boolean deleteRelation(TenantId tenantId, EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup);

    ListenableFuture<Boolean> deleteRelationAsync(TenantId tenantId, EntityId from, EntityId to, String relationType, RelationTypeGroup typeGroup);

    void deleteEntityRelations(TenantId tenantId, EntityId entity);

    void deleteEntityCommonRelations(TenantId tenantId, EntityId entity);

    List<EntityRelation> findByFrom(TenantId tenantId, EntityId from, RelationTypeGroup typeGroup);

    ListenableFuture<List<EntityRelation>> findByFromAsync(TenantId tenantId, EntityId from, RelationTypeGroup typeGroup);

    ListenableFuture<List<EntityRelationInfo>> findInfoByFrom(TenantId tenantId, EntityId from, RelationTypeGroup typeGroup);

    List<EntityRelation> findByFromAndType(TenantId tenantId, EntityId from, String relationType, RelationTypeGroup typeGroup);

    ListenableFuture<List<EntityRelation>> findByFromAndTypeAsync(TenantId tenantId, EntityId from, String relationType, RelationTypeGroup typeGroup);

    List<EntityRelation> findByTo(TenantId tenantId, EntityId to, RelationTypeGroup typeGroup);

    ListenableFuture<List<EntityRelation>> findByToAsync(TenantId tenantId, EntityId to, RelationTypeGroup typeGroup);

    ListenableFuture<List<EntityRelationInfo>> findInfoByTo(TenantId tenantId, EntityId to, RelationTypeGroup typeGroup);

    List<EntityRelation> findByToAndType(TenantId tenantId, EntityId to, String relationType, RelationTypeGroup typeGroup);

    ListenableFuture<List<EntityRelation>> findByToAndTypeAsync(TenantId tenantId, EntityId to, String relationType, RelationTypeGroup typeGroup);

    ListenableFuture<List<EntityRelation>> findByQuery(TenantId tenantId, EntityRelationsQuery query);

    ListenableFuture<List<EntityRelationInfo>> findInfoByQuery(TenantId tenantId, EntityRelationsQuery query);

    void removeRelations(TenantId tenantId, EntityId entityId);

    List<EntityRelation> findRuleNodeToRuleChainRelations(TenantId tenantId, RuleChainType ruleChainType, int limit);

//    TODO: This method may be useful for some validations in the future
//    ListenableFuture<Boolean> checkRecursiveRelation(EntityId from, EntityId to);

}
